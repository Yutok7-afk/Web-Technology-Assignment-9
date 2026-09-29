package com.example.cricket.model;

public record BattingView(
        Long playerId,
        String playerName,
        int runs,
        int balls,
        int fours,
        int sixes,
        boolean out,
        double strikeRate
) {}
