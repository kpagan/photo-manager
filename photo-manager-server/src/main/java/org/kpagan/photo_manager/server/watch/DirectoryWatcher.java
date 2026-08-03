package org.kpagan.photo_manager.server.watch;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.kpagan.photo_manager.server.event.file.FileAddedEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
public class DirectoryWatcher implements Runnable, AutoCloseable {

    public static final int DELAY = 50;
    private final ApplicationEventPublisher publisher;
    private final WatchService watchService;
    private final ScheduledExecutorService executorService;
    private final Map<WatchKey, Path> keys;

    public DirectoryWatcher(ApplicationEventPublisher publisher) throws IOException {
        this.publisher = publisher;
        this.watchService = FileSystems.getDefault().newWatchService();
        this.executorService = Executors.newSingleThreadScheduledExecutor();
        this.keys = new HashMap<>();

        log.info("Directory watcher initialized...");
    }

    @SuppressWarnings("unchecked")
    static <T> WatchEvent<T> cast(WatchEvent<?> event) {
        return (WatchEvent<T>) event;
    }

    public void registerPaths(List<String> paths) throws IOException {
        for (String dir : paths) {
            Path path = Paths.get(dir);
            log.info("Scanning {} ...", dir);
            registerAll(path);
            log.info("Done.");
        }
    }

    /**
     * Register the given directory with the WatchService
     */
    private void register(Path dir) throws IOException {
        WatchKey key = dir.register(watchService, ENTRY_CREATE);
        Path prev = keys.get(key);
        if (prev == null) {
            log.debug("register: {}", dir);
        } else {
            if (!dir.equals(prev)) {
                log.info("update: {} -> {}", prev, dir);
            }
        }
        keys.put(key, dir);
    }

    /**
     * Register the given directory, and all its sub-directories, with the
     * WatchService.
     */
    private void registerAll(final Path start) throws IOException {
        // register directory and sub-directories
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(@NonNull Path dir, @NonNull BasicFileAttributes attrs) throws IOException {
                register(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Process all events for keys queued to the watcher
     */
    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {

                log.info("Waiting for files to be created ...");
                // wait for key to be signalled
                WatchKey key = watchService.take();
                log.info("Awaken...");

                Path dir = keys.get(key);
                if (dir == null) {
                    log.warn("WatchKey [{}] not recognized!!", key);
                    continue;
                }

                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();

                    // TBD - provide example of how OVERFLOW event is handled
                    if (kind == OVERFLOW) {
                        continue;
                    }

                    // Context for directory entry event is the file name of entry
                    WatchEvent<Path> ev = cast(event);
                    Path name = ev.context();
                    Path child = dir.resolve(name);

                    // print out event
                    log.info("{}: {}", event.kind().name(), child);

                    // if directory is created, and watching recursively,
                    // then register it and its sub-directories
                    if (kind == ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child, NOFOLLOW_LINKS)) {
                                registerAll(child);
                                // when a directory that already contains files is created then publish events for the contained files
                                Files.walkFileTree(child, new SimpleFileVisitor<>() {
                                    @Override
                                    public FileVisitResult visitFile(@NonNull Path file, @NonNull BasicFileAttributes attrs) throws IOException {
                                        publisher.publishEvent(new FileAddedEvent(file));
                                        return super.visitFile(file, attrs);
                                    }
                                });

                            } else {
                                // publish event only if it is an actual file, not a directory
                                executorService.schedule(() -> publisher.publishEvent(new FileAddedEvent(child)), DELAY, TimeUnit.MILLISECONDS);
                            }
                        } catch (IOException x) {
                            log.error("Error while registering new directory {} in watch directory", child, x);
                        }
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    keys.remove(key);

                    // all directories are inaccessible
                    if (keys.isEmpty()) {
                        break;
                    }
                }
            }
        } catch (InterruptedException ie) {
            log.info("DirectoryWatcher is Interrupted, bye!");
        }
    }

    @Override
    public void close() throws Exception {
        try {
            if (watchService != null) {
                watchService.close();
            }
        } catch (Exception e) {
            log.error("Failed to close WatchService", e);
        }
        try {
            if (executorService != null) {
                executorService.shutdown();
            }
        } catch (Exception e) {
            log.error("Failed to shutdown ExecutorService", e);
        }
    }
}
