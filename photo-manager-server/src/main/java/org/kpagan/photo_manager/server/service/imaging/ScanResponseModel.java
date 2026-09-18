package org.kpagan.photo_manager.server.service.imaging;

public record ScanResponseModel(boolean running,
                                Long numberOfPhotos) {
}
