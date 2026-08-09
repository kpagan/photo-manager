package org.kpagan.photo_manager.server.web.dashboard;

import org.kpagan.photo_manager.server.service.dashboard.DashboardModel;
import org.mapstruct.Mapper;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DashboardDtoMapper {

    DashboardDto mapToDto(DashboardModel model);
}
