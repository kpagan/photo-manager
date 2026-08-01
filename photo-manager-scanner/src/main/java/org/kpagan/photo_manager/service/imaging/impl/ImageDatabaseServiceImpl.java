package org.kpagan.photo_manager.service.imaging.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.hashing.HashGenerator;
import org.kpagan.photo_manager.image.ImageModel;
import org.kpagan.photo_manager.image.persistence.DuplicateImageEntity;
import org.kpagan.photo_manager.image.persistence.DuplicateImageRepository;
import org.kpagan.photo_manager.image.persistence.ImageEntity;
import org.kpagan.photo_manager.image.persistence.ImageRepository;
import org.kpagan.photo_manager.service.imaging.ImageDatabaseService;
import org.kpagan.photo_manager.service.imaging.ImageMapper;
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
        List<ImageEntity> nearDuplicates = getNearDuplicates(model.hash().perceptualHash());
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

    private List<ImageEntity> getNearDuplicates(long currentPHash) {
        // 1. Break target hash into 16-bit chunks
        int c1 = (int) ((currentPHash >> 48) & 0xFFFF);
        int c2 = (int) ((currentPHash >> 32) & 0xFFFF);
        int c3 = (int) ((currentPHash >> 16) & 0xFFFF);
        int c4 = (int) (currentPHash & 0xFFFF);

        // 2. Query DB - Indexed lookup returns only ~0.1% of rows!
        List<ImageEntity> candidates = imageRepository.findCandidatesByChunkBounds(c1, c2, c3, c4);

        // 3. Verify precise Hamming distance on filtered candidates
        List<ImageEntity> nearDuplicates = new ArrayList<>();
        for (ImageEntity candidate : candidates) {
            int distance = HashGenerator.calculateHammingDistance(currentPHash, candidate.getPerceptualHash());

            if (distance <= 5) {
                nearDuplicates.add(candidate);
            }
        }
        return nearDuplicates;
    }
}
