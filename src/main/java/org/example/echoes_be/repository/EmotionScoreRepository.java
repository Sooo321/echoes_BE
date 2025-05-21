package org.example.echoes_be.repository;

import org.example.echoes_be.domain.EmotionScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmotionScoreRepository extends JpaRepository<EmotionScore, Long> {
    Optional<EmotionScore> findByDiaryId(Long diaryId);

    // 가장 최근 감정 점수 1개 불러오기
    Optional<EmotionScore> findTopByDiaryIdOrderByCreatedAtDesc(Long diaryId);

}
