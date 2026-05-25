package com.adventurekm.backend.dto.request;

import java.time.LocalDate;
import java.util.List;

public record AdventureUpdateRequest(
    String title,
    LocalDate date,
    String content,
    String type,
    Integer difficulty,
    List<Long> equipmentIds,
    java.math.BigDecimal distanceKm,
    Integer elevationGainM,
    Integer durationMinutes
) {}
