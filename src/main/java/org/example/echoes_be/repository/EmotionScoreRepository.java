package org.example.echoes_be.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.echoes_be.domain.EmotionScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EmotionScoreRepository extends JpaRepository<EmotionScore, Long> {
    Optional<EmotionScore> findByDiaryId(Long diaryId);

    // USER 기준으로 가장 최근 감정 점수 1개 불러오기
    Optional<EmotionScore> findTopByUserIdOrderByCreatedAtDesc(Long userId);

    // USER 의 최근 7일 감정 점수 조회
    List<EmotionScore> findTop7ByUserIdOrderByCreatedAtDesc(Long userId);

    List<EmotionScore> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime fromDateTime);


    Optional<EmotionScore> findByUserIdAndDiaryCreatedAt(Long userId, LocalDate date);
}
