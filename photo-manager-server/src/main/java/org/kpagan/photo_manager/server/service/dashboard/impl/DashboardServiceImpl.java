package org.kpagan.photo_manager.server.service.dashboard.impl;

import org.kpagan.photo_manager.server.image.persistence.DuplicateGroupMappingsRepository;
import org.kpagan.photo_manager.server.image.persistence.ImageRepository;
import org.kpagan.photo_manager.server.service.dashboard.DashBoardService;
import org.kpagan.photo_manager.server.service.dashboard.DashboardModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DashboardServiceImpl implements DashBoardService {

    private final String scanDirectory;
    private final DuplicateGroupMappingsRepository duplicateGroupMappingsRepository;
    private final ImageRepository imageRepository;

    public DashboardServiceImpl(@Value("${photo.config.directory}") String scanDirectory,
                                DuplicateGroupMappingsRepository duplicateGroupMappingsRepository,
                                ImageRepository imageRepository) {
        this.scanDirectory = scanDirectory;
        this.duplicateGroupMappingsRepository = duplicateGroupMappingsRepository;
        this.imageRepository = imageRepository;
    }

    @Override
    public DashboardModel getDashboardInfo() {
        long photosNumbers = imageRepository.count();
        long duplicates = duplicateGroupMappingsRepository.countByExactMatch(true);
        long similarDuplicates = duplicateGroupMappingsRepository.countByExactMatch(false);
        return new DashboardModel(scanDirectory,
                photosNumbers,
                duplicates,
                similarDuplicates);
    }
}
