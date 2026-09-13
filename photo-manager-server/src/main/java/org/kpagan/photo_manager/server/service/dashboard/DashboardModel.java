package org.kpagan.photo_manager.server.service.dashboard;

public record DashboardModel(String photoFolder,
                             long photosNumbers,
                             long duplicates,
                             long similarDuplicates) {
}
