package com.example.cricket.controller;

import com.example.cricket.entity.Player;
import com.example.cricket.model.CreatePlayerRequest;
import com.example.cricket.service.CricketService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@CrossOrigin
public class PlayerController {
    private final CricketService cricketService;

    public PlayerController(CricketService cricketService) {
        this.cricketService = cricketService;
    }

    @GetMapping
    public List<Player> getPlayers(@RequestParam(required = false) Long teamId) {
        return cricketService.getPlayers(teamId);
    }

    @PostMapping
    public Player createPlayer(@Valid @RequestBody CreatePlayerRequest request) {
        return cricketService.createPlayer(request);
    }
}
