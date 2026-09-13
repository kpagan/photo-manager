package org.kpagan.photo_manager.server.event.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.service.imaging.ImageProcessingService;
import org.kpagan.photo_manager.server.service.imaging.PhotoPathService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileAddedEventHandler {

    private final ImageProcessingService imageProcessingService;
    private final PhotoPathService photoPathService;

    @EventListener
    public void handleNewFileAdded(FileAddedEvent event) {
        Path path = event.path();
        if (!photoPathService.isProcessablePhoto(path)) {
            log.info("Skipping event for path: {}", path);
            return;
        }
        imageProcessingService.processImage(path);
    }
}
