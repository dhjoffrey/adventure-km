package com.adventurekm.backend.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AdventureResponse(
    Long id, String title, LocalDate date, String content,
    String type, Integer difficulty, String gpxPath, String status,
    UserResponse author, AdventureStatsResponse stats,
    List<PhotoResponse> photos, List<EquipmentItemResponse> equipment
) {}
