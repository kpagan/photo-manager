package org.kpagan.photo_manager.server.web.dashboard;

import java.util.List;

public record DashboardDto(List<String> photoFolders,
                           long photosNumbers,
                           long duplicates,
                           long similarDuplicates) {
}
