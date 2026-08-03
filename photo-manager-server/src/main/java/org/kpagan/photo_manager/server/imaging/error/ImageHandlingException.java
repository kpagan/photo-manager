package org.kpagan.photo_manager.server.imaging.error;

public class ImageHandlingException extends RuntimeException {
    public ImageHandlingException(String message) {
        super(message);
    }

    public ImageHandlingException(String message, Throwable cause) {
        super(message, cause);
    }
}
