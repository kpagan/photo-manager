package org.kpagan.photo_manager.server.imaging;

import org.kpagan.photo_manager.server.image.ImageModel;
import org.kpagan.photo_manager.server.image.persistence.ImageEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface ImageMapper {

    @Mapping(source = "metadata.filename", target = "filename")
    @Mapping(source = "metadata.absolutePath", target = "absolutePath")
    @Mapping(source = "metadata.fileSize", target = "fileSize")
    @Mapping(source = "metadata.dateTaken", target = "dateTaken")
    @Mapping(source = "metadata.timeTaken", target = "timeTaken")
    @Mapping(source = "metadata.cameraMake", target = "cameraMake")
    @Mapping(source = "metadata.cameraModel", target = "cameraModel")
    @Mapping(source = "metadata.width", target = "width")
    @Mapping(source = "metadata.height", target = "height")
    @Mapping(source = "metadata.latitude", target = "latitude")
    @Mapping(source = "metadata.longitude", target = "longitude")
    @Mapping(source = "hash.sha256", target = "sha256")
    @Mapping(source = "hash.perceptualHash", target = "perceptualHash")
    ImageEntity mapToEntity(ImageModel model);

    @InheritInverseConfiguration
    ImageModel mapToModel(ImageEntity entity);

}
