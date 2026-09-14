package org.kpagan.photo_manager.server.service.duplicates;

import java.time.LocalDate;

public record DuplicateImageModel(Long id,
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
