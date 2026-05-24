package com.adventurekm.backend.repository;

import com.adventurekm.backend.model.UserLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserLevelRepository extends JpaRepository<UserLevel, Long> {
    List<UserLevel> findAllByOrderByRpgScoreDesc();
    List<UserLevel> findAllByOrderByTotalKmDesc();
    List<UserLevel> findAllByOrderByTotalElevationMDesc();
    List<UserLevel> findAllByOrderByAdventureCountDesc();
}
