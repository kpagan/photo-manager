package org.kpagan.photo_manager.server.service.imaging.impl;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.config.PhotoProperties;
import org.kpagan.photo_manager.server.io.util.FileUtils;
import org.kpagan.photo_manager.server.service.imaging.PhotoPathService;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class PhotoPathServiceImpl implements PhotoPathService {

    private final PhotoProperties photoProperties;

    @Override
    public Path getPhotosPath() {
        return photoProperties.getPhotosPath();
    }

    @Override
    public boolean isThumbnailPath(Path path) {
        Path normalized = path.toAbsolutePath().normalize();
        return normalized.startsWith(photoProperties.getThumbnailsPath());
    }

    @Override
    public boolean isSupportedImage(Path path) {
        if (Files.isDirectory(path)) return false;
        String ext = FileUtils.getFileExtension(path);
        return photoProperties.getAllowedExtensions().contains(ext.toLowerCase());
    }

    @Override
    public boolean isProcessablePhoto(Path path) {
        return !isThumbnailPath(path) && isSupportedImage(path);
    }
}