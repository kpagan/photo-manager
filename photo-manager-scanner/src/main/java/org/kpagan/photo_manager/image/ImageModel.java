package org.kpagan.photo_manager.image;

import org.kpagan.photo_manager.hashing.HashInformation;

public record ImageModel(ImageMetadata metadata, HashInformation hash) {
}
