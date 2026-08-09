package org.kpagan.photo_manager.server.web.duplicates;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateImagesModel;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DuplicatesController {

    private final DuplicateService duplicateService;
    private final DuplicateImageDtoMapper duplicateImageDtoMapper;

    @GetMapping(path = "duplicates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DuplicatesDto>> getDuplicates() {
        List<DuplicateImagesModel> duplicates = duplicateService.getDuplicates();
        return ResponseEntity.ok(duplicateImageDtoMapper.mapToDto(duplicates));
    }

}
