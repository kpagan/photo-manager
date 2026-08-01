package org.kpagan.photo_manager.image.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuplicateImageRepository extends JpaRepository<DuplicateImageEntity, Long> {

}