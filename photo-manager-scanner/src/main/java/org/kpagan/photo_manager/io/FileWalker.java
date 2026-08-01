package org.kpagan.photo_manager.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Stream;

public class FileWalker {

    // Supported image extensions
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "heic", "webp", "gif");

    public static Stream<Path> traverseDirectory(String directory) throws IOException {
        Path path = Path.of(directory).toAbsolutePath().normalize();
        ;
        validatePath(path);
        return Files.walk(path).filter(Files::isRegularFile) // Ignore directories themselves
                .filter(FileWalker::isImageFile);
    }

    private static void validatePath(Path path) {
        if (!path.toFile().exists()) {
            throw new IllegalArgumentException("Path %s does not exist".formatted(path.toString()));
        }
        if (!path.toFile().isDirectory()) {
            throw new IllegalArgumentException("Path %s is not a directory".formatted(path.toString()));
        }
    }

    private static boolean isImageFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) return false;

        String extension = fileName.substring(dotIndex + 1);
        return ALLOWED_EXTENSIONS.contains(extension);
    }
}
