package org.kpagan.photo_manager.server.scan;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.kpagan.photo_manager.server.imaging.ImageProcessingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
public class ScanFolderRunner implements ApplicationRunner {

    private final List<String> scanDirectories;
    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;
    private final ImageProcessingService imageProcessingService;

    public ScanFolderRunner(@Value("${photo.config.directories}") List<String> scanDirectories,
                            ImageProcessingService imageProcessingService) {
        this.scanDirectories = scanDirectories;
        this.imageProcessingService = imageProcessingService;
        this.simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor();
    }

    @Override
    public void run(@NonNull ApplicationArguments args) {
        if (CollectionUtils.isEmpty(scanDirectories)) {
            log.warn("Watch directory is not configured. Can't watch when new files are added");
            return;
        }

        for (String dir : scanDirectories) {
            try {
                log.info("Scanning directory: {}", dir);
                simpleAsyncTaskExecutor.execute(() -> {
                    try {
                        // TODO: replace this runner by a rest endpoint
                        imageProcessingService.scanImagesUnder(dir);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                log.error("Failure scanning folder {}", dir, e);
            }
        }
    }
}
