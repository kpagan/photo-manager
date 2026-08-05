package org.kpagan.photo_manager.server.image.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<ImageEntity, Long> {

    // 1. Exact Duplicate Search
    List<ImageEntity> findBySha256(String sha256);

    boolean existsByAbsolutePath(String path);

    /**
     * Finds near-duplicates using H2's native BIT_COUNT and XOR (BITXOR or ^).
     * BIT_COUNT(p.pHash XOR :targetHash) returns the exact Hamming Distance in SQL.
     */
    @Query(value = """
        SELECT * FROM photos p
        WHERE p.PERCEPTUAL_HASH IS NOT NULL
          AND BITCOUNT(BITXOR(p.PERCEPTUAL_HASH, CAST(:targetHash AS BIGINT))) <= :maxDistance
        """, nativeQuery = true)
    List<ImageEntity> findByHammingDistance(
            @Param("targetHash") long targetHash,
            @Param("maxDistance") int maxDistance
    );


}