package com.adventurekm.backend.dto.response;

import java.math.BigDecimal;

public record UserLevelResponse(
    Long userId, String username, Integer avatarSpriteId,
    BigDecimal totalKm, Integer totalElevationM,
    Integer adventureCount, Integer rpgScore, Integer level,
    String theme
) {}
