package org.kpagan.photo_manager.server.image;

import org.kpagan.photo_manager.server.hashing.HashInformation;

public record ImageModel(ImageMetadata metadata, HashInformation hash) {
}
