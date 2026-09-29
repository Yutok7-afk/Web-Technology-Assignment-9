package com.example.cricket.config;

import com.example.cricket.entity.CricketMatch;
import com.example.cricket.entity.Player;
import com.example.cricket.entity.Team;
import com.example.cricket.model.CreateMatchRequest;
import com.example.cricket.model.CreatePlayerRequest;
import com.example.cricket.model.CreateTeamRequest;
import com.example.cricket.model.EventRequest;
import com.example.cricket.repository.PlayerRepository;
import com.example.cricket.repository.TeamRepository;
import com.example.cricket.service.CricketService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    private final CricketService cricketService;
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    public DataInitializer(CricketService cricketService,
                           TeamRepository teamRepository,
                           PlayerRepository playerRepository) {
        this.cricketService = cricketService;
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
    }

    @Override
    public void run(String... args) {
        if (teamRepository.count() > 0) {
            return;
        }

        Team india = cricketService.createTeam(new CreateTeamRequest("India", "IND", "Mumbai"));
        Team australia = cricketService.createTeam(new CreateTeamRequest("Australia", "AUS", "Sydney"));
        Team england = cricketService.createTeam(new CreateTeamRequest("England", "ENG", "London"));
        Team pakistan = cricketService.createTeam(new CreateTeamRequest("Pakistan", "PAK", "Lahore"));

        List<Player> indiaPlayers = createPlayers(india, List.of(
                new String[]{"Rohit Sharma", "BATTER"},
                new String[]{"Shubman Gill", "BATTER"},
                new String[]{"Virat Kohli", "BATTER"},
                new String[]{"Suryakumar Yadav", "BATTER"},
                new String[]{"Ravindra Jadeja", "ALL_ROUNDER"},
                new String[]{"Jasprit Bumrah", "BOWLER"}
        ));

        List<Player> australiaPlayers = createPlayers(australia, List.of(
                new String[]{"Travis Head", "BATTER"},
                new String[]{"Steve Smith", "BATTER"},
                new String[]{"Mitchell Marsh", "ALL_ROUNDER"},
                new String[]{"Glenn Maxwell", "ALL_ROUNDER"},
                new String[]{"Pat Cummins", "BOWLER"},
                new String[]{"Mitchell Starc", "BOWLER"}
        ));

        List<Player> englandPlayers = createPlayers(england, List.of(
                new String[]{"Ben Duckett", "BATTER"},
                new String[]{"Harry Brook", "BATTER"},
                new String[]{"Joe Root", "BATTER"},
                new String[]{"Liam Livingstone", "ALL_ROUNDER"},
                new String[]{"Jofra Archer", "BOWLER"},
                new String[]{"Adil Rashid", "BOWLER"}
        ));

        List<Player> pakistanPlayers = createPlayers(pakistan, List.of(
                new String[]{"Babar Azam", "BATTER"},
                new String[]{"Mohammad Rizwan", "WICKET_KEEPER"},
                new String[]{"Fakhar Zaman", "BATTER"},
                new String[]{"Shadab Khan", "ALL_ROUNDER"},
                new String[]{"Shaheen Afridi", "BOWLER"},
                new String[]{"Haris Rauf", "BOWLER"}
        ));

        CricketMatch match1 = cricketService.createMatch(new CreateMatchRequest(
                "India vs Australia - Live ODI", "Wankhede Stadium, Mumbai", india.getId(), australia.getId(), 50));
        seedEvents(match1.getId(), indiaPlayers, australiaPlayers,
                List.of("FOUR", "RUN", "SIX", "DOT", "RUN", "FOUR", "RUN", "RUN", "WICKET", "FOUR", "SIX", "DOT"));

        CricketMatch match2 = cricketService.createMatch(new CreateMatchRequest(
                "England vs Pakistan - Live T20", "Lord's, London", england.getId(), pakistan.getId(), 20));
        seedEvents(match2.getId(), englandPlayers, pakistanPlayers,
                List.of("FOUR", "RUN", "SIX", "DOT", "RUN", "FOUR", "RUN", "RUN", "FOUR", "DOT", "DOT", "RUN"));
    }

    private List<Player> createPlayers(Team team, List<String[]> input) {
        List<Player> result = new ArrayList<>();
        for (String[] row : input) {
            result.add(cricketService.createPlayer(new CreatePlayerRequest(row[0], row[1], team.getId())));
        }
        return result;
    }

    private void seedEvents(Long matchId, List<Player> batters, List<Player> bowlers, List<String> types) {
        for (int i = 0; i < types.size(); i++) {
            String type = types.get(i);
            Long batsmanId;
            if (i < 6) {
                batsmanId = batters.get(i % 2).getId();
            } else {
                batsmanId = batters.get(1).getId();
            }
            // Keep seeded score simple. The ninth ball in the first demo is a wicket.
            if ("WICKET".equals(type)) {
                batsmanId = batters.get(0).getId();
            }
            Long bowlerId = bowlers.stream()
                    .filter(p -> p.getRole().equalsIgnoreCase("BOWLER") || p.getRole().equalsIgnoreCase("ALL_ROUNDER"))
                    .findFirst()
                    .orElse(bowlers.get(0)).getId();

            int runs = switch (type) {
                case "RUN" -> (i % 3) + 1;
                default -> 0;
            };
            cricketService.recordEvent(matchId, new EventRequest(batsmanId, bowlerId, type, runs, "Demo event"));
        }
    }
}
