package org.kpagan.photo_manager.server.service.imaging.error;

public class ImageLoadException extends RuntimeException {
    public ImageLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
