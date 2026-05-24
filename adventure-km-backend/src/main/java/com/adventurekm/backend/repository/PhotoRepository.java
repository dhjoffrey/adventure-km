package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByAdventureIdOrderBySortOrder(Long adventureId);
    int countByAdventureId(Long adventureId);
}
