package org.example.echoes_be.client;

import lombok.RequiredArgsConstructor;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.EmotionScore;
import org.example.echoes_be.repository.DiaryRepository;
import org.example.echoes_be.repository.EmotionScoreRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ReportClient {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final DiaryRepository diaryRepository;
    private final EmotionScoreRepository emotionScoreRepository;

    public String generateReport(Long userId) {
        LocalDate fromDate = LocalDate.now().minusDays(7);
        LocalDateTime fromDateTime = fromDate.atStartOfDay();  // 자정(LocalDateTime)으로 변환

        List<Diary> diaries = diaryRepository.findByUserIdAndCreatedAtAfter(userId, fromDate);
        List<EmotionScore> scores = emotionScoreRepository.findByUserIdAndCreatedAtAfter(userId, fromDateTime);

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
        다음은 감정 점수 해석 기준이야:
0~9: 매우 위험 (심각 우울)
10~19: 우울증 위험군
20~29: 부정적 사고 경향
30~39: 최근 스트레스·어려움
40~59: 안정적 일상
60~69: 긍정 경험 다수
70~79: 긍정적 사고 우세
80~89: 높은 낙관적 태도
90~100: 극도의 긍정 상태

다음은 사용자의 최근 7일 일기 및 감정 데이터야:
""");
        for (Diary diary : diaries) {
            prompt.append(String.format("- [%s] %s\n", diary.getCreatedAt(), diary.getContent()));
        }
        for (EmotionScore score : scores) {
            prompt.append(String.format("- [%s] 점수 %.1f 상태 %.1f\n", score.getCreatedAt(), score.getScore(), score.getState()));
        }

        prompt.append("""
위 데이터를 참고하여, 아래 형식으로 감정 리포트를 마크다운 텍스트로 작성해줘.  
각 항목은 귀여운 이모지와 함께 작성하고, 추천 할 일은 반드시 번호 리스트(체크리스트) 형태로 작성해!  

# 🌤 감정 리포트: 오늘의 마음 날씨

---

### 📝 [일기 요약]

- **지난 7일간의 주요 일기 요약을 간결하게 작성 (리스트)**

> 🌈 감정 흐름을 귀여운 표현으로 설명 (예: 감정의 흐름이 구름 사이 햇살처럼 밝아지고 있어요!)

---

### 📊 [감정 추이 분석]

- **일별 감정 점수 및 상태 변화를 요약**
- **감정 점수 해석에 따른 변화 요약 (예: "중립 ➡️ 긍정"으로 변화)**

| 날짜 | 점수 | 상태 |
| --- | --- | --- |
| ... | ... | ... |

> 🩵 전반적인 감정은 점점 회복 중이며, 긍정적인 방향으로 향하고 있어요.

---

### 🌈 [주요 감정 및 추이]

- **최근 일주일간 감정 패턴과 변화 요약**
- **긍정/부정 비율, 감정 기복 등 분석**

> 😊 최근 감정은 점점 안정되며 긍정적인 흐름으로 바뀌고 있어요. 꾸준히 감정을 잘 관리하고 있어요!

---

### 🌱 [오늘의 추천 할 일]

1. 🧘‍♀️ **마음 산책하기**  
   - 자연 속을 거닐며 생각을 정리해보세요.
2. ✍️ **감정 일기 작성하기**  
   - 감정을 적어보는 것만으로도 마음이 정리돼요.
3. 🎵 **좋은 음악 듣기**  
   - 감정을 부드럽게 만들어줄 재즈나 클래식 음악 추천!
4. ☕ **따뜻한 음료로 힐링**  
   - 카페나 집에서 따뜻한 차 한 잔으로 마음을 달래보세요.

---

### 💬 [마무리 메시지]

> 💖 감정은 늘 흐르는 강물 같아요.  
> 오늘의 햇살과 따뜻한 차처럼, 당신의 하루도 평화롭길 바랄게요.  
> **당신은 이미 충분히 잘하고 있어요!**

""");


        // GPT 호출
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "너는 따뜻한 감정 리포트를 작성하는 친구야."),
                        Map.of("role", "user", "content", prompt.toString())
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {
                }
        );

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        return (String) message.get("content");  // GPT의 프리포맷 텍스트 그대로 반환
    }
}