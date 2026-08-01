package org.kpagan.photo_manager.service.imaging.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.hashing.HashGenerator;
import org.kpagan.photo_manager.hashing.HashInformation;
import org.kpagan.photo_manager.hashing.error.HashingException;
import org.kpagan.photo_manager.image.ImageMetadata;
import org.kpagan.photo_manager.image.ImageModel;
import org.kpagan.photo_manager.image.MetadataExtractor;
import org.kpagan.photo_manager.image.error.ImageMetadataExtractionException;
import org.kpagan.photo_manager.io.FileWalker;
import org.kpagan.photo_manager.service.imaging.ImageDatabaseService;
import org.kpagan.photo_manager.service.imaging.ImageHandlingService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageHandlingServiceImpl implements ImageHandlingService {

    private final ImageDatabaseService databaseService;

    @Override
    public void handleImagesUnder(String directory) throws IOException {
        try (Stream<Path> paths = FileWalker.traverseDirectory(directory)) {
            paths.forEach(this::processImage);
        }
    }

    private void processImage(Path path) {
        log.info("Processing image: {}", path.toString());
        try {
            HashInformation hash = HashGenerator.getHashInformation(path);
            ImageMetadata imageMetadata = MetadataExtractor.extractMetadata(path);
            databaseService.processAndSave(new ImageModel(imageMetadata, hash));
            log.info("Metadata {}", imageMetadata);
        } catch (ImageMetadataExtractionException | HashingException e) {
            log.error("Skipping processing file {} due to error", path, e);
        }
    }
}
