package org.kpagan.photo_manager.server.service.scan.impl;

import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.service.imaging.ImageProcessingService;
import org.kpagan.photo_manager.server.service.scan.ScanFolderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Service
@Slf4j
public class ScanFolderServiceImpl implements ScanFolderService {

    private final String scanDirectory;
    private final ImageProcessingService imageProcessingService;
    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    public ScanFolderServiceImpl(@Value("${photo.config.directory}") String scanDirectory,
                                 ImageProcessingService imageProcessingService) {
        this.scanDirectory = scanDirectory;
        this.imageProcessingService = imageProcessingService;
        this.simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("scan-executor");
    }

    @Override
    public void scan() {
        if (!StringUtils.hasText(scanDirectory)) {
            String msg = "Photos directory is not configured. Can't scan for photos";
            throw new IllegalArgumentException(msg);
        }

        try {
            log.info("Scanning directory: {}", scanDirectory);
            simpleAsyncTaskExecutor.execute(() -> {
                try {
                    imageProcessingService.scanImagesUnder(scanDirectory);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failure scanning folder {}", scanDirectory, e);
        }
    }
}
