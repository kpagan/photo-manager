package org.kpagan.photo_manager.server.imaging.impl;

import lombok.RequiredArgsConstructor;
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
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageProcessingServiceImpl implements ImageProcessingService {

    private final ImageDatabaseService databaseService;

    @Override
    public void processImage(Path path) {
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
