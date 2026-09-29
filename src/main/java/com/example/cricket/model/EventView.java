package com.example.cricket.model;

public record EventView(
        Long id,
        String over,
        String batsman,
        String bowler,
        String type,
        int runs,
        String note
) {}
