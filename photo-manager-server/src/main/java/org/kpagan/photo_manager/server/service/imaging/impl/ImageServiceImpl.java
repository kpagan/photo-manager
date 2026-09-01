package org.kpagan.photo_manager.server.service.imaging.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.image.persistence.ImageEntity;
import org.kpagan.photo_manager.server.image.persistence.ImageRepository;
import org.kpagan.photo_manager.server.service.imaging.ImageService;
import org.kpagan.photo_manager.server.service.imaging.StreamWriter;
import org.kpagan.photo_manager.server.service.imaging.StreamingResourceModel;
import org.kpagan.photo_manager.server.service.imaging.error.ImageLoadException;
import org.springframework.stereotype.Service;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public StreamingResourceModel getImageStream(Long imageId) {
        ImageEntity image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Not found image with id: " + imageId));

        Path path = Paths.get(image.getAbsolutePath());
        if (!Files.exists(path)) {
            throw new ImageLoadException("Image file does not exist at path: " + image.getAbsolutePath());
        }

        long filesize;
        try {
            filesize = Files.size(path);
        } catch (IOException e) {
            throw new ImageLoadException("Error while calculating filesize for file", e);
        }

        StreamWriter streamWriter = outputStream -> {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(image.getAbsolutePath()))) {
                bis.transferTo(outputStream);
            } catch (IOException e) {
                throw new ImageLoadException(
                        String.format("Error while loading image with id: %d, absolute path: %s",
                                imageId,
                                image.getAbsolutePath()),
                        e);
            } finally {
                outputStream.flush();
            }
        };

        return new StreamingResourceModel(path, filesize, streamWriter);
    }
}
