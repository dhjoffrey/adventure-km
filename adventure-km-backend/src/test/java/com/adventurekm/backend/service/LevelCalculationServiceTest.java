package com.adventurekm.backend.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class LevelCalculationServiceTest {

    private final LevelCalculationService service = new LevelCalculationService();

    @Test
    void calculateScoreForJoffreysSeedData() {
        // score = (367 * 1) + (22000 / 100 * 2) + (3 * 50) = 367 + 440 + 150 = 957
        int score = service.calculateScore(BigDecimal.valueOf(367), 22000, 3);
        assertThat(score).isEqualTo(957);
    }

    @Test
    void calculateLevelFromScore() {
        // level = floor(sqrt(957 / 10)) = floor(sqrt(95.7)) = floor(9.78) = 9
        int level = service.calculateLevel(957);
        assertThat(level).isEqualTo(9);
    }

    @Test
    void level1ForNewUser() {
        int score = service.calculateScore(BigDecimal.ZERO, 0, 0);
        assertThat(score).isEqualTo(0);
        assertThat(service.calculateLevel(0)).isEqualTo(1);
    }
}
