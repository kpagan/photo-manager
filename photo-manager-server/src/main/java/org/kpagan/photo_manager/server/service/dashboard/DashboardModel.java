package org.kpagan.photo_manager.server.service.dashboard;

import java.util.List;

public record DashboardModel(List<String> photoFolders,
                             long photosNumbers,
                             long duplicates,
                             long similarDuplicates) {
}
