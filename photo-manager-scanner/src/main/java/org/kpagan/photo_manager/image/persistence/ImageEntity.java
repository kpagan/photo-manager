package org.kpagan.photo_manager.image.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "photos", indexes = {
        @Index(name = "idx_sha256", columnList = "sha256"),
        @Index(name = "idx_chunk1", columnList = "pHashChunk1"),
        @Index(name = "idx_chunk2", columnList = "pHashChunk2"),
        @Index(name = "idx_chunk3", columnList = "pHashChunk3"),
        @Index(name = "idx_chunk4", columnList = "pHashChunk4")
})
@Getter
@Setter
@AllArgsConstructor
public class ImageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false, unique = true)
    private String absolutePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(nullable = false)
    private String sha256;

    // 16-bit partitions for fast indexed SQL queries
    @Column(name = "p_hash_chunk1")
    private Integer pHashChunk1;
    @Column(name = "p_hash_chunk2")
    private Integer pHashChunk2;
    @Column(name = "p_hash_chunk3")
    private Integer pHashChunk3;
    @Column(name = "p_hash_chunk4")
    private Integer pHashChunk4;

    // Metadata Fields
    @Column(name = "date_taken")
    private LocalDate dateTaken;
    @Column(name = "time_taken")
    private LocalTime timeTaken;
    @Column(name = "camera_make")
    private String cameraMake;
    @Column(name = "camera_model")
    private String cameraModel;
    private Integer width;
    private Integer height;
    private Double latitude;
    private Double longitude;

    // Constructors
    public ImageEntity() {
    }

    /**
     * SETTER: Accepts a 64-bit pHash and splits it into 4 x 16-bit integers
     * for storage in the database columns.
     */
    public void setPerceptualHash(Long pHash) {
        if (pHash == null) {
            this.pHashChunk1 = null;
            this.pHashChunk2 = null;
            this.pHashChunk3 = null;
            this.pHashChunk4 = null;
        } else {
            // Mask with 0xFFFF to isolate 16 bits per chunk
            this.pHashChunk1 = (int) ((pHash >> 48) & 0xFFFF);
            this.pHashChunk2 = (int) ((pHash >> 32) & 0xFFFF);
            this.pHashChunk3 = (int) ((pHash >> 16) & 0xFFFF);
            this.pHashChunk4 = (int) (pHash & 0xFFFF);
        }
    }

    /**
     * GETTER: Reconstructs the original 64-bit long pHash from the 4 chunks on the fly.
     * No pHash column needed in the SQLite DB table!
     */
    @Transient // Tells JPA/Hibernate NOT to create a 'pHash' column in the database table
    public Long getPerceptualHash() {
        if (pHashChunk1 == null || pHashChunk2 == null || pHashChunk3 == null || pHashChunk4 == null) {
            return null;
        }

        // Reconstruct by shifting chunks back to their bit positions & combining via bitwise OR (|)
        // Note: (& 0xFFFFL) prevents negative sign-extension when converting int -> long
        return ((long) (pHashChunk1 & 0xFFFF) << 48) |
                ((long) (pHashChunk2 & 0xFFFF) << 32) |
                ((long) (pHashChunk3 & 0xFFFF) << 16) |
                ((long) (pHashChunk4 & 0xFFFF));
    }
}
