package org.kpagan.photo_manager.server.image.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DuplicateGroupMappingsRepository extends JpaRepository<DuplicateGroupMappingsEntity, Long> {

    @Query(value = """
            select  i.id,
                    d.id.groupId,
                    i.filename,
                    i.absolutePath,
                    i.fileSize,
                    i.sha256,
                    i.perceptualHash,
                    i.dateTaken,
                    i.width,
                    i.height,
                    d.exactMatch
            from DuplicateGroupMappingsEntity d
            inner join ImageEntity i on d.id.photoId = i.id
            where d.id.groupId in :groupIds
            order by d.id.photoId
            """)
    List<DuplicateImageEntity> findByByGroupIds(@Param("groupIds") List<Long> groupIds);

    long countByExactMatch(boolean exactMatch);
}
