package com.example.cricket.repository;

import com.example.cricket.entity.BowlingStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BowlingStatRepository extends JpaRepository<BowlingStat, Long> {
    Optional<BowlingStat> findByInningsIdAndPlayerId(Long inningsId, Long playerId);
    List<BowlingStat> findByInningsIdOrderByWicketsDesc(Long inningsId);
}
