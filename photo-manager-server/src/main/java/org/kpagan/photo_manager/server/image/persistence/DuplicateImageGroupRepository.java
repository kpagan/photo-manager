package org.kpagan.photo_manager.server.image.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DuplicateImageGroupRepository extends JpaRepository<DuplicateImageGroupEntity, Long> {

    DuplicateImageGroupEntity findBySha256(String sha256);

    /**
     * Finds near-duplicates using H2's native BIT_COUNT and XOR (BITXOR or ^).
     * BIT_COUNT(p.pHash XOR :targetHash) returns the exact Hamming Distance in SQL.
     */
    @Query(value = """
        SELECT * FROM duplicate_groups d
        WHERE d.PERCEPTUAL_HASH IS NOT NULL
          AND BITCOUNT(BITXOR(d.PERCEPTUAL_HASH, CAST(:targetHash AS BIGINT))) <= :maxDistance
        """, nativeQuery = true)
    List<DuplicateImageGroupEntity> findByHammingDistance(
            @Param("targetHash") long targetHash,
            @Param("maxDistance") int maxDistance
    );

    @Query(value = """
            select d.id
            from DuplicateImageGroupEntity d
            order by d.id
            """,
            countQuery = """
            select count(d.id)
            from DuplicateImageGroupEntity d
            """)
    Page<Long> getGroupIds(Pageable pageable);
}
