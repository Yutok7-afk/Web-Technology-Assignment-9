package com.example.cricket.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EventRequest(
        @NotNull Long batsmanId,
        @NotNull Long bowlerId,
        @NotBlank String type,
        Integer runs,
        String note
) {}
