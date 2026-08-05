package org.kpagan.photo_manager.server.service.duplicates;

public record DuplicateImagePairModel(DuplicateImageModel image1,
                                      DuplicateImageModel image2,
                                      Boolean exactMatch) {
}
