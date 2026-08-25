package org.kpagan.photo_manager.server.image.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DuplicateImageRepository extends JpaRepository<DuplicateImageEntity, Long> {

    @Query(value = """
            select distinct d.id.image1Id
            from DuplicateImageEntity d
            order by d.id.image1Id
            """,
            countQuery = """
            select count(distinct d.id.image1Id)
            from DuplicateImageEntity d
            """)
    Page<Long> findDistinctImage1Ids(Pageable pageable);

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
            where d.id.image1Id in :image1Ids
            order by d.id.image1Id, d.id.image2Id
            """)
    List<DuplicateImagePairEntity> findDuplicatesByImage1Ids(@Param("image1Ids") List<Long> image1Ids);

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

    long countByExactMatch(boolean exactMatch);
}