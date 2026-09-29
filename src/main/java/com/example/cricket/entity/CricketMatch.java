package com.example.cricket.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
public class CricketMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String venue;
    private int oversLimit;

    @Column(nullable = false)
    private String status;

    private LocalDateTime matchDate;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_one_id")
    private Team teamOne;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_two_id")
    private Team teamTwo;

    private Integer currentInnings;
    private String resultSummary;

    public CricketMatch() {}

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getVenue() { return venue; }
    public int getOversLimit() { return oversLimit; }
    public String getStatus() { return status; }
    public LocalDateTime getMatchDate() { return matchDate; }
    public Team getTeamOne() { return teamOne; }
    public Team getTeamTwo() { return teamTwo; }
    public Integer getCurrentInnings() { return currentInnings; }
    public String getResultSummary() { return resultSummary; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setVenue(String venue) { this.venue = venue; }
    public void setOversLimit(int oversLimit) { this.oversLimit = oversLimit; }
    public void setStatus(String status) { this.status = status; }
    public void setMatchDate(LocalDateTime matchDate) { this.matchDate = matchDate; }
    public void setTeamOne(Team teamOne) { this.teamOne = teamOne; }
    public void setTeamTwo(Team teamTwo) { this.teamTwo = teamTwo; }
    public void setCurrentInnings(Integer currentInnings) { this.currentInnings = currentInnings; }
    public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
}
