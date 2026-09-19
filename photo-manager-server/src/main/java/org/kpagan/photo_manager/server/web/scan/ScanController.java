package org.kpagan.photo_manager.server.web.scan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.service.imaging.ScanResponseModel;
import org.kpagan.photo_manager.server.service.scan.ScanFolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ScanController {

    private final ScanFolderService scanFolderService;
    private final ScanDtoMapper mapper;

    @PostMapping(path = "/scan")
    public ResponseEntity<String> scanFolder() {
        scanFolderService.scan();
        return ResponseEntity.accepted().body("Initiated folder scanning...");
    }

    @GetMapping(path = "/scanStatus")
    public ResponseEntity<ScanDto> scanStatus() {
        ScanResponseModel scanStatus = scanFolderService.getScanStatus();
        return ResponseEntity.ok().body(mapper.mapToDto(scanStatus));
    }
}
