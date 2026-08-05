package org.kpagan.photo_manager.server.image.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateImageRepository extends JpaRepository<DuplicateImageEntity, Long> {

    @Query(value = """
            select  i1.id,
                    i1.filename,
                    i1.absolutePath,
                    i1.fileSize,
                    i1.sha256,
                    i1.perceptualHash,
                    i1.dateTaken,
                    i1.width,
                    i1.height,
                    d.exactMatch,
                    i2.id,
                    i2.filename,
                    i2.absolutePath,
                    i2.fileSize,
                    i2.sha256,
                    i2.perceptualHash,
                    i2.dateTaken,
                    i2.width,
                    i2.height
            from DuplicateImageEntity d
            inner join ImageEntity i1 on d.id.image1Id = i1.id
            inner join ImageEntity i2 on d.id.image2Id = i2.id
            """)
    List<DuplicateImagePairEntity> findDuplicates();
}