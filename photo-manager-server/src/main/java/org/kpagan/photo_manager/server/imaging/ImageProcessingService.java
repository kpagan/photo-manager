package org.kpagan.photo_manager.server.imaging;

import java.nio.file.Path;

public interface ImageProcessingService {

    void processImage(Path path);
}
