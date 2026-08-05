package org.kpagan.photo_manager.server.image.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "duplicate_images")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateImageEntity {

    @EmbeddedId
    private DuplicateId id;

    @Column(name = "exact_match")
    private boolean exactMatch;

    public static DuplicateImageEntity create(long id1, long id2, boolean exactMatch) {
        return new DuplicateImageEntity(new DuplicateId(id1, id2), exactMatch);
    }

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    static class DuplicateId implements Serializable {
        @Column(name = "image1_id")
        long image1Id;
        @Column(name = "image2_id")
        long image2Id;
    }
}
