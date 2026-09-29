package com.example.cricket.model;

import jakarta.validation.constraints.NotBlank;

public record StatusRequest(@NotBlank String status) {}
