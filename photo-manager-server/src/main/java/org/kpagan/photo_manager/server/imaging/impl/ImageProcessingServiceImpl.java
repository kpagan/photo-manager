package org.kpagan.photo_manager.server.imaging.impl;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.hashing.HashGenerator;
import org.kpagan.photo_manager.server.hashing.HashInformation;
import org.kpagan.photo_manager.server.hashing.error.HashingException;
import org.kpagan.photo_manager.server.image.ImageMetadata;
import org.kpagan.photo_manager.server.image.ImageModel;
import org.kpagan.photo_manager.server.image.MetadataExtractor;
import org.kpagan.photo_manager.server.image.error.ImageMetadataExtractionException;
import org.kpagan.photo_manager.server.imaging.ImageDatabaseService;
import org.kpagan.photo_manager.server.imaging.ImageProcessingService;
import org.kpagan.photo_manager.server.io.FileWalker;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Service
@Slf4j
public class ImageProcessingServiceImpl implements ImageProcessingService {

    // Poison pill object to gracefully signal the DB thread that scanning is done
    private static final ImageModel NO_MORE_IMAGES = new ImageModel(null, null);

    private final ImageDatabaseService databaseService;
    private final ExecutorService dbExecutor;
    private final BlockingQueue<ImageModel> queue;
    private final ExecutorService workerPool;

    public ImageProcessingServiceImpl(ImageDatabaseService databaseService) {
        this.databaseService = databaseService;
        int cpuCores = Runtime.getRuntime().availableProcessors();
        this.workerPool = Executors.newFixedThreadPool(cpuCores);
        // Single-Threaded Executor strictly dedicated to Database Writes/Queries
        this.dbExecutor = Executors.newSingleThreadExecutor();
        // Bounded queue to prevent out-of-memory issues if hashing is faster than DB writes
        this.queue = new LinkedBlockingQueue<>(500);
    }


    @Override
    public void scanImagesUnder(String directory) throws IOException {
        // 1. Start the single-threaded DB Consumer
        Future<?> dbTask = dbExecutor.submit(() -> runDbConsumer(queue));

        try (Stream<Path> paths = FileWalker.traverseDirectory(directory)) {
            // Counter to track total tasks submitted vs completed
            AtomicLong submittedTasks = new AtomicLong(0);
            // 2. Phaser starts with 1 registered party (the main thread)
            Phaser phaser = new Phaser(1);
            // Submit tasks lazily one-by-one as the stream reads from OS
            paths.forEach(path -> {
                submittedTasks.incrementAndGet();

                // Register this photo as an active unit of work
                phaser.register();

                workerPool.submit(() -> {
                    try {
                        producePhotoData(path, queue);
                    } finally {
                        // Deregister this task when completed (even if an exception occurred)
                        phaser.arriveAndDeregister();
                    }
                });
            });

            log.info("Discovered {} photos. Waiting for processing and storing to DB to finish...", + submittedTasks.get());

            // Main thread arrives (finishes stream reading) AND deregisters its initial registration.
            // When the remaining active worker count drops to 0, this call returns instantly.
            phaser.arriveAndAwaitAdvance();

            // Guaranteed: Every single photo worker has completed!
            queue.put(NO_MORE_IMAGES);

            dbTask.get();
        } catch (ExecutionException | InterruptedException e) {
            log.error("Error while processing folder {}", directory, e);
        }
    }

    @Override
    public void processImage(Path path) {
        try {
            databaseService.processAndSave(createImageModel(path));
        } catch (ImageMetadataExtractionException | HashingException e) {
            log.error("Skipping processing file {} due to error", path, e);
        }
    }

    private ImageModel createImageModel(Path path) throws HashingException, ImageMetadataExtractionException {
        log.info("Processing image: {}", path.toString());
        HashInformation hash = HashGenerator.getHashInformation(path);
        ImageMetadata imageMetadata = MetadataExtractor.extractMetadata(path);
        log.debug("Metadata {}", imageMetadata);
        databaseService.processAndSave(new ImageModel(imageMetadata, hash));
        return new ImageModel(imageMetadata, hash);
    }

    // --- PRODUCER (Runs in parallel across multiple CPU cores) ---
    private void producePhotoData(Path path, BlockingQueue<ImageModel> queue) {
        try {
            // Blocks producer thread automatically if queue reaches capacity (500 items)
            queue.put(createImageModel(path));
        } catch (Exception e) {
            log.error("Worker failed processing {}", path.getFileName(), e);
        }
    }

    // --- CONSUMER (Runs strictly on a SINGLE dedicated thread) ---
    private void runDbConsumer(BlockingQueue<ImageModel> queue) {
        try {
            while (true) {
                ImageModel item = queue.take(); // Blocks until an item is available
                if (item == NO_MORE_IMAGES) {
                    log.info("No more image data to store to DB. Stop consuming queue...");
                    break; // Exit loop cleanly
                }
                // Execute DB transaction sequentially on a single thread—zero locking issues!
                databaseService.processAndSave(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    public void shutdown() {
        log.info("Closing Image processing service");
        try {
            if (workerPool != null) {
                workerPool.shutdown();
            }
        } catch (Exception e) {
            log.error("Failed to close workerPool", e);
        }
        try {
            if (dbExecutor != null) {
                dbExecutor.shutdown();
            }
        } catch (Exception e) {
            log.error("Failed to shutdown dbExecutor", e);
        }
    }
}
