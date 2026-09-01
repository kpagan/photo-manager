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
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Override
    public StreamingResourceModel getImageStream(Long imageId) {
        Optional<ImageEntity> optionalImage = imageRepository.findById(imageId);
        if (optionalImage.isEmpty()) {
            throw new EntityNotFoundException("Not found image with id: " + imageId);
        }
        ImageEntity image = optionalImage.get();
        StreamWriter streamWriter = outputStream -> {
            try (FileInputStream fis = new FileInputStream(image.getAbsolutePath());
                 BufferedInputStream bis = new BufferedInputStream(fis)) {
                byte[] buffer = new byte[1024];
                while (bis.read(buffer) != -1) {
                    outputStream.write(buffer);
                }
            } catch (IOException e) {
                throw new ImageLoadException(
                        String.format("Error while loading image with id: %d, absolute path: %s ",
                                imageId,
                                image.getAbsolutePath()),
                        e);
            } finally {
                outputStream.flush();
            }
        };

        Path path = Paths.get(image.getAbsolutePath());
        long filesize;
        try {
            filesize = Files.size(path);
        } catch (IOException e) {
            throw new ImageLoadException("Error while calculating filesize for file", e);
        }
        String filename = image.getFilename();
        return new StreamingResourceModel(getMediaType(filename),
                filename,
                filesize,
                streamWriter);
    }

    private static String getMediaType(String filename) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        return fileNameMap.getContentTypeFor(filename);
    }
}
