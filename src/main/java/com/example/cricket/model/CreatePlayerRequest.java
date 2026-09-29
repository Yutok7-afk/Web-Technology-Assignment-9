package com.example.cricket.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreatePlayerRequest(
        @NotBlank String name,
        @NotBlank String role,
        @NotNull Long teamId
) {}
