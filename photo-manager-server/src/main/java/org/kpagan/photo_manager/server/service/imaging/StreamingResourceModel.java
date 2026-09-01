package org.kpagan.photo_manager.server.service.imaging;

public record StreamingResourceModel(String mediaType,
                                     String filename,
                                     long contentLength,
                                     StreamWriter streamWriter) {
}
