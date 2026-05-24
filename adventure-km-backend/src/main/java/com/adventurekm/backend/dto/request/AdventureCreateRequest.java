package com.adventurekm.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record AdventureCreateRequest(
    @NotBlank String title,
    @NotNull LocalDate date,
    @NotBlank String content,
    String type,
    Integer difficulty,
    List<Long> equipmentIds
) {}
