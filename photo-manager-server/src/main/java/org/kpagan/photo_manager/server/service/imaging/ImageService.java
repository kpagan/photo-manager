package org.kpagan.photo_manager.server.service.imaging;

public interface ImageService {
    StreamingResourceModel getImageStream(Long imageId);
}
