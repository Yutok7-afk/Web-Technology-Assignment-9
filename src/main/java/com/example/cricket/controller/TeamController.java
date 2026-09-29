package com.example.cricket.controller;

import com.example.cricket.entity.Team;
import com.example.cricket.model.CreateTeamRequest;
import com.example.cricket.service.CricketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin
public class TeamController {
    private final CricketService cricketService;

    public TeamController(CricketService cricketService) {
        this.cricketService = cricketService;
    }

    @GetMapping
    public List<Team> getTeams() {
        return cricketService.getTeams();
    }

    @PostMapping
    public Team createTeam(@Valid @RequestBody CreateTeamRequest request) {
        return cricketService.createTeam(request);
    }
}
