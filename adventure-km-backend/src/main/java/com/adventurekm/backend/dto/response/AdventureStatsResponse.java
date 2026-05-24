package com.adventurekm.backend.dto.response;

import java.math.BigDecimal;

public record AdventureStatsResponse(
    BigDecimal distanceKm, Integer elevationGainM, Integer elevationLossM,
    Integer durationMinutes, Integer maxAltitudeM, Integer minAltitudeM
) {}
