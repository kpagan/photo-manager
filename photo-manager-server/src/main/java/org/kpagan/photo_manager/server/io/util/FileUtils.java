package org.kpagan.photo_manager.server.io.util;

import lombok.experimental.UtilityClass;

import java.nio.file.Path;

@UtilityClass
public class FileUtils {

    public static String getFileExtension(Path path) {
        if (path == null) {
            return "";
        }
        String filename = path.toFile().getName();
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex >= 0) {
            return filename.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}
