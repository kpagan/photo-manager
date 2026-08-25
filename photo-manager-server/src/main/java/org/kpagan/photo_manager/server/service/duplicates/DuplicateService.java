package org.kpagan.photo_manager.server.service.duplicates;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DuplicateService {
    Page<DuplicateImagesModel> getDuplicates(Pageable pageable);
}
