package org.kpagan.photo_manager.server.web.duplicates;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateImagesModel;
import org.kpagan.photo_manager.server.service.duplicates.DuplicateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DuplicatesController {

    private final DuplicateService duplicateService;
    private final DuplicateImageDtoMapper duplicateImageDtoMapper;

    @GetMapping(path = "duplicates", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DuplicatesDto>> getDuplicates(@PageableDefault(size = 10) Pageable pageable) {
        Page<DuplicateImagesModel> duplicates = duplicateService.getDuplicates(pageable);
        return ResponseEntity.ok(duplicates.map(duplicateImageDtoMapper::mapToDto));
    }

}
