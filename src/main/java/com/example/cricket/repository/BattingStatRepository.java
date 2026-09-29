package com.example.cricket.repository;

import com.example.cricket.entity.BattingStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BattingStatRepository extends JpaRepository<BattingStat, Long> {
    Optional<BattingStat> findByInningsIdAndPlayerId(Long inningsId, Long playerId);
    List<BattingStat> findByInningsIdOrderByRunsDesc(Long inningsId);
}
