package com.example.cricket.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "teams")
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String shortName;

    private String city;

    public Team() {}

    public Team(String name, String shortName, String city) {
        this.name = name;
        this.shortName = shortName;
        this.city = city;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getShortName() { return shortName; }
    public String getCity() { return city; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setShortName(String shortName) { this.shortName = shortName; }
    public void setCity(String city) { this.city = city; }
}
