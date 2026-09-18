package org.kpagan.photo_manager.server.web.scan;

import org.kpagan.photo_manager.server.service.imaging.ScanResponseModel;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ScanDtoMapper {

    ScanDto mapToDto(ScanResponseModel model);
}
