package com.example.cricket.controller;

import com.example.cricket.entity.BallEvent;
import com.example.cricket.entity.CricketMatch;
import com.example.cricket.model.CreateMatchRequest;
import com.example.cricket.model.DashboardResponse;
import com.example.cricket.model.EventRequest;
import com.example.cricket.model.StatusRequest;
import com.example.cricket.service.CricketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin
public class MatchController {
    private final CricketService cricketService;

    public MatchController(CricketService cricketService) {
        this.cricketService = cricketService;
    }

    @GetMapping
    public List<CricketMatch> getMatches() {
        return cricketService.getMatches();
    }

    @GetMapping("/{matchId}")
    public CricketMatch getMatch(@PathVariable Long matchId) {
        return cricketService.getMatch(matchId);
    }

    @PostMapping
    public CricketMatch createMatch(@Valid @RequestBody CreateMatchRequest request) {
        return cricketService.createMatch(request);
    }

    @PutMapping("/{matchId}/status")
    public CricketMatch updateStatus(@PathVariable Long matchId, @Valid @RequestBody StatusRequest request) {
        return cricketService.updateStatus(matchId, request.status());
    }

    @PostMapping("/{matchId}/events")
    public BallEvent recordEvent(@PathVariable Long matchId, @Valid @RequestBody EventRequest request) {
        return cricketService.recordEvent(matchId, request);
    }

    @GetMapping("/{matchId}/dashboard")
    public DashboardResponse getDashboard(@PathVariable Long matchId) {
        return cricketService.getDashboard(matchId);
    }
}
