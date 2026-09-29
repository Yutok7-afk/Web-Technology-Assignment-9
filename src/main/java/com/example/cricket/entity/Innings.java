package com.example.cricket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "innings")
public class Innings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "match_id")
    @JsonIgnore
    private CricketMatch match;

    @Column(nullable = false)
    private int inningsNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "batting_team_id")
    private Team battingTeam;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bowling_team_id")
    private Team bowlingTeam;

    private int totalRuns;
    private int wickets;
    private int legalBalls;
    private boolean completed;
    private Integer targetRuns;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "striker_id")
    private Player striker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "non_striker_id")
    private Player nonStriker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_bowler_id")
    private Player currentBowler;

    public Innings() {}

    public Long getId() { return id; }
    public CricketMatch getMatch() { return match; }
    public int getInningsNumber() { return inningsNumber; }
    public Team getBattingTeam() { return battingTeam; }
    public Team getBowlingTeam() { return bowlingTeam; }
    public int getTotalRuns() { return totalRuns; }
    public int getWickets() { return wickets; }
    public int getLegalBalls() { return legalBalls; }
    public boolean isCompleted() { return completed; }
    public Integer getTargetRuns() { return targetRuns; }
    public Player getStriker() { return striker; }
    public Player getNonStriker() { return nonStriker; }
    public Player getCurrentBowler() { return currentBowler; }

    public void setId(Long id) { this.id = id; }
    public void setMatch(CricketMatch match) { this.match = match; }
    public void setInningsNumber(int inningsNumber) { this.inningsNumber = inningsNumber; }
    public void setBattingTeam(Team battingTeam) { this.battingTeam = battingTeam; }
    public void setBowlingTeam(Team bowlingTeam) { this.bowlingTeam = bowlingTeam; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }
    public void setWickets(int wickets) { this.wickets = wickets; }
    public void setLegalBalls(int legalBalls) { this.legalBalls = legalBalls; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setTargetRuns(Integer targetRuns) { this.targetRuns = targetRuns; }
    public void setStriker(Player striker) { this.striker = striker; }
    public void setNonStriker(Player nonStriker) { this.nonStriker = nonStriker; }
    public void setCurrentBowler(Player currentBowler) { this.currentBowler = currentBowler; }
}
