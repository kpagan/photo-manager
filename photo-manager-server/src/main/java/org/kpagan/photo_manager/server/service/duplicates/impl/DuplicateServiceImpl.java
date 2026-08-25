package org.kpagan.photo_manager.server.service.duplicates.impl;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImagePairEntity;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageRepository;
import org.kpagan.photo_manager.server.service.duplicates.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DuplicateServiceImpl implements DuplicateService {

    private final DuplicateImageRepository duplicateImageRepository;
    private final DuplicateImageModelMapper duplicateImageModelMapper;

    @Override
    public Page<DuplicateImagesModel> getDuplicates(Pageable pageable) {
        Page<Long> image1IdsPage = duplicateImageRepository.findDistinctImage1Ids(pageable);
        if (image1IdsPage.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, image1IdsPage.getTotalElements());
        }

        List<DuplicateImagePairEntity> duplicates = duplicateImageRepository.findDuplicatesByImage1Ids(image1IdsPage.getContent());
        List<DuplicateImagePairModel> imagePairModels = duplicates.stream().map(duplicateImageModelMapper::mapToModel).toList();
        Map<DuplicateImageModel, List<DuplicateImageModel>> duplicatesForImage1 = imagePairModels.stream()
                .collect(Collectors.groupingBy(
                        DuplicateImagePairModel::image1,
                        LinkedHashMap::new,
                        Collectors.mapping(DuplicateImagePairModel::image2, Collectors.toList())));
        List<DuplicateImagesModel> duplicateImagesModels = new ArrayList<>(duplicatesForImage1.size());
        for (var duplicateEntry : duplicatesForImage1.entrySet()) {
            List<DuplicateImageModel> duplicatesForImage = new ArrayList<>();
            duplicatesForImage.add(duplicateEntry.getKey());
            duplicatesForImage.addAll(duplicateEntry.getValue());
            duplicateImagesModels.add(new DuplicateImagesModel(duplicatesForImage));
        }
        return new PageImpl<>(duplicateImagesModels, pageable, image1IdsPage.getTotalElements());
    }
}
