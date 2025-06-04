package org.example.echoes_be.controller;


import lombok.RequiredArgsConstructor;
import org.example.echoes_be.dto.EmotionScoreDTO;
import org.example.echoes_be.dto.ScoreResultDTO;
import org.example.echoes_be.dto.WeekScoreDTO;
import org.example.echoes_be.service.DiaryService;
import org.example.echoes_be.service.EmotionService;
import org.example.echoes_be.service.GptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emotion")
public class EmotionController {

    private final EmotionService emotionService;
    private final DiaryService diaryService;
    private final GptService gptService;

    // AI 에게 감정 스코어링 분석 요청 및 응답 받는 API
    @PostMapping("/{diaryId}/analyze")
    public ResponseEntity<EmotionScoreDTO> createEmotionScore(@PathVariable Long diaryId) {
        EmotionScoreDTO saved = emotionService.analyzeEmotion(diaryId);
        return ResponseEntity.ok(saved);
    }


    // 분석된 감정 SCORE 조회 API
    @GetMapping("/{userId}/latestScore")
    public ResponseEntity<ScoreResultDTO> getEmotionScore(@PathVariable Long userId) {
        ScoreResultDTO result = emotionService.getLatestScore(userId);
        return ResponseEntity.ok(result);
    }

    // 최근 7일 감정 스코어 조회 API
    @GetMapping("/{userId}/weekScore")
    public ResponseEntity<List<WeekScoreDTO>> getWeekScore(@PathVariable Long userId) {
        return ResponseEntity.ok(emotionService.getWeekScore(userId));
    }

    @GetMapping("/{userId}/report")
    public ResponseEntity<String> getUserReport(@PathVariable Long userId) {
        String report = gptService.generateUserReport(userId);
        return ResponseEntity.ok(report);
    }
}
