package org.kpagan.photo_manager.server.web.duplicates;

import org.kpagan.photo_manager.server.service.duplicates.DuplicateImageModel;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateImagesModel;
import org.mapstruct.Mapper;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DuplicateImageDtoMapper {

    DuplicateDto mapToDto(DuplicateImageModel model);

    List<DuplicatesDto> mapToDto(List<DuplicateImagesModel> model);

}
