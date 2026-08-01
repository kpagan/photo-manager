package org.kpagan.photo_manager.image;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * size in bytes
 */
public record ImageMetadata(
        String filename,
        String absolutePath,
        Long fileSize,
        LocalDate dateTaken,
        LocalTime timeTaken,
        String cameraMake,
        String cameraModel,
        Integer width,
        Integer height,
        Double latitude,
        Double longitude
) {
    @Override
    public String toString() {
        return String.format(
                "Taken: %sT%s | Camera: %s %s | Size: %dx%d | GPS: %s, %s",
                dateTaken != null ? dateTaken : "Unknown",
                timeTaken != null ? timeTaken : "Unknown",
                cameraMake != null ? cameraMake : "Unknown",
                cameraModel != null ? cameraModel : "Unknown",
                width != null ? width : 0,
                height != null ? height : 0,
                latitude != null ? latitude : 0.0,
                longitude != null ? longitude : 0.0
        );
    }
}