package org.kpagan.photo_manager.server.service.duplicates;

import org.kpagan.photo_manager.server.image.persistence.DuplicateImagePairEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING)
public interface DuplicateImageModelMapper {

    @Mapping(source = "entity.image1Id", target = "image1.id")
    @Mapping(source = "entity.image1Filename", target = "image1.filename")
    @Mapping(source = "entity.image1AbsolutePath", target = "image1.absolutePath")
    @Mapping(source = "entity.image1FileSize", target = "image1.fileSize")
    @Mapping(source = "entity.image1Sha256", target = "image1.sha256")
    @Mapping(source = "entity.image1PerceptualHash", target = "image1.perceptualHash")
    @Mapping(source = "entity.image1DateTaken", target = "image1.dateTaken")
    @Mapping(source = "entity.image1Width", target = "image1.width")
    @Mapping(source = "entity.image1Height", target = "image1.height")
    @Mapping(source = "entity.image2Id", target = "image2.id")
    @Mapping(source = "entity.image2Filename", target = "image2.filename")
    @Mapping(source = "entity.image2AbsolutePath", target = "image2.absolutePath")
    @Mapping(source = "entity.image2FileSize", target = "image2.fileSize")
    @Mapping(source = "entity.image2Sha256", target = "image2.sha256")
    @Mapping(source = "entity.image2PerceptualHash", target = "image2.perceptualHash")
    @Mapping(source = "entity.image2DateTaken", target = "image2.dateTaken")
    @Mapping(source = "entity.image2Width", target = "image2.width")
    @Mapping(source = "entity.image2Height", target = "image2.height")
    @Mapping(source = "entity.exactMatch", target = "image2.exactMatch")
    DuplicateImagePairModel mapToModel(DuplicateImagePairEntity entity);
}
