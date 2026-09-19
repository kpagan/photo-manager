package org.kpagan.photo_manager.server.service.imaging;

import org.kpagan.photo_manager.server.hashing.error.HashingException;

import java.io.IOException;
import java.nio.file.Path;

public interface ThumbnailService {
    Path generateThumbnail(Path imageAbsolutePath) throws HashingException, IOException;
}
