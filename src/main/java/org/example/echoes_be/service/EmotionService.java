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

import java.time.LocalDate;
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

    //온도 바탕 피드백
    // 오늘의 감정 점수 기반 메시지를 생성해주는 서비스 메서드
    public EmotionMessageResponseDTO buildTodayEmotionMessage(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 날짜 범위 설정 (정확히 오늘/어제 하루)
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime todayEnd = today.plusDays(1).atStartOfDay();
        LocalDateTime yesterdayStart = yesterday.atStartOfDay();
        LocalDateTime yesterdayEnd = yesterday.plusDays(1).atStartOfDay();

        // 오늘 감정 점수 조회
        Optional<EmotionScore> todayOpt =
                emotionScoreRepository.findTop1ByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, todayStart, todayEnd);

        if (todayOpt.isEmpty()) {
            return EmotionMessageResponseDTO.builder()
                    .changeType("NO_TODAY")
                    .message("오늘 하루는 어땠나요? 저와 함께 이야기 나눠봐요")
                    .build();
        }

        double todayScore = todayOpt.get().getScore();

        // 어제 감정 점수 조회
        Optional<EmotionScore> yestOpt =
                emotionScoreRepository.findTop1ByUserIdAndCreatedAtBetweenOrderByCreatedAtDesc(userId, yesterdayStart, yesterdayEnd);

        if (yestOpt.isEmpty()) {
            String message;
            if (todayScore >= 4.0) {
                message = "오늘은 꽤 괜찮은 하루였네요. 평온함이 느껴져요.";
            } else if (todayScore >= 2.5) {
                message = "무난한 하루였던 것 같아요. 잘 지내고 있네요.";
            } else {
                message = "오늘은 조금 힘들었을 수도 있겠어요. 스스로를 잘 돌봐주세요.";
            }

            return EmotionMessageResponseDTO.builder()
                    .todayScore(todayScore)
                    .changeType("ONLY_TODAY")
                    .message(message)
                    .build();
        }

        double yestScore = yestOpt.get().getScore();
        double diff = todayScore - yestScore;

        String message;
        String changeType;

        if (diff > 0.3) {
            message = "오늘은 어제보다 훨씬 나아졌네요! 변화가 느껴져요, 정말 멋져요.";
            changeType = "UP";
        } else if (diff > 0.1) {
            message = "조금씩 기분이 나아지고 있어요. 오늘도 잘 버텨줘서 고마워요.";
            changeType = "UP";
        } else if (diff < -0.3) {
            message = "마음이 힘들었겠네요. 그래도 여기까지 온 것만으로도 잘한 거예요.";
            changeType = "DOWN";
        } else if (diff < -0.1) {
            message = "오늘은 조금 무거운 하루였나 봐요. 괜찮아요, 그런 날도 있어요.";
            changeType = "DOWN";
        } else {
            message = String.format("어제와 비슷한 하루였네요. 오늘 점수는 %.1f점이에요.", todayScore);
            changeType = "SAME";
        }

        return EmotionMessageResponseDTO.builder()
                .todayScore(todayScore)
                .yesterdayScore(yestScore)
                .scoreDiff(Math.round(diff * 100.0) / 100.0)
                .changeType(changeType)
                .message(message)
                .build();
    }

}
