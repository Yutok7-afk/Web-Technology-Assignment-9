package com.example.cricket.service;

import com.example.cricket.entity.*;
import com.example.cricket.model.*;
import com.example.cricket.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class CricketService {
    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final CricketMatchRepository matchRepository;
    private final InningsRepository inningsRepository;
    private final BallEventRepository eventRepository;
    private final BattingStatRepository battingStatRepository;
    private final BowlingStatRepository bowlingStatRepository;

    public CricketService(TeamRepository teamRepository,
                          PlayerRepository playerRepository,
                          CricketMatchRepository matchRepository,
                          InningsRepository inningsRepository,
                          BallEventRepository eventRepository,
                          BattingStatRepository battingStatRepository,
                          BowlingStatRepository bowlingStatRepository) {
        this.teamRepository = teamRepository;
        this.playerRepository = playerRepository;
        this.matchRepository = matchRepository;
        this.inningsRepository = inningsRepository;
        this.eventRepository = eventRepository;
        this.battingStatRepository = battingStatRepository;
        this.bowlingStatRepository = bowlingStatRepository;
    }

    public List<Team> getTeams() {
        return teamRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    public Team createTeam(CreateTeamRequest request) {
        teamRepository.findByNameIgnoreCase(request.name()).ifPresent(t -> {
            throw new ResponseStatusException(BAD_REQUEST, "Team already exists");
        });
        Team team = new Team(request.name().trim(), request.shortName().trim().toUpperCase(Locale.ROOT), request.city());
        return teamRepository.save(team);
    }

    public List<Player> getPlayers(Long teamId) {
        if (teamId == null) {
            return playerRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        }
        return playerRepository.findByTeamIdOrderByName(teamId);
    }

    public Player createPlayer(CreatePlayerRequest request) {
        Team team = teamRepository.findById(request.teamId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Team not found"));
        return playerRepository.save(new Player(request.name().trim(), request.role().trim(), team));
    }

    @Transactional
    public CricketMatch createMatch(CreateMatchRequest request) {
        if (request.teamOneId().equals(request.teamTwoId())) {
            throw new ResponseStatusException(BAD_REQUEST, "A match requires two different teams");
        }
        Team teamOne = getTeam(request.teamOneId());
        Team teamTwo = getTeam(request.teamTwoId());

        CricketMatch match = new CricketMatch();
        match.setTitle(request.title().trim());
        match.setVenue(request.venue().trim());
        match.setOversLimit(request.oversLimit());
        match.setStatus("LIVE");
        match.setMatchDate(LocalDateTime.now());
        match.setTeamOne(teamOne);
        match.setTeamTwo(teamTwo);
        match.setCurrentInnings(1);
        match = matchRepository.save(match);

        createInnings(match, 1, teamOne, teamTwo, null);
        return match;
    }

    public List<CricketMatch> getMatches() {
        return matchRepository.findAllByOrderByMatchDateDesc();
    }

    public CricketMatch getMatch(Long matchId) {
        return matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Match not found"));
    }

    @Transactional
    public CricketMatch updateStatus(Long matchId, String status) {
        CricketMatch match = getMatch(matchId);
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        if (!List.of("UPCOMING", "LIVE", "COMPLETED").contains(normalized)) {
            throw new ResponseStatusException(BAD_REQUEST, "Status must be UPCOMING, LIVE or COMPLETED");
        }
        match.setStatus(normalized);
        return matchRepository.save(match);
    }

    @Transactional
    public BallEvent recordEvent(Long matchId, EventRequest request) {
        CricketMatch match = getMatch(matchId);
        if (!"LIVE".equalsIgnoreCase(match.getStatus())) {
            throw new ResponseStatusException(BAD_REQUEST, "Only LIVE matches can receive score events");
        }

        int inningsNumber = Optional.ofNullable(match.getCurrentInnings()).orElse(1);
        Innings innings = inningsRepository.findByMatchIdAndInningsNumber(matchId, inningsNumber)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Current innings not found"));

        Player batsman = playerRepository.findById(request.batsmanId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Batsman not found"));
        Player bowler = playerRepository.findById(request.bowlerId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Bowler not found"));

        if (!batsman.getTeam().getId().equals(innings.getBattingTeam().getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Selected batsman does not belong to batting team");
        }
        if (!bowler.getTeam().getId().equals(innings.getBowlingTeam().getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "Selected bowler does not belong to bowling team");
        }

        String type = request.type().trim().toUpperCase(Locale.ROOT);
        if (!List.of("DOT", "RUN", "FOUR", "SIX", "WICKET", "WIDE", "NO_BALL").contains(type)) {
            throw new ResponseStatusException(BAD_REQUEST, "Unsupported event type");
        }

        int suppliedRuns = Math.max(request.runs() == null ? 0 : request.runs(), 0);
        int batRuns = 0;
        int totalRuns = 0;
        boolean legal = true;

        switch (type) {
            case "DOT" -> { batRuns = 0; totalRuns = 0; }
            case "RUN" -> { batRuns = suppliedRuns; totalRuns = suppliedRuns; }
            case "FOUR" -> { batRuns = 4; totalRuns = 4; }
            case "SIX" -> { batRuns = 6; totalRuns = 6; }
            case "WICKET" -> { batRuns = suppliedRuns; totalRuns = suppliedRuns; }
            case "WIDE" -> { batRuns = 0; totalRuns = Math.max(1, suppliedRuns); legal = false; }
            case "NO_BALL" -> { batRuns = suppliedRuns; totalRuns = 1 + suppliedRuns; legal = false; }
        }

        innings.setTotalRuns(innings.getTotalRuns() + totalRuns);
        if (legal) {
            innings.setLegalBalls(innings.getLegalBalls() + 1);
        }
        if ("WICKET".equals(type)) {
            innings.setWickets(innings.getWickets() + 1);
        }

        BattingStat battingStat = getOrCreateBattingStat(innings, batsman);
        battingStat.setRuns(battingStat.getRuns() + batRuns);
        if (legal) {
            battingStat.setBalls(battingStat.getBalls() + 1);
        }
        if ("FOUR".equals(type)) battingStat.setFours(battingStat.getFours() + 1);
        if ("SIX".equals(type)) battingStat.setSixes(battingStat.getSixes() + 1);
        if ("WICKET".equals(type)) battingStat.setOut(true);
        battingStatRepository.save(battingStat);

        BowlingStat bowlingStat = getOrCreateBowlingStat(innings, bowler);
        bowlingStat.setRunsConceded(bowlingStat.getRunsConceded() + totalRuns);
        if (legal) bowlingStat.setBalls(bowlingStat.getBalls() + 1);
        if ("WICKET".equals(type)) bowlingStat.setWickets(bowlingStat.getWickets() + 1);
        bowlingStatRepository.save(bowlingStat);

        innings.setStriker(batsman);
        innings.setCurrentBowler(bowler);
        if (innings.getNonStriker() == null || innings.getNonStriker().getId().equals(batsman.getId())) {
            innings.setNonStriker(findFirstAvailableBatter(innings, batsman.getId()));
        }
        if ("WICKET".equals(type)) {
            Player next = findFirstAvailableBatter(innings,
                    innings.getStriker() == null ? null : innings.getStriker().getId(),
                    innings.getNonStriker() == null ? null : innings.getNonStriker().getId());
            innings.setStriker(next);
        }

        BallEvent event = new BallEvent();
        event.setOverNumber((innings.getLegalBalls() / 6) + 1);
        event.setBallNumber((innings.getLegalBalls() % 6) + 1);
        event.setInnings(innings);
        event.setBatsman(batsman);
        event.setBowler(bowler);
        event.setEventType(type);
        event.setBatRuns(batRuns);
        event.setTotalRuns(totalRuns);
        event.setLegalDelivery(legal);
        event.setNote(request.note() == null ? "" : request.note().trim());
        event.setCreatedAt(LocalDateTime.now());
        eventRepository.save(event);

        boolean limitReached = innings.getLegalBalls() >= innings.getMatch().getOversLimit() * 6;
        boolean allOut = innings.getWickets() >= 10;
        boolean chaseFinished = innings.getTargetRuns() != null && innings.getTotalRuns() >= innings.getTargetRuns();

        if (limitReached || allOut || chaseFinished) {
            completeInningsAndProgress(match, innings);
        }

        inningsRepository.save(innings);
        return event;
    }

    private void completeInningsAndProgress(CricketMatch match, Innings innings) {
        innings.setCompleted(true);

        if (innings.getInningsNumber() == 1) {
            Team nextBatting = innings.getBowlingTeam();
            Team nextBowling = innings.getBattingTeam();
            match.setCurrentInnings(2);
            createInnings(match, 2, nextBatting, nextBowling, innings.getTotalRuns() + 1);
            matchRepository.save(match);
        } else {
            match.setStatus("COMPLETED");
            List<Innings> all = inningsRepository.findByMatchIdOrderByInningsNumber(match.getId());
            int first = all.stream().filter(i -> i.getInningsNumber() == 1).mapToInt(Innings::getTotalRuns).findFirst().orElse(0);
            int second = innings.getTotalRuns();
            if (second > first) {
                match.setResultSummary(innings.getBattingTeam().getName() + " won by " + Math.max(1, 10 - innings.getWickets()) + " wickets");
            } else if (second < first) {
                match.setResultSummary(all.stream().filter(i -> i.getInningsNumber() == 1).findFirst().map(Innings::getBattingTeam).map(Team::getName).orElse("First innings team")
                        + " won by " + Math.max(1, first - second) + " runs");
            } else {
                match.setResultSummary("Match tied");
            }
            matchRepository.save(match);
        }
    }

    @Transactional
    public DashboardResponse getDashboard(Long matchId) {
        CricketMatch match = getMatch(matchId);
        int current = Optional.ofNullable(match.getCurrentInnings()).orElse(1);
        Innings innings = inningsRepository.findByMatchIdAndInningsNumber(matchId, current)
                .orElseGet(() -> inningsRepository.findByMatchIdAndInningsNumber(matchId, 1).orElseThrow());

        List<BattingStat> battingStats = battingStatRepository.findByInningsIdOrderByRunsDesc(innings.getId());
        List<BowlingStat> bowlingStats = bowlingStatRepository.findByInningsIdOrderByWicketsDesc(innings.getId());

        List<BattingView> battingViews = battingStats.stream().map(s -> new BattingView(
                s.getPlayer().getId(),
                s.getPlayer().getName(),
                s.getRuns(),
                s.getBalls(),
                s.getFours(),
                s.getSixes(),
                s.isOut(),
                s.getBalls() == 0 ? 0 : round2((s.getRuns() * 100.0) / s.getBalls())
        )).toList();

        List<BowlingView> bowlingViews = bowlingStats.stream().map(s -> new BowlingView(
                s.getPlayer().getId(),
                s.getPlayer().getName(),
                formatOvers(s.getBalls()),
                s.getRunsConceded(),
                s.getWickets(),
                s.getBalls() == 0 ? 0 : round2(s.getRunsConceded() / (s.getBalls() / 6.0))
        )).toList();

        List<BallEvent> events = eventRepository.findRecentByMatchId(matchId).stream().limit(12).toList();
        List<EventView> eventViews = events.stream()
                .sorted(Comparator.comparing(BallEvent::getCreatedAt))
                .map(this::toEventView)
                .toList();

        List<String> summaries = inningsRepository.findByMatchIdOrderByInningsNumber(matchId).stream()
                .map(i -> i.getBattingTeam().getShortName() + " " + i.getTotalRuns() + "/" + i.getWickets() + " (" + formatOvers(i.getLegalBalls()) + ")")
                .toList();

        return new DashboardResponse(
                match.getId(),
                match.getTitle(),
                match.getVenue(),
                match.getStatus(),
                match.getOversLimit(),
                innings.getInningsNumber(),
                innings.getBattingTeam().getId(),
                innings.getBowlingTeam().getId(),
                innings.getBattingTeam().getName(),
                innings.getBowlingTeam().getName(),
                innings.getTotalRuns(),
                innings.getWickets(),
                formatOvers(innings.getLegalBalls()),
                innings.getTargetRuns(),
                innings.getStriker() == null ? "-" : innings.getStriker().getName(),
                innings.getNonStriker() == null ? "-" : innings.getNonStriker().getName(),
                innings.getCurrentBowler() == null ? "-" : innings.getCurrentBowler().getName(),
                battingViews,
                bowlingViews,
                eventViews,
                summaries,
                match.getResultSummary()
        );
    }

    private EventView toEventView(BallEvent event) {
        String over = event.getOverNumber() + "." + event.getBallNumber();
        return new EventView(
                event.getId(),
                over,
                event.getBatsman().getName(),
                event.getBowler().getName(),
                event.getEventType(),
                event.getTotalRuns(),
                event.getNote()
        );
    }

    private Innings createInnings(CricketMatch match, int number, Team batting, Team bowling, Integer target) {
        Innings innings = new Innings();
        innings.setMatch(match);
        innings.setInningsNumber(number);
        innings.setBattingTeam(batting);
        innings.setBowlingTeam(bowling);
        innings.setTargetRuns(target);

        List<Player> batters = playerRepository.findByTeamIdOrderByName(batting.getId());
        List<Player> bowlers = playerRepository.findByTeamIdOrderByName(bowling.getId());
        if (batters.size() >= 1) innings.setStriker(batters.get(0));
        if (batters.size() >= 2) innings.setNonStriker(batters.get(1));
        if (!bowlers.isEmpty()) innings.setCurrentBowler(bowlers.stream()
                .filter(p -> p.getRole().equalsIgnoreCase("BOWLER") || p.getRole().equalsIgnoreCase("ALL_ROUNDER"))
                .findFirst().orElse(bowlers.get(0)));
        return inningsRepository.save(innings);
    }

    private BattingStat getOrCreateBattingStat(Innings innings, Player player) {
        return battingStatRepository.findByInningsIdAndPlayerId(innings.getId(), player.getId()).orElseGet(() -> {
            BattingStat s = new BattingStat();
            s.setInnings(innings);
            s.setPlayer(player);
            return s;
        });
    }

    private BowlingStat getOrCreateBowlingStat(Innings innings, Player player) {
        return bowlingStatRepository.findByInningsIdAndPlayerId(innings.getId(), player.getId()).orElseGet(() -> {
            BowlingStat s = new BowlingStat();
            s.setInnings(innings);
            s.setPlayer(player);
            return s;
        });
    }

    private Player findFirstAvailableBatter(Innings innings, Long... excluded) {
        List<Long> ids = List.of(excluded).stream().filter(x -> x != null).toList();
        return playerRepository.findByTeamIdOrderByName(innings.getBattingTeam().getId()).stream()
                .filter(p -> !ids.contains(p.getId()))
                .filter(p -> battingStatRepository.findByInningsIdAndPlayerId(innings.getId(), p.getId()).map(s -> !s.isOut()).orElse(true))
                .findFirst().orElse(null);
    }

    private Team getTeam(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Team not found: " + id));
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private String formatOvers(int balls) {
        return (balls / 6) + "." + (balls % 6);
    }

}
