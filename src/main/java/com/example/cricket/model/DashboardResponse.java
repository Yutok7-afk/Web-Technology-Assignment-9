package com.example.cricket.model;

import java.util.List;

public record DashboardResponse(
        Long matchId,
        String title,
        String venue,
        String status,
        int oversLimit,
        int inningsNumber,
        Long battingTeamId,
        Long bowlingTeamId,
        String battingTeam,
        String bowlingTeam,
        int runs,
        int wickets,
        String overs,
        Integer targetRuns,
        String striker,
        String nonStriker,
        String currentBowler,
        List<BattingView> battingStats,
        List<BowlingView> bowlingStats,
        List<EventView> recentEvents,
        List<String> inningsSummary,
        String resultSummary
) {}
