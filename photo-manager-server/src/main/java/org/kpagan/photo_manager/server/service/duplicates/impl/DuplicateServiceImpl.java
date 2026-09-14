package org.kpagan.photo_manager.server.service.duplicates.impl;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.image.persistence.DuplicateGroupMappingsRepository;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageEntity;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageGroupRepository;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateImageModel;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateImageModelMapper;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateImagesModel;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DuplicateServiceImpl implements DuplicateService {

    private final DuplicateImageGroupRepository duplicateImageGroupRepository;
    private final DuplicateGroupMappingsRepository duplicateGroupMappingsRepository;
    private final DuplicateImageModelMapper duplicateImageModelMapper;

    @Override
    public Page<DuplicateImagesModel> getDuplicates(Pageable pageable) {
        Page<Long> groupsPage = duplicateImageGroupRepository.getGroupIds(pageable);
        if (groupsPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, groupsPage.getTotalElements());
        }

        List<DuplicateImageEntity> duplicates = duplicateGroupMappingsRepository.findByByGroupIds(groupsPage.getContent());
        List<DuplicateImageModel> duplicateImageModels = duplicates.stream().map(duplicateImageModelMapper::mapToModel).toList();
        Map<Long, List<DuplicateImageModel>> duplicatesByGroup = duplicateImageModels.stream()
                .collect(Collectors.groupingBy(DuplicateImageModel::groupId));

        List<DuplicateImagesModel> duplicateImagesModels = new ArrayList<>(duplicatesByGroup.size());
        for (var duplicateEntry : duplicatesByGroup.entrySet()) {
            List<DuplicateImageModel> duplicatesForImage = new ArrayList<>(duplicateEntry.getValue());
            duplicateImagesModels.add(new DuplicateImagesModel(duplicatesForImage));
        }
        return new PageImpl<>(duplicateImagesModels, pageable, groupsPage.getTotalElements());
    }
}
