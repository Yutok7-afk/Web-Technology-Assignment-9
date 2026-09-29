package com.example.cricket.repository;

import com.example.cricket.entity.BallEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BallEventRepository extends JpaRepository<BallEvent, Long> {
    @Query("select e from BallEvent e where e.innings.match.id = :matchId order by e.createdAt desc")
    List<BallEvent> findRecentByMatchId(@Param("matchId") Long matchId);
}
