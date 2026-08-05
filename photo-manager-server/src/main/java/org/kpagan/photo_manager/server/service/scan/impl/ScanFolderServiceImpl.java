package org.kpagan.photo_manager.server.service.scan.impl;

import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.service.imaging.ImageProcessingService;
import org.kpagan.photo_manager.server.service.scan.ScanFolderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ScanFolderServiceImpl implements ScanFolderService {

    private final List<String> scanDirectories;
    private final ImageProcessingService imageProcessingService;
    private final SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    public ScanFolderServiceImpl(@Value("${photo.config.directories}") List<String> scanDirectories,
                                 ImageProcessingService imageProcessingService) {
        this.scanDirectories = scanDirectories;
        this.imageProcessingService = imageProcessingService;
        this.simpleAsyncTaskExecutor = new SimpleAsyncTaskExecutor("scan-executor");
    }

    @Override
    public void scan() {
        if (CollectionUtils.isEmpty(scanDirectories)) {
            String msg = "Photos directory is not configured. Can't scan for photos";
            throw new IllegalArgumentException(msg);
        }

        for (String dir : scanDirectories) {
            try {
                log.info("Scanning directory: {}", dir);
                simpleAsyncTaskExecutor.execute(() -> {
                    try {
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
