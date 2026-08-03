package org.kpagan.photo_manager.server.image;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.GeoLocation;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import org.kpagan.photo_manager.server.image.error.ImageMetadataExtractionException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.*;
import java.util.Date;

public class MetadataExtractor {

    public static ImageMetadata extractMetadata(Path path) throws ImageMetadataExtractionException {
        try (InputStream is = Files.newInputStream(path)) {
            // Read metadata tags directly from file header (streams without loading raw pixels)
            Metadata metadata = ImageMetadataReader.readMetadata(is);

            // 1. Extract Camera Make & Model
            ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
            String make = exifIFD0 != null ? exifIFD0.getString(ExifIFD0Directory.TAG_MAKE) : null;
            String model = exifIFD0 != null ? exifIFD0.getString(ExifIFD0Directory.TAG_MODEL) : null;

            // 2. Extract Date/Time Original
            ExifSubIFDDirectory subIFD = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            LocalDate dateTaken = null;
            LocalTime timeTaken = null;
            if (subIFD != null) {
                Date date = subIFD.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
                if (date != null) {
                    dateTaken = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    timeTaken = date.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                }
            }
            if (dateTaken == null && timeTaken == null) {
                BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
                FileTime modified = attr.lastModifiedTime();
                FileTime created = attr.creationTime();
                // keep the oldest timestamp
                FileTime oldest = created.compareTo(modified) >= 0 ? modified : created;
                ZonedDateTime zonedDateTime = oldest.toInstant().atZone(ZoneId.systemDefault());
                dateTaken = zonedDateTime.toLocalDate();
                timeTaken = zonedDateTime.toLocalTime();
            }

            // 3. Extract GPS Coordinates
            GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
            Double latitude = null;
            Double longitude = null;
            if (gpsDirectory != null) {
                GeoLocation location = gpsDirectory.getGeoLocation();
                if (location != null && !location.isZero()) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
            }

            // 4. Extract Width and Height
            Integer width = null;
            Integer height = null;
            if (subIFD != null && subIFD.containsTag(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH)) {
                width = subIFD.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_WIDTH);
                height = subIFD.getInteger(ExifSubIFDDirectory.TAG_EXIF_IMAGE_HEIGHT);
            } else {
                // Fallback for JPEGs if Exif tags missing width/height
                JpegDirectory jpegDir = metadata.getFirstDirectoryOfType(JpegDirectory.class);
                if (jpegDir != null) {
                    width = jpegDir.getImageWidth();
                    height = jpegDir.getImageHeight();
                }
            }

            File file = path.getFileName().toFile();
            String filename = file.getName();
            String absolutePath = path.toAbsolutePath().toString();
            Long filesize = Files.size(path);
            return new ImageMetadata(filename, absolutePath, filesize, dateTaken, timeTaken, make, model, width, height, latitude, longitude);
        } catch (DateTimeException | ImageProcessingException | MetadataException | IOException e) {
            // Return empty record if file has no EXIF or isn't a supported image header
            throw new ImageMetadataExtractionException(String.format("Error while extracting metadata for file %s", path), e);
        }
    }
}
