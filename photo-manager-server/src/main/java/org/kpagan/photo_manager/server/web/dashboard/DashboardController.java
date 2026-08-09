package org.kpagan.photo_manager.server.web.dashboard;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.service.dashboard.DashBoardService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {

    private final DashBoardService dashBoardService;
    private final DashboardDtoMapper dashboardDtoMapper;

    @GetMapping(path = "dashboard", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardDto> getDashBoardInfo() {
        return ResponseEntity.ok(dashboardDtoMapper.mapToDto(dashBoardService.getDashboardInfo()));
    }

}
