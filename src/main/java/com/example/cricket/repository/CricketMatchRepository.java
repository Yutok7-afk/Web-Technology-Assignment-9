package com.example.cricket.repository;

import com.example.cricket.entity.CricketMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CricketMatchRepository extends JpaRepository<CricketMatch, Long> {
    List<CricketMatch> findAllByOrderByMatchDateDesc();
}
