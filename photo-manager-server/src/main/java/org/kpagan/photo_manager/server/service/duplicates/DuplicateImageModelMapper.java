package org.kpagan.photo_manager.server.service.duplicates;

import org.kpagan.photo_manager.server.image.persistence.DuplicateImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DuplicateImageModelMapper {

    @Mapping(source = "entity.photoId", target = "id")
    DuplicateImageModel mapToModel(DuplicateImageEntity entity);
}
