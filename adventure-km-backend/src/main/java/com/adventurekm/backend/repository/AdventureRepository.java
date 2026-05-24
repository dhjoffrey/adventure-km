package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.Adventure;
import com.adventurekm.backend.model.AdventureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdventureRepository extends JpaRepository<Adventure, Long> {
    List<Adventure> findByStatusOrderByDateDesc(AdventureStatus status);
    List<Adventure> findByUserIdOrderByDateDesc(Long userId);
    List<Adventure> findByUserIdAndStatusOrderByDateDesc(Long userId, AdventureStatus status);
}
