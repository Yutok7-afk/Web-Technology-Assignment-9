package com.example.cricket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ball_events")
public class BallEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "innings_id")
    @JsonIgnore
    private Innings innings;

    @ManyToOne(optional = false)
    @JoinColumn(name = "batsman_id")
    private Player batsman;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bowler_id")
    private Player bowler;

    @Column(nullable = false)
    private String eventType;

    private int overNumber;
    private int ballNumber;
    private int batRuns;
    private int totalRuns;
    private boolean legalDelivery;
    private String note;
    private LocalDateTime createdAt;

    public BallEvent() {}

    public Long getId() { return id; }
    public Innings getInnings() { return innings; }
    public Player getBatsman() { return batsman; }
    public Player getBowler() { return bowler; }
    public String getEventType() { return eventType; }
    public int getOverNumber() { return overNumber; }
    public int getBallNumber() { return ballNumber; }
    public int getBatRuns() { return batRuns; }
    public int getTotalRuns() { return totalRuns; }
    public boolean isLegalDelivery() { return legalDelivery; }
    public String getNote() { return note; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setInnings(Innings innings) { this.innings = innings; }
    public void setBatsman(Player batsman) { this.batsman = batsman; }
    public void setBowler(Player bowler) { this.bowler = bowler; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public void setOverNumber(int overNumber) { this.overNumber = overNumber; }
    public void setBallNumber(int ballNumber) { this.ballNumber = ballNumber; }
    public void setBatRuns(int batRuns) { this.batRuns = batRuns; }
    public void setTotalRuns(int totalRuns) { this.totalRuns = totalRuns; }
    public void setLegalDelivery(boolean legalDelivery) { this.legalDelivery = legalDelivery; }
    public void setNote(String note) { this.note = note; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
