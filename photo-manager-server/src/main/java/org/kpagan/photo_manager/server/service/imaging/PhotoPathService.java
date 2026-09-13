package org.kpagan.photo_manager.server.service.imaging;

import java.nio.file.Path;

public interface PhotoPathService {
    Path getPhotosPath();

    boolean isThumbnailPath(Path path);

    boolean isSupportedImage(Path path);

    boolean isProcessablePhoto(Path path);
}
