package org.kpagan.photo_manager.server.service.imaging.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.image.ImageModel;
import org.kpagan.photo_manager.server.image.persistence.DuplicateGroupMappingsEntity;
import org.kpagan.photo_manager.server.image.persistence.DuplicateGroupMappingsRepository;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageGroupEntity;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageGroupRepository;
import org.kpagan.photo_manager.server.image.persistence.ImageEntity;
import org.kpagan.photo_manager.server.image.persistence.ImageRepository;
import org.kpagan.photo_manager.server.service.imaging.ImageDatabaseService;
import org.kpagan.photo_manager.server.service.imaging.ImageMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageDatabaseServiceImpl implements ImageDatabaseService {

    public static final int MAX_DISTANCE = 5;

    private final ImageRepository imageRepository;
    private final DuplicateImageGroupRepository duplicateImageGroupRepository;
    private final DuplicateGroupMappingsRepository duplicateGroupMappingsRepository;
    private final ImageMapper imageMapper;

    @Transactional
    @Override
    public void processAndSave(ImageModel model) {

        // 1. Check if the file is already added in DB
        boolean alreadyExists = imageRepository.existsByAbsolutePath(model.metadata().absolutePath());
        if (alreadyExists) {
            return;
        }

        // 2. Save the new image information
        ImageEntity entity = imageMapper.mapToEntity(model);
        ImageEntity saved = imageRepository.save(entity);

        // 3. check if there are existing groups first
        boolean addedToExistingGroup = checkAndAddPhotoToExistingDuplicateGroups(saved.getId(), saved.getSha256(), saved.getPerceptualHash());

        // 4. If not added to existing
        if (!addedToExistingGroup) {
            checkForDuplicatesAndCreateNewGroupAndSave(saved);
        }
    }

    private void checkForDuplicatesAndCreateNewGroupAndSave(ImageEntity imageEntity) {
        // Check Exact Duplicate (SHA-256)
        List<ImageEntity> exactMatches = imageRepository.findBySha256(imageEntity.getSha256());
        Set<Long> exactMatchesIds = exactMatches.stream().map(ImageEntity::getId).collect(Collectors.toCollection(TreeSet::new));

        // Near-Duplicate Check (pHash Hamming Distance)
        List<ImageEntity> nearDuplicates = imageRepository.findByHammingDistance(imageEntity.getPerceptualHash(), MAX_DISTANCE);
        Set<Long> nearDuplicatesIds = nearDuplicates.stream().map(ImageEntity::getId).collect(Collectors.toCollection(TreeSet::new));

        boolean hasMoreThanOneExactMatches = hasMoreThanOne(exactMatchesIds);
        boolean hasMoreThanOneNearDuplicates = hasMoreThanOne(nearDuplicatesIds);
        if (hasMoreThanOneExactMatches || hasMoreThanOneNearDuplicates) {
            // Create new Duplicates group
            DuplicateImageGroupEntity newGroup = createAndSaveDuplicateImageGroup(imageEntity);

            List<DuplicateGroupMappingsEntity> duplicates = new ArrayList<>(exactMatchesIds.size() + nearDuplicatesIds.size());

            if (hasMoreThanOneExactMatches) {
                for (Long exactId : exactMatchesIds) {
                    duplicates.add(DuplicateGroupMappingsEntity.create(newGroup.getId(), exactId, true));
                }
            }
            // near duplicate will always be an exact match so there is no reason to add it again
            nearDuplicatesIds.removeAll(exactMatchesIds);
            if (hasMoreThanOneNearDuplicates && !nearDuplicatesIds.isEmpty()) {
                for (Long nearDuplicateId : nearDuplicatesIds) {
                    duplicates.add(DuplicateGroupMappingsEntity.create(newGroup.getId(), nearDuplicateId, false));
                }
            }
            // Save the duplicates information
            duplicateGroupMappingsRepository.saveAll(duplicates);
        }
    }

    private DuplicateImageGroupEntity createAndSaveDuplicateImageGroup(ImageEntity saved) {
        DuplicateImageGroupEntity newGroup = new DuplicateImageGroupEntity();
        newGroup.setSha256(saved.getSha256());
        newGroup.setPerceptualHash(saved.getPerceptualHash());
        return duplicateImageGroupRepository.save(newGroup);
    }

    private boolean checkAndAddPhotoToExistingDuplicateGroups(Long newPhotoId, String sha256, long perceptualHash) {
        boolean addedToExistingGroup = false;
        DuplicateImageGroupEntity groupEntity = duplicateImageGroupRepository.findBySha256(sha256);
        if (groupEntity != null) {
            DuplicateGroupMappingsEntity mapping = DuplicateGroupMappingsEntity.create(groupEntity.getId(), newPhotoId, true);
            duplicateGroupMappingsRepository.save(mapping);
            addedToExistingGroup = true;
        }

        List<DuplicateImageGroupEntity> nearDuplicateGroups = duplicateImageGroupRepository.findByHammingDistance(perceptualHash, MAX_DISTANCE);
        if (!nearDuplicateGroups.isEmpty()) {
            Set<Long> nearDuplicatesGroupIds = nearDuplicateGroups.stream().map(DuplicateImageGroupEntity::getId).collect(Collectors.toCollection(TreeSet::new));
            List<DuplicateGroupMappingsEntity> duplicates = new ArrayList<>(nearDuplicatesGroupIds.size());
            for (Long nearDuplicateId : nearDuplicatesGroupIds) {
                duplicates.add(DuplicateGroupMappingsEntity.create(nearDuplicateId, newPhotoId, false));
            }
            duplicateGroupMappingsRepository.saveAll(duplicates);
            addedToExistingGroup = true;
        }
        return addedToExistingGroup;
    }

    private static <T> boolean hasMoreThanOne(Collection<T> col) {
        return col != null && !col.isEmpty() && col.size() > 1;
    }
}
