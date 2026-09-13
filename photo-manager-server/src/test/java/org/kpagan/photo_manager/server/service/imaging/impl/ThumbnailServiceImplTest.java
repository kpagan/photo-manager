package org.kpagan.photo_manager.server.service.imaging.impl;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.kpagan.photo_manager.server.config.PhotoProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ThumbnailServiceImplTest {

    @TempDir
    private static Path tempDir;
    private static ThumbnailServiceImpl thumbnailService;
    private static Path thumbnailsDir;

    @BeforeAll
    static void setUp() throws IOException {
        String photosDirectory = tempDir.toString();
        String thumbnailRelativeDirectory = "thumbnails";

        int maxWidth = 300;
        PhotoProperties photoProperties = new PhotoProperties();
        photoProperties.setDirectory(photosDirectory);
        PhotoProperties.Thumbnail thumbnail = new PhotoProperties.Thumbnail();
        thumbnail.setMaxWidth(maxWidth);
        thumbnail.setStoragePath(thumbnailRelativeDirectory);
        photoProperties.setThumbnail(thumbnail);
        photoProperties.init();

        thumbnailService = new ThumbnailServiceImpl(photoProperties);
        thumbnailService.init();
        thumbnailsDir = tempDir.resolve(thumbnailRelativeDirectory);
    }

    @Test
    @DisplayName("Should return direct thumbnail path when destination file does not exist")
    void getThumbnailPath_WhenFileDoesNotExist_ReturnsOriginalFileNameInThumbnailsDir() {
        Path sourceImage = Path.of("C:/photos/vacation/beach.jpg");

        Path result = thumbnailService.getThumbnailPath(sourceImage);

        assertEquals(thumbnailsDir.resolve("beach.jpg"), result);
        assertFalse(Files.exists(result));
    }

    @Test
    @DisplayName("Should append suffix when thumbnail file already exists in thumbnail directory")
    void getThumbnailPath_WhenFileAlreadyExists_ReturnsSuffixedFileName() throws IOException {
        Path sourceImage = Path.of("C:/photos/vacation/sunset.png");

        // Create conflicting file in thumbnails directory
        Files.createFile(thumbnailsDir.resolve("sunset.png"));

        Path result = thumbnailService.getThumbnailPath(sourceImage);

        assertEquals(thumbnailsDir.resolve("sunset_1.png"), result);
    }

    @Test
    @DisplayName("Should handle multiple collisions by resolving to next available filename")
    void getThumbnailPath_WhenMultipleCollisionsExist_ReturnsUniqueFileName() throws IOException {
        Path sourceImage = Path.of("/data/photos/mountain.jpg");

        // Simulate existing collisions
        Files.createFile(thumbnailsDir.resolve("mountain.jpg"));
        Files.createFile(thumbnailsDir.resolve("mountain_1.jpg"));

        Path result = thumbnailService.getThumbnailPath(sourceImage);

        assertEquals(thumbnailsDir.resolve("mountain_2.jpg"), result);
        assertFalse(Files.exists(result));
    }
}
