package org.kpagan.photo_manager.server.image.persistence;

import java.time.LocalDate;

public record DuplicateImagePairEntity(Long image1Id,
                                       String image1Filename,
                                       String image1AbsolutePath,
                                       Long image1FileSize,
                                       String image1Sha256,
                                       long image1PerceptualHash,
                                       LocalDate image1DateTaken,
                                       Integer image1Width,
                                       Integer image1Height,
                                       Boolean exactMatch,
                                       Long image2Id,
                                       String image2Filename,
                                       String image2AbsolutePath,
                                       Long image2FileSize,
                                       String image2Sha256,
                                       long image2PerceptualHash,
                                       LocalDate image2DateTaken,
                                       Integer image2Width,
                                       Integer image2Height) {
}
