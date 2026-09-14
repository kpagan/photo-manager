package org.kpagan.photo_manager.server.web.dashboard;

public record DashboardDto(String photoFolder,
                           long photosNumbers,
                           long duplicates,
                           long similarDuplicates) {
}
