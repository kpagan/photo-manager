package org.kpagan.photo_manager.server.web.duplicates;

import java.time.LocalDate;

public record DuplicateDto(Long id,
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
