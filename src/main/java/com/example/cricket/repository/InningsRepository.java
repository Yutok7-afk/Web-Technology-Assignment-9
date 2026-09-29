package com.example.cricket.repository;

import com.example.cricket.entity.Innings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InningsRepository extends JpaRepository<Innings, Long> {
    Optional<Innings> findByMatchIdAndInningsNumber(Long matchId, int inningsNumber);
    List<Innings> findByMatchIdOrderByInningsNumber(Long matchId);
}
