package org.kpagan.photo_manager.image.error;

public class ImageMetadataExtractionException extends Exception {
    public ImageMetadataExtractionException(String message) {
        super(message);
    }

    public ImageMetadataExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
