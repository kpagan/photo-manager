package org.kpagan.photo_manager.server.web.image;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.service.imaging.ImageService;
import org.kpagan.photo_manager.server.service.imaging.StreamingResourceModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + streamingResourceModel.filename() +"\"")
                .contentLength(streamingResourceModel.contentLength())
                .contentType(MediaType.parseMediaType(streamingResourceModel.mediaType()))
                .body(responseBody);
    }
}
