package org.kpagan.photo_manager.server.event.file;

import java.nio.file.Path;

public record FileAddedEvent(Path path) {
}
