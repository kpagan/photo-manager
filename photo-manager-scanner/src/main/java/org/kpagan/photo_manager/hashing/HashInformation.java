package org.kpagan.photo_manager.hashing;

import org.jspecify.annotations.NonNull;

public record HashInformation(@NonNull String sha256,
                              long perceptualHash) {
}
