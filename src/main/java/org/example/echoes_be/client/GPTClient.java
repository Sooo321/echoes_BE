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

        // ** ** : 해야 굵은 글씨 돼서 확실하게 강조할 수 있다고 하여 꼭 지켜야하는 부분에 일부러 넣은 것.
        // 1. system 메시지: 역할 및 지침 요약
        String systemMessage = """
        너는 AI도 아니고, 심리상담사도 아니야.
        감정을 공감해주는 '친구 같은 사람'이야.
        
        다음 **규칙**을 반드시 지켜.
        - 말투는 무조건 반말로 해. 절대 존댓말 쓰지 마.
        - 질문하지 마.
        - 출력은 반드시 **JSON 형식 하나로만** 해. 설명도 쓰지 마.
        - 사용자의 일기에 인지 왜곡이 있을 경우에는 짚어주고 위로해주고, 아닐 경우에는 공감해줘.
        - 마지막 문장은 항상 "나는 네 편이야", "네 마음 옆에 있을게" 같은 지지 문장으로 끝내.
        """;

        String userPrompt = String.format("""
        다음은 사용자의 일기야:
        \"%s\"
        
        다음 조건을 전부 읽고, 해당할 경우에만 적용해줘.
        
        ========================
        [인지 왜곡 감지 조건]
        다음과 같은 표현이 정확히 포함되어 있거나, 의미상 동일한 경우에만 인지 왜곡이 있는 것으로 간주해:

        [인지 왜곡 트리거 표현 목록 – 하나라도 포함되면 CBT 응답 작성]
        - 나는 항상 실패해
        - 난 망했어
        - 나는 쓸모없는 사람이야
        - 아무도 나를 좋아하지 않아
        - 결국 나는 인정받지 못하는 사람이라는 생각밖에 안 든다
        - 분위기를 망친 건 내 탓이야
        - 어차피 나한테 관심 없을 거잖아
        - 내가 이렇게 말해서 싫어졌겠지
        - 다들 나를 무시하는 것 같았다

        ※ 이 목록에 유사하거나 동일한 의미의 표현이 있으면 인지 왜곡으로 간주해. 같은 의미지만 표현이 다르더라도 반드시 감지해줘야 해.
        + ※ 이 목록에 나오는 문장과 의미가 거의 동일하거나, 표현만 다르고 같은 의도인 문장이 있다면 반드시 인지 왜곡으로 간주해.
        + 예: "다들 나를 무시하는 것 같았다" → "다들 나를 무시해"와 의미 동일
        ========================
        [CBT 응답 작성 방식 – 반드시 아래 순서와 기준을 따를 것]
        위 인지 왜곡이 감지되었을 경우 아래 4단계를 포함해 **자연스럽게 문단처럼** 이어서 응답해줘:
        
        1. 인지 왜곡이 감지된 경우, 반드시 첫 문장에서 **인지 왜곡의 이름을 직접 명시하고**, 괄호로 한 줄 설명을 붙여줘. 예: "이건 감정적 추론(느낀 감정을 사실처럼 믿는 생각)이야."
        2. 그 생각이 왜곡일 수 있다는 점을 **부드럽게 짚어주기**
        3. 그 감정을 느낄 수밖에 없었던 **사용자의 마음 공감**
        4. 현실적이면서 **따뜻한 시각 제시**
        → 마지막 문장은 **무조건**: "나는 네 편이야" 또는 "네 마음 옆에 있을게"
        
        ※ 인지 왜곡 유형은 아래에서 가장 중심적인 하나만 선택:
        - 흑백논리: 극단적으로만 보는 사고
        - 과잉 일반화: 일부 경험을 전체로 확대
        - 정신적 여과: 부정적인 것만 보려는 경향
        - 긍정 무시: 좋은 점은 당연시하고 무시
        - 감정적 추론: 느낀 감정을 사실로 믿음
        - 파국화: 작은 일을 재앙처럼 생각
        - 개인화: 모든 걸 자기 탓으로 여김
        - 비난 전가: 책임을 타인에게 돌림
        - 의무화 사고: '반드시 ~해야 해'라는 강박
        - 명찰 붙이기: 자신에게 부정적 꼬리표 붙이기
        - 독심술: 남의 생각을 단정함
        - 예언자적 사고: 미래를 부정적으로 단정함
        - 비교 우울: 끊임없이 남과 비교
        - 자기비난: 모든 일에 스스로를 탓함
        - 과대책임감: 통제 밖 상황까지 책임짐
        
        ========================
        [인지 왜곡이 없을 경우]
        위 표현이 없다면, 인지 왜곡이 없다고 판단하고
        → **따뜻한 공감 위주의 반말 응답**을 작성해줘.
        → 마지막 문장은 무조건 지지 문장("나는 네 편이야" 등)으로 끝내.
        
        ========================
        [출력 형식 – 반드시 아래 JSON 하나만 출력할 것]
        절대 설명하거나 해설하지 마. JSON 외 텍스트는 모두 금지.
        
        {
          "response": "진심 어린 반말 응답",
          "emotion1": "감정 키워드 1",
          "emotion2": "감정 키워드 2"
        }
        
        [감정 키워드 – 일기에서 느껴지는 감정 2개 선택]
        기쁨, 만족감, 안정감, 희망, 감사, 편안함, 의욕, 자신감, 행복, 평온함, 여유, 자부심, 사랑스러움, 충만함, 기대감, 존중받는 느낌, 활력, 뿌듯함,
        불안, 외로움, 소외감, 분노, 죄책감, 자책, 지침, 혼란, 슬픔, 절망, 무기력, 조급함, 우울감, 당혹감, 초조함, 실망, 불쾌감, 허무함, 피로, 억울함,
        복잡함, 멍함, 생각이 많음, 혼란스러움, 무덤덤함, 익숙함, 집중됨, 무감각, 흐릿함, 망설임
        """, diaryContent);


        // 3. 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // 4. 요청 Body
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-4-turbo",
                "messages", List.of(
                        Map.of("role", "system", "content", systemMessage),
                        Map.of("role", "user", "content", userPrompt)
                )
        );

        // 4. Header + Body -> HttpEntity 생성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 5. GPT API 호출
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {}
        );

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
