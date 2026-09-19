package org.kpagan.photo_manager.server.service.scan.impl;

import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.service.imaging.ImageProcessingService;
import org.kpagan.photo_manager.server.service.imaging.ScanResponseModel;
import org.kpagan.photo_manager.server.service.scan.ScanFolderService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ScanFolderServiceImpl implements ScanFolderService {

    private final String scanDirectory;
    private final ImageProcessingService imageProcessingService;
    private final ExecutorService singleThreadExecutor;

    public ScanFolderServiceImpl(@Value("${photo.config.directory}") String scanDirectory,
                                 ImageProcessingService imageProcessingService) {
        this.scanDirectory = scanDirectory;
        this.imageProcessingService = imageProcessingService;
        this.singleThreadExecutor = Executors.newSingleThreadExecutor(new CustomizableThreadFactory("scan-executor"));
    }

    @Override
    public void scan() {
        if (!StringUtils.hasText(scanDirectory)) {
            String msg = "Photos directory is not configured. Can't scan for photos";
            throw new IllegalArgumentException(msg);
        }

        try {
            singleThreadExecutor.execute(() -> {
                try {
                    log.info("Scanning directory: {}", scanDirectory);
                    imageProcessingService.scanImagesUnder(scanDirectory);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            log.error("Failure scanning folder {}", scanDirectory, e);
        }
    }

    @Override
    public ScanResponseModel getScanStatus() {
        return imageProcessingService.getScanningStatus();
    }
}
