package org.kpagan.photo_manager.server.imaging;

import org.kpagan.photo_manager.server.image.ImageModel;

public interface ImageDatabaseService {

    void processAndSave(ImageModel model);

}
