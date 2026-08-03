package org.kpagan.photo_manager.server.event.file;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.imaging.ImageProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Set;

@Component
@Slf4j
@RequiredArgsConstructor
public class FileAddedEventHandler {

    @Value("${photo.config.allowedExtensions}")
    private Set<String> allowedExtensions;

    private final ImageProcessingService imageProcessingService;

    @EventListener
    public void handleNewFileAdded(FileAddedEvent event) {
        Path path = event.path();
        String filename = path.toFile().getName();
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            log.info("Skipping added file {} because it is not supported", filename);
            return;
        }
        imageProcessingService.processImage(path);
    }
}
