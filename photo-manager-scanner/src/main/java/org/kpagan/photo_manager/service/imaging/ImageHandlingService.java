package org.kpagan.photo_manager.service.imaging;

import java.io.IOException;

public interface ImageHandlingService {

    void handleImagesUnder(String directory) throws IOException;
}
