package com.example.cricket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "bowling_stats", uniqueConstraints = @UniqueConstraint(columnNames = {"innings_id", "player_id"}))
public class BowlingStat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "innings_id")
    @JsonIgnore
    private Innings innings;

    @ManyToOne(optional = false)
    @JoinColumn(name = "player_id")
    private Player player;

    private int balls;
    private int runsConceded;
    private int wickets;

    public BowlingStat() {}

    public Long getId() { return id; }
    public Innings getInnings() { return innings; }
    public Player getPlayer() { return player; }
    public int getBalls() { return balls; }
    public int getRunsConceded() { return runsConceded; }
    public int getWickets() { return wickets; }

    public void setId(Long id) { this.id = id; }
    public void setInnings(Innings innings) { this.innings = innings; }
    public void setPlayer(Player player) { this.player = player; }
    public void setBalls(int balls) { this.balls = balls; }
    public void setRunsConceded(int runsConceded) { this.runsConceded = runsConceded; }
    public void setWickets(int wickets) { this.wickets = wickets; }
}
