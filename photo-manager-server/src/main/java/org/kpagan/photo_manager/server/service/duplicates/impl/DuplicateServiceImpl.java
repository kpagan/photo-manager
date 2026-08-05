package org.kpagan.photo_manager.server.service.duplicates.impl;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImagePairEntity;
import org.kpagan.photo_manager.server.image.persistence.DuplicateImageRepository;
import org.kpagan.photo_manager.server.service.duplicates.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DuplicateServiceImpl implements DuplicateService {

    private final DuplicateImageRepository duplicateImageRepository;
    private final DuplicateImageModelMapper duplicateImageModelMapper;

    @Override
    public List<DuplicateImagesModel> getDuplicates() {
        List<DuplicateImagePairEntity> duplicates = duplicateImageRepository.findDuplicates();
        List<DuplicateImagePairModel> imagePairModels = duplicates.stream().map(duplicateImageModelMapper::mapToModel).toList();
        Map<DuplicateImageModel, List<DuplicateImageModel>> duplicatesForImage1 = imagePairModels.stream()
                .collect(Collectors.groupingBy(
                        DuplicateImagePairModel::image1,
                        Collectors.mapping(DuplicateImagePairModel::image2, Collectors.toList())));
        List<DuplicateImagesModel> duplicateImagesModels = new ArrayList<>(duplicatesForImage1.size());
        for (var duplicateEntry : duplicatesForImage1.entrySet()) {
            List<DuplicateImageModel> duplicatesForImage = new ArrayList<>();
            duplicatesForImage.add(duplicateEntry.getKey());
            duplicatesForImage.addAll(duplicateEntry.getValue());
            duplicateImagesModels.add(new DuplicateImagesModel(duplicatesForImage));
        }
        return duplicateImagesModels;
    }
}
