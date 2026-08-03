package org.kpagan.photo_manager.server.image.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "photos", indexes = {
        @Index(name = "idx_sha256", columnList = "sha256"),
        @Index(name = "idx_perceptual_hash", columnList = "perceptualHash"),
        @Index(name = "idx_absolute_path", columnList="absolutePath", unique = true)
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

    @Column(name = "absolute_path", nullable = false, unique = true)
    private String absolutePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(nullable = false)
    private String sha256;

    // 16-bit partitions for fast indexed SQL queries
    @Column(name = "perceptual_hash")
    private long perceptualHash;

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
}
