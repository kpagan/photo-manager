package org.kpagan.photo_manager.server.web.scan;

public record ScanDto(boolean running,
                      Long numberOfPhotos) {
}
