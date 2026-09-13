package org.kpagan.photo_manager.server.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "photo.config")
public class PhotoProperties {
    private String directory;
    private Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png", "heic", "webp", "gif");
    private Thumbnail thumbnail = new Thumbnail();

    private Path photosPath;
    private Path thumbnailsPath;

    @Getter
    @Setter
    public static class Thumbnail {
        private int maxWidth = 300;
        private String storagePath = ".thumbnails";
    }

    @PostConstruct
    public void init() {
        if (directory != null && !directory.isBlank()) {
            this.photosPath = Paths.get(directory).toAbsolutePath().normalize();
            validatePath(this.photosPath);
            Path thumbPath = Paths.get(thumbnail.getStoragePath());
            this.thumbnailsPath = thumbPath.isAbsolute()
                    ? thumbPath.normalize()
                    : this.photosPath.resolve(thumbPath).normalize();
        } else {
            throw new IllegalStateException("Photos directory is not set!!! Can't initialize server");
        }
    }

    private static void validatePath(Path path) {
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("Path %s does not exist".formatted(path.toString()));
        }
        if (!path.toFile().isDirectory()) {
            throw new IllegalArgumentException("Path %s is not a directory".formatted(path.toString()));
        }
    }
}