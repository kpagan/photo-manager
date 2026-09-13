package org.kpagan.photo_manager.server.service.imaging.impl;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.kpagan.photo_manager.server.config.PhotoProperties;
import org.kpagan.photo_manager.server.io.util.FileUtils;
import org.kpagan.photo_manager.server.service.imaging.ThumbnailService;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    public Path generateThumbnail(Path imageAbsolutePath) {
        try {
            Path thumbnailPath = getThumbnailPath(imageAbsolutePath);
            Thumbnails.of(imageAbsolutePath.toFile())
                    .size(photoProperties.getThumbnail().getMaxWidth(), Integer.MAX_VALUE)
                    .outputQuality(0.5)
                    .toFile(thumbnailPath.toFile());
            return thumbnailPath;
        } catch (IOException e) {
            log.error("Could not generate thumbnail for {}", imageAbsolutePath, e);
            return null;
        }
    }

    Path getThumbnailPath(Path imageAbsolutePath) {
        Path thumbnailPath = photoProperties.getThumbnailsPath().resolve(imageAbsolutePath.getFileName());
        int i = 1;
        String fileExtension = FileUtils.getFileExtension(thumbnailPath);
        String filenameWithoutExtension = FileUtils.getFilenameWithoutExtension(thumbnailPath);
        while (Files.exists(thumbnailPath)) {
            String newFilename = filenameWithoutExtension + "_" + i + "." + fileExtension;
            thumbnailPath = photoProperties.getThumbnailsPath().resolve(newFilename);
            i++;
        }
        return thumbnailPath;
    }
}