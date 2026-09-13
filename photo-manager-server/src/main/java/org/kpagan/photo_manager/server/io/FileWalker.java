package org.kpagan.photo_manager.server.io;

import lombok.RequiredArgsConstructor;
import org.kpagan.photo_manager.server.service.imaging.PhotoPathService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FileWalker {

    private final PhotoPathService photoPathService;

    public Stream<Path> traverseDirectory(String directory) throws IOException {
        Path rootPath = Path.of(directory).toAbsolutePath().normalize();
        List<Path> discoveredPhotos = new ArrayList<>();

        Files.walkFileTree(rootPath, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                if (photoPathService.isThumbnailPath(dir)) {
                    return FileVisitResult.SKIP_SUBTREE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                if (attrs.isRegularFile() && photoPathService.isProcessablePhoto(file)) {
                    discoveredPhotos.add(file);
                }
                return FileVisitResult.CONTINUE;
            }
        });

        return discoveredPhotos.stream();
    }
}
