package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.AdventureStats;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdventureStatsRepository extends JpaRepository<AdventureStats, Long> {
    Optional<AdventureStats> findByAdventureId(Long adventureId);
}
