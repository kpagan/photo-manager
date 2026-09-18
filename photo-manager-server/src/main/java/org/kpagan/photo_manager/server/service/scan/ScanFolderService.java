package org.kpagan.photo_manager.server.service.scan;

import org.kpagan.photo_manager.server.service.imaging.ScanResponseModel;

public interface ScanFolderService {
    void scan();

    ScanResponseModel getScanStatus();
}
