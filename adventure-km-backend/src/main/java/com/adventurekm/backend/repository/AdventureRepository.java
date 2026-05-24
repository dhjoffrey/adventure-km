package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.Adventure;
import com.adventurekm.backend.model.AdventureStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {
    List<Adventure> findByStatusOrderByDateDesc(AdventureStatus status);
    List<Adventure> findByStatusOrderByDateAsc(AdventureStatus status);
    List<Adventure> findByUser_IdOrderByDateDesc(Long userId);
    List<Adventure> findByUser_IdAndStatusOrderByDateDesc(Long userId, AdventureStatus status);
}
