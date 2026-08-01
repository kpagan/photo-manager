package org.kpagan.photo_manager.service.imaging;

import org.kpagan.photo_manager.image.ImageModel;

public interface ImageDatabaseService {

    void processAndSave(ImageModel model);

}
