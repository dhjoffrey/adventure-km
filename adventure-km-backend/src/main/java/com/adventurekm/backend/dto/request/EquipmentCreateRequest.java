package com.adventurekm.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EquipmentCreateRequest(
    @NotBlank String name,
    @NotBlank String category,
    String iconKey,
    String pixelSpriteKey
) {}
