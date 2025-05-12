package org.example.echoes_be.repository;

import org.example.echoes_be.domain.EmotionScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionScoreRepository extends JpaRepository<EmotionScore, Long> {
    Optional<EmotionScore> findByDiaryId(Long diaryId);
}