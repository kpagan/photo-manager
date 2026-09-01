package org.kpagan.photo_manager.server.service.imaging;

import java.nio.file.Path;

public record StreamingResourceModel(Path path, long contentLength, StreamWriter streamWriter) {
}
