package org.example.echoes_be.service;


import lombok.RequiredArgsConstructor;
import org.example.echoes_be.client.AiClient;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.EmotionScore;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.*;
import org.example.echoes_be.repository.DiaryRepository;
import org.example.echoes_be.repository.EmotionScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final AiClient aiClient;
    private final DiaryRepository diaryRepository;
    private final EmotionScoreRepository emotionScoreRepository;

    // AI <- 감정 스코어링 분석 요청 및 응답 저장
    @Transactional
    public EmotionScoreDTO analyzeEmotion(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        Users user = diary.getUser(); // 사용자 추출

        // 1. 이전 감정 점수 조회 (없으면 초기값)
        double prevScore = 50.0;
        double prevState = 1.0;

        Optional<EmotionScore> latestScoreOpt = emotionScoreRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId());
        if (latestScoreOpt.isPresent()) {
            EmotionScore prev = latestScoreOpt.get();
            prevScore = prev.getScore();
            prevState = prev.getState();
        }

        // 2. AI 서버에 감정 분석 요청
        ScoreRequestDTO request = new ScoreRequestDTO();
        request.setTitle(diary.getTitle());
        request.setContent(diary.getContent());
        request.setPrevScore(prevScore);
        request.setPrevState(prevState);

        // 3. AI 서버로부터 응답 받기
        ScoreResponseDTO response = aiClient.requestEmotionScore(request);

        // 4. 결과 저장
        EmotionScore emotionScore = EmotionScore.builder()
                .diary(diary)
                .user(user)
                .prevScore(prevScore)
                .prevState(prevState)
                .score(response.getScore())
                .state(response.getState())
                .build();

        EmotionScore saved = emotionScoreRepository.save(emotionScore);
        return new EmotionScoreDTO(saved);
    }


    // USER 기준 가장 최근 감정 스코어 조회
    public ScoreResultDTO getLatestScore(Long userId) {
        EmotionScore score = emotionScoreRepository.findTopByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new IllegalArgumentException("감정 점수가 존재하지 않습니다."));
        return new ScoreResultDTO(score);
    }

    // USER 의 최근 7일 감정 스코어 조회
    public List<WeekScoreDTO> getWeekScore(Long userId) {
        List<EmotionScore> scores = emotionScoreRepository.findTop7ByUserIdOrderByCreatedAtDesc(userId);
        Collections.reverse(scores); // 오래된 순 정렬
        return scores.stream()
                .map(WeekScoreDTO::new)
                .collect(Collectors.toList());
    }
}
