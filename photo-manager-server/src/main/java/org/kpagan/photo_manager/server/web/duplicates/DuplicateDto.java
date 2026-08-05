package org.kpagan.photo_manager.server.web.duplicates;

import org.jspecify.annotations.Nullable;

import java.time.LocalDate;

public record DuplicateDto(Long id,
                           String filename,
                           String absolutePath,
                           Long fileSize,
                           String sha256,
                           long perceptualHash,
                           LocalDate dateTaken,
                           Integer width,
                           Integer height,
                           @Nullable Boolean exactMatch) {
}
