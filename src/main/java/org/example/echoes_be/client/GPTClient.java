package org.example.echoes_be.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GPTClient {

    @Value("${openai.api.key}") // application.yml 에서 OpenAI API Key 제공
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateAdvice(String diaryContent, List<String> emotionTags) {
       // 1. GPT 에게 제공할 프롬프트 생성
        String prompt = String.format("일기: \"%s\"\n감정 태그: %s\n사용자에게 따뜻한 조언을 해주세요.",
                diaryContent, String.join(", ", emotionTags));

        // 2. 요청 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

       // 3. 요청 Body 구성
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", "당신은 따뜻한 조언자입니다."),
                        Map.of("role", "user", "content", prompt)
                )
        );

        // 4. Header + Body -> HttpEntity 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 5. GPT API 호출
        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions", entity, Map.class);

        // 6. 응답에서 조언만 추출하기
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");

        return (String) message.get("content");
    }
}
