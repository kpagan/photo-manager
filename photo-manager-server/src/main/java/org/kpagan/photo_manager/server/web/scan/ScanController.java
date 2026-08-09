package org.kpagan.photo_manager.server.web.scan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kpagan.photo_manager.server.service.scan.ScanFolderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ScanController {

    private final ScanFolderService scanFolderService;

    @PostMapping(path = "/scan")
    public ResponseEntity<String> scanFolder() {
        scanFolderService.scan();
        return ResponseEntity.accepted().body("Initiated folder scanning...");
    }
}
