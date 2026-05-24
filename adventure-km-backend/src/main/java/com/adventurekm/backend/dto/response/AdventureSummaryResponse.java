package com.adventurekm.backend.dto.response;

import java.time.LocalDate;

public record AdventureSummaryResponse(
    Long id, String title, LocalDate date, String type,
    Integer difficulty, String status,
    UserResponse author, AdventureStatsResponse stats
) {}
