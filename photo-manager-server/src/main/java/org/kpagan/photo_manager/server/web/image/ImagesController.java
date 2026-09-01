package org.kpagan.photo_manager.server.web.image;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.kpagan.photo_manager.server.service.imaging.ImageService;
import org.kpagan.photo_manager.server.service.imaging.StreamingResourceModel;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@RestController
@RequiredArgsConstructor
public class ImagesController {

    private final ImageService imageService;

    @GetMapping(path = "image/{imageId}")
    public ResponseEntity<StreamingResponseBody> getImage(@PathVariable(value = "imageId") Long imageId) {
        StreamingResourceModel streamingResourceModel = imageService.getImageStream(imageId);
        StreamingResponseBody responseBody = streamingResourceModel.streamWriter()::writeTo;
        Path path = streamingResourceModel.path();
        String filename = path.toFile().getName();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(getMediaType(path, filename)))
                .contentLength(streamingResourceModel.contentLength())
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.inline()
                        .filename(filename)
                        .build()
                        .toString())
                .body(responseBody);
    }

    private static String getMediaType(Path path, String filename) {
        try {
            String probed = Files.probeContentType(path);
            if (probed != null) {
                return probed;
            }
        } catch (IOException ignored) {
        }
        return MediaTypeFactory.getMediaType(filename)
                .map(MediaType::toString)
                .orElse(MediaType.APPLICATION_OCTET_STREAM_VALUE);
    }
}
