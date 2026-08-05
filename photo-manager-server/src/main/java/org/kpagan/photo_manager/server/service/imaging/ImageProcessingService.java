package org.kpagan.photo_manager.server.service.imaging;

import java.io.IOException;
import java.nio.file.Path;

public interface ImageProcessingService {

    void scanImagesUnder(String directory) throws IOException;

    void processImage(Path path);
}
