package org.kpagan.photo_manager.server.imaging.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.hashing.HashGenerator;
import org.kpagan.photo_manager.server.image.ImageModel;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageEntity;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageRepository;
import org.kpagan.photo_manager.server.image.persistence.ImageEntity;
import org.kpagan.photo_manager.server.image.persistence.ImageRepository;
import org.kpagan.photo_manager.server.imaging.ImageDatabaseService;
import org.kpagan.photo_manager.server.imaging.ImageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageDatabaseServiceImpl implements ImageDatabaseService {

    public static final int MAX_DISTANCE = 5;

    private final ImageRepository imageRepository;
    private final DuplicateImageRepository duplicateImageRepository;
    private final ImageMapper imageMapper;

    @Transactional
    @Override
    public void processAndSave(ImageModel model) {

        // 1. Check if the file is already added in DB
        boolean alreadyExists = imageRepository.existsByAbsolutePath(model.metadata().absolutePath());
        if (alreadyExists) {
            return;
        }

        // 2. Check Exact Duplicate (SHA-256)
        List<ImageEntity> exactMatches = imageRepository.findBySha256(model.hash().sha256());
        Set<Long> exactMatchesIds = exactMatches.stream().map(ImageEntity::getId).collect(Collectors.toSet());

        // 3. Near-Duplicate Check (pHash Hamming Distance)
        List<ImageEntity> nearDuplicates = imageRepository.findByHammingDistance(model.hash().perceptualHash(), MAX_DISTANCE);
        Set<Long> nearDuplicatesIds = nearDuplicates.stream().map(ImageEntity::getId).collect(Collectors.toSet());

        // 4. Save the new image information
        ImageEntity entity = imageMapper.mapToEntity(model);
        ImageEntity saved = imageRepository.save(entity);

        // 5. Save the duplicates information
        if (!exactMatchesIds.isEmpty() || !nearDuplicatesIds.isEmpty()) {
            List<DuplicateImageEntity> duplicates = new ArrayList<>(exactMatchesIds.size() + nearDuplicatesIds.size());

            if (!exactMatchesIds.isEmpty()) {
                for (Long exactId : exactMatchesIds) {
                    duplicates.add(DuplicateImageEntity.create(saved.getId(), exactId, true));
                }
            }
            // near duplicate will always be an exact match so there is no reason to add it again
            nearDuplicatesIds.removeAll(exactMatchesIds);
            if (!nearDuplicatesIds.isEmpty()) {
                for (Long nearDuplicateId : nearDuplicatesIds) {
                    duplicates.add(DuplicateImageEntity.create(saved.getId(), nearDuplicateId, false));
                }
            }
            duplicateImageRepository.saveAll(duplicates);
        }
    }
}
