package org.kpagan.photo_manager.image.persistence;

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

    // 2. Fetch Images that have a valid pHash to run Hamming Distance comparisons
    @Query("""
            SELECT p FROM ImageEntity p
            WHERE p.pHashChunk1 IS NOT NULL
              AND p.pHashChunk2 IS NOT NULL
              AND p.pHashChunk3 IS NOT NULL
              AND p.pHashChunk4 IS NOT NULL
              AND (
                   p.pHashChunk1 = :c1
                OR p.pHashChunk2 = :c2
                OR p.pHashChunk3 = :c3
                OR p.pHashChunk4 = :c4
              )
            """)
    List<ImageEntity> findCandidatesByChunkBounds(
            @Param("c1") int c1,
            @Param("c2") int c2,
            @Param("c3") int c3,
            @Param("c4") int c4
    );
}