package org.kpagan.photo_manager.server.service.dashboard.impl;

import org.kpagan.photo_manager.server.image.persistence.DuplicateImageRepository;
import org.kpagan.photo_manager.server.image.persistence.ImageRepository;
import org.kpagan.photo_manager.server.service.dashboard.DashBoardService;
import org.kpagan.photo_manager.server.service.dashboard.DashboardModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashBoardService {

    private final String scanDirectory;
    private final DuplicateImageRepository duplicateImageRepository;
    private final ImageRepository imageRepository;

    public DashboardServiceImpl(@Value("${photo.config.directory}") String scanDirectory,
                                DuplicateImageRepository duplicateImageRepository,
                                ImageRepository imageRepository) {
        this.scanDirectory = scanDirectory;
        this.duplicateImageRepository = duplicateImageRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public DashboardModel getDashboardInfo() {
        long photosNumbers = imageRepository.count();
        long duplicates = duplicateImageRepository.countByExactMatch(true);
        long similarDuplicates = duplicateImageRepository.countByExactMatch(false);
        return new DashboardModel(scanDirectory,
                photosNumbers,
                duplicates,
                similarDuplicates);
    }
}
