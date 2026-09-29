package com.example.cricket.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "batting_stats",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"innings_id", "player_id"}
    )
)
public class BattingStat {

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

    private int runs;
    private int balls;
    private int fours;
    private int sixes;

    /*
     * 'out' can cause SQL problems in MySQL.
     * Therefore, the actual database column is named 'is_out'.
     */
    @Column(name = "is_out", nullable = false)
    private boolean out;

    // Default constructor required by JPA
    public BattingStat() {
    }

    // Getters

    public Long getId() {
        return id;
    }

    public Innings getInnings() {
        return innings;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRuns() {
        return runs;
    }

    public int getBalls() {
        return balls;
    }

    public int getFours() {
        return fours;
    }

    public int getSixes() {
        return sixes;
    }

    public boolean isOut() {
        return out;
    }

    // Setters

    public void setId(Long id) {
        this.id = id;
    }

    public void setInnings(Innings innings) {
        this.innings = innings;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setRuns(int runs) {
        this.runs = runs;
    }

    public void setBalls(int balls) {
        this.balls = balls;
    }

    public void setFours(int fours) {
        this.fours = fours;
    }

    public void setSixes(int sixes) {
        this.sixes = sixes;
    }

    public void setOut(boolean out) {
        this.out = out;
    }
}