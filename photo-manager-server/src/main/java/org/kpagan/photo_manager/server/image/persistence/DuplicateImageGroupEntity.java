package org.kpagan.photo_manager.server.image.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "duplicate_groups")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateImageGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sha256;

    @Column(name = "perceptual_hash")
    private long perceptualHash;

}
