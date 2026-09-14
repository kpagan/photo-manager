package org.kpagan.photo_manager.server.image.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "duplicate_group_mappings")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DuplicateGroupMappingsEntity {

    @EmbeddedId
    private DuplicateGroupMappingsEntity.DuplicateMappingId id;

    @Column(name = "exact_match")
    private boolean exactMatch;

    public static DuplicateGroupMappingsEntity create(long groupId, long photoId, boolean exactMatch) {
        return new DuplicateGroupMappingsEntity(new DuplicateGroupMappingsEntity.DuplicateMappingId(groupId, photoId), exactMatch);
    }

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    static class DuplicateMappingId implements Serializable {
        @Column(name = "group_id")
        long groupId;
        @Column(name = "photo_id")
        long photoId;
    }
}
