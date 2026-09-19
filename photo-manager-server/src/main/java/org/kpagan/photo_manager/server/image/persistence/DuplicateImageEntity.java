package org.kpagan.photo_manager.server.image.persistence;

import java.time.LocalDate;

public record DuplicateImageEntity(Long photoId,
                                   Long groupId,
                                   String filename,
                                   String absolutePath,
                                   Long fileSize,
                                   String sha256,
                                   long perceptualHash,
                                   LocalDate dateTaken,
                                   Integer width,
                                   Integer height,
                                   boolean exactMatch) {
}
