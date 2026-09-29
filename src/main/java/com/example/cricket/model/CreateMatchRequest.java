package com.example.cricket.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateMatchRequest(
        @NotBlank String title,
        @NotBlank String venue,
        @NotNull Long teamOneId,
        @NotNull Long teamTwoId,
        @Min(1) @Max(50) int oversLimit
) {}
