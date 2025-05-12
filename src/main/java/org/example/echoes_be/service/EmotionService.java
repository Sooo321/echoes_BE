package org.example.echoes_be.service;


import lombok.RequiredArgsConstructor;
import org.example.echoes_be.client.AiClient;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.EmotionScore;
import org.example.echoes_be.dto.ScoreRequestDTO;
import org.example.echoes_be.dto.ScoreResponseDTO;
import org.example.echoes_be.repository.DiaryRepository;
import org.example.echoes_be.repository.EmotionScoreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final AiClient aiClient;
    private final DiaryRepository diaryRepository;
    private final EmotionScoreRepository emotionScoreRepository;

    @Transactional
    public EmotionScore analyzeEmotion(Long diaryId) {
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // 이미 점수가 존재하면 예외 발생
        if (emotionScoreRepository.findByDiaryId(diaryId).isPresent()) {
            throw new IllegalStateException("이미 감정 점수가 생성된 일기입니다.");
        }

        // AI 서버에 감정 분석 요청
        ScoreRequestDTO request = new ScoreRequestDTO();
        request.setTitle(diary.getTitle());
        request.setContent(diary.getContent());

        ScoreResponseDTO response = aiClient.requestEmotionScore(request);

        // 결과 저장
        EmotionScore emotionScore = EmotionScore.builder()
                .score(response.getScore())
                .diary(diary)
                .build();

        return emotionScoreRepository.save(emotionScore);
    }

    public EmotionScore getScore(Long diaryId) {
        return emotionScoreRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("감정 점수가 존재하지 않습니다."));
    }
}
