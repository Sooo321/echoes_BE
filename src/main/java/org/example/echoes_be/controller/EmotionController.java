package org.example.echoes_be.controller;


import lombok.RequiredArgsConstructor;
import org.example.echoes_be.domain.EmotionScore;
import org.example.echoes_be.dto.EmotionScoreDTO;
import org.example.echoes_be.service.EmotionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotion")
public class EmotionController {

    private final EmotionService emotionService;

    // AI 에게 감정 스코어링 분석 요청 및 응답 받는 API
    @PostMapping("/{diaryId}/analyze")
    public ResponseEntity<EmotionScoreDTO> createEmotionScore(@PathVariable Long diaryId) {
        EmotionScoreDTO saved = emotionService.analyzeEmotion(diaryId);
        return ResponseEntity.ok(saved);
    }


    // 분석된 감정 SCORE 조회 API
    @GetMapping("/{diaryId}")
    public ResponseEntity<EmotionScoreDTO> getEmotionScore(@PathVariable Long diaryId) {
        EmotionScoreDTO result = emotionService.getLatestScore(diaryId);
        return ResponseEntity.ok(result);
    }

}
