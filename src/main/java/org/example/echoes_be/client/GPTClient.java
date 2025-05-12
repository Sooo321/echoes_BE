package org.example.echoes_be.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.echoes_be.dto.GptResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GPTClient {

    @Value("${openai.api.key}") // application.yml 에서 OpenAI API Key 제공
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public GptResponseDTO generateAdvice(String diaryContent) {
       // 1. GPT 에게 제공할 프롬프트 생성
        String prompt = String.format(
                "다음은 사용자의 일기입니다: \"%s\".\n"
                        + "심리적 지지자로서, 이 일기에 대해 따뜻한 조언을 1개, 그리고 일기 내용과 가장 관련이 깊은 감정을 나타내는 키워드를 2개 생성해줘. "
                        + "출력 형식은 반드시 JSON으로:\n"
                        + "{ \"response\": \"...\", \"emotion1\": \"...\", \"emotion2\": \"...\" }",
                diaryContent);

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
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = (String) message.get("content");

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(content, GptResponseDTO.class);
        } catch (IOException e) {
            throw new RuntimeException("GPT 응답 파싱 실패: " + e.getMessage(), e);
        }
    }
}
