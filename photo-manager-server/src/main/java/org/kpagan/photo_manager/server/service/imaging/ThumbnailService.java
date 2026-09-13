package org.kpagan.photo_manager.server.service.imaging;

import java.nio.file.Path;

public interface ThumbnailService {
    Path generateThumbnail(Path imageAbsolutePath);
}
