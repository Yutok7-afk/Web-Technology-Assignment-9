package com.example.cricket.model;

public record BowlingView(
        Long playerId,
        String playerName,
        String overs,
        int runsConceded,
        int wickets,
        double economy
) {}
