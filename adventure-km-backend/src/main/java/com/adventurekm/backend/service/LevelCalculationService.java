package com.adventurekm.backend.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class LevelCalculationService {

    public int calculateScore(BigDecimal totalKm, int totalElevationM, int adventureCount) {
        int kmPoints = totalKm.intValue();
        int elevationPoints = (totalElevationM / 100) * 2;
        int adventurePoints = adventureCount * 50;
        return kmPoints + elevationPoints + adventurePoints;
    }

    public int calculateLevel(int score) {
        if (score <= 0) return 1;
        return Math.max(1, (int) Math.floor(Math.sqrt(score / 10.0)));
    }
}
