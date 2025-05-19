package org.example.echoes_be.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.echoes_be.dto.GptResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
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
//        String prompt = String.format(
//                "다음은 사용자의 일기입니다: \"%s\".\n"
//                        + "심리적 지지자로서, 이 일기에 대해 따뜻한 조언을 1개, 그리고 일기 내용과 가장 관련이 깊은 감정을 나타내는 키워드를 2개 생성해줘. "
//                        + "출력 형식은 반드시 JSON으로:\n"
//                        + "{ \"response\": \"...\", \"emotion1\": \"...\", \"emotion2\": \"...\" }",
//                diaryContent);
        // 1. system 메시지: 역할 및 지침 요약
        String systemMessage = """
            당신은 AI도 아니고, 심리상담사도 아니야.
            감정을 공감해주는 ‘친구 같은 사람’이야.
            반드시 아래 말투 규칙을 지켜야 해:
            - 반말로만 작성해. 절대 존댓말 쓰지 마.
            - 질문하지 마.
            - 편지 인삿말(안녕, 사랑하는 너에게 등) 쓰지 마.
            - 마지막엔 반드시 "나는 네 편이야", "네 마음 옆에 있을게" 같은 지지 문장으로 끝내.
            출력은 반드시 JSON 형식만 사용해.
    """;

        // 2. user 메시지: 실제 프롬프트 요청
        String userPrompt = String.format("""
            다음은 사용자의 일기입니다: \"%s\".

            다음 조건에 따라 반드시 CBT 기법을 기반으로 진심 어린 응답을 작성해줘:

            [출력 형식 – 반드시 이 JSON 형식으로만 응답하세요]
            {
              "response": "CBT 기법을 적용한 따뜻한 응답",
              "emotion1": "감정 키워드 1",
              "emotion2": "감정 키워드 2"
            }

            - 절대로 JSON 외의 어떤 문장도 포함하지 마.
            - 설명, 해설, 인삿말 없이 JSON만 정확히 출력해줘.

            [CBT 응답 방식 – 반드시 아래 기준에 따라 판단]
            - 다음과 같은 표현이 있다면 자동적 사고(= 인지 왜곡)가 있는 것이므로, 반드시 아래 4단계를 모두 포함해 응답해줘:
            예시 문장:
            - "나는 항상 실패해"
            - "나는 쓸모없는 사람이야"
            - "아무도 나를 좋아하지 않아"
            - "난 망했어"
            - "다들 나를 무시하는 것 같았다"
            - "결국 나는 인정받지 못하는 사람이라는 생각밖에 안 든다"
            - "분위기를 망친 건 내 탓이야"
            - "어차피 나한테 관심 없을 거잖아"
            - "내가 이렇게 말해서 싫어졌겠지"
            
            이처럼:
            - 자신을 비하하거나 무가치하게 느끼는 문장
            - 타인의 의도를 마음대로 추측하거나 단정하는 문장
            - 부정적 상황을 확대 해석하거나 일반화하는 문장
            - 미래를 비관적으로 예단하거나 예언하는 문장
            
            위와 같은 표현이 하나라도 있다면,아래 4단계를 포함해서 문단처럼 자연스럽게 이어서 작성해줘:
             1. 어떤 인지 왜곡인지 정확히 이름 언급 후, **반드시** 간단히 설명해줘
             2. 그 생각이 왜곡일 수 있음을 부드럽게 짚어주기
             3. 그 감정에 빠질 수밖에 없었던 사용자의 마음 공감하기
             4. 그 감정을 덜어줄 수 있는 따뜻한 현실적 시각 제시하기

            - 인지 왜곡이 없고, 자동적 사고도 발견되지 않을 경우에만 위로 중심의 따뜻한 반말 응답을 해줘.
            - 위로 응답도 반드시 4~5문장 이상, 감정 공감 + 지지 문장으로 마무리해야 해.

            
            [주의사항: 인지 왜곡 종류 – 이름 + 한줄 설명 포함]
            - 인지 왜곡이 감지되면 반드시 아래 중에서 가장 중심적인 왜곡을 선택하세요:
            - 흑백논리: 모든 일을 극단적으로만 보는 생각 (예: 전부 아니면 전혀 아님)
            - 과잉 일반화: 한두 번의 일로 모든 상황을 단정함
            - 정신적 여과: 부정적인 것만 골라서 보는 경향
            - 긍정 무시: 긍정적인 사실이나 성과를 당연하게 여기며 무시함
            - 감정적 추론: 느낀 감정을 사실이라고 믿는 생각
            - 파국화: 사소한 일도 큰 재앙처럼 여김
            - 개인화: 무슨 일이든 전부 자기 탓으로 돌림
            - 비난 전가: 모든 책임을 남에게 돌리는 생각
            - 의무화 사고: ‘반드시 ~해야 한다’는 강박적 생각
            - 명찰 붙이기: 자신이나 타인에게 부정적인 꼬리표를 붙임
            - 독심술: 다른 사람이 자신을 어떻게 생각할지 단정지음
            - 예언자적 사고: 미래를 부정적으로 단정해서 예측함
            - 비교 우울: 남과 끊임없이 비교해서 자신을 깎아내림
            - 자기비난: 일어난 모든 일에 대해 자신을 탓함
            - 과대책임감: 내 통제를 벗어난 일까지 책임을 느끼는 상태

            
            [감정 키워드 목록 - 반드시 일기 내용에서 느껴지는 감정을 기준으로 선택]
            - 기쁨, 만족감, 안정감, 희망, 감사, 편안함, 의욕, 자신감, 행복, 평온함, 여유, 자부심, 사랑스러움, 충만함, 기대감, 존중받는 느낌, 활력, 뿌듯함,
              불안, 외로움, 소외감, 분노, 죄책감, 자책, 지침, 혼란, 슬픔, 절망, 무기력, 조급함, 우울감, 당혹감, 초조함, 실망, 불쾌감, 허무함, 피로, 억울함,
              복잡함, 멍함, 생각이 많음, 혼란스러움, 무덤덤함, 익숙함, 집중됨, 무감각, 흐릿함, 망설임
            """, diaryContent);

        // 3. 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 4. 요청 Body
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", systemMessage),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        // 4. Header + Body -> HttpEntity 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 5. GPT API 호출
//        ResponseEntity<Map> response = restTemplate.postForEntity(
//                "https://api.openai.com/v1/chat/completions", entity, Map.class);
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        // 6. 응답에서 조언만 추출하기
//        @SuppressWarnings("unchecked")
//        List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
//        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
//        String content = (String) message.get("content");
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
