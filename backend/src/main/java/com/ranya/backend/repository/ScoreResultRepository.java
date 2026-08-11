package com.ranya.backend.repository;

import com.ranya.backend.model.ScoreResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScoreResultRepository extends JpaRepository<ScoreResult, Long> {
}