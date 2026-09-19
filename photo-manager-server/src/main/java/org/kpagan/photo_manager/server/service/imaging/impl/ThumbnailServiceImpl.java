package org.kpagan.photo_manager.server.service.imaging.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.kpagan.photo_manager.server.config.PhotoProperties;
import org.kpagan.photo_manager.server.hashing.HashGenerator;
import org.kpagan.photo_manager.server.hashing.error.HashingException;
import org.kpagan.photo_manager.server.service.imaging.ThumbnailService;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@Slf4j
@RequiredArgsConstructor
public class ThumbnailServiceImpl implements ThumbnailService {

    private final PhotoProperties photoProperties;

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(photoProperties.getThumbnailsPath());
    }

    @Override
    public Path generateThumbnail(Path imageAbsolutePath) throws HashingException, IOException {
        Path thumbnailPath = getThumbnailPath(imageAbsolutePath);
        if (Files.exists(thumbnailPath)) {
            return thumbnailPath;
        }
        try (OutputStream os = new FileOutputStream(thumbnailPath.toFile())) {
            Thumbnails.of(imageAbsolutePath.toFile())
                    .size(photoProperties.getThumbnail().getMaxWidth(), Integer.MAX_VALUE)
                    .outputQuality(0.5)
                    .toOutputStream(os);
        }
        return thumbnailPath;
    }

    private Path getThumbnailPath(Path imageAbsolutePath) throws HashingException {
        String hashedFilename = HashGenerator.calculateSHA256(imageAbsolutePath.toString());
        return photoProperties.getThumbnailsPath().resolve(hashedFilename);
    }
}