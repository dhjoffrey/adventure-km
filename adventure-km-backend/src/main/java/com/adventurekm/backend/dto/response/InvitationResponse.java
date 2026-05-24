package com.adventurekm.backend.dto.response;

import java.time.LocalDateTime;

public record InvitationResponse(
    Long id, String token, String email,
    LocalDateTime expiresAt, LocalDateTime usedAt
) {}
