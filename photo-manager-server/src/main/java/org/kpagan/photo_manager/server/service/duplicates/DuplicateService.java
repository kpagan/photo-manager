package org.kpagan.photo_manager.server.service.duplicates;

import java.util.List;

public interface DuplicateService {
    List<DuplicateImagesModel> getDuplicates();
}
