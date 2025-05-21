package org.example.echoes_be.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.example.echoes_be.dto.ScoreRequestDTO;
import org.example.echoes_be.dto.ScoreResponseDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
@RequiredArgsConstructor
public class AiClient {

    // HTTP 요청을 보내기 위한 Spring 의 기본 클라이언트
    private final RestTemplate restTemplate;

    // application.yml 에서 주입받은 AI 서버의 URL
    @Value("${ai.api.url}")
    private String aiApiUrl;

    public ScoreResponseDTO requestEmotionScore(ScoreRequestDTO request) {

        /*
        // 1. HTTP 헤더 설정 (Content-Type: application/json)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. 본문 + 헤더를 하나의 HttpEntity 로 구성
        HttpEntity<ScoreRequestDTO> entity = new HttpEntity<>(request, headers);

        // 3. AI 서버에 POST 요청 전송, 응답을 ScoreResponse 로 받음
        ResponseEntity<ScoreResponseDTO> response = restTemplate.exchange(
                aiApiUrl,                 // 요청 URL
                HttpMethod.POST,         // HTTP 메서드
                entity,                  // 요청 본문 및 헤더
                ScoreResponseDTO.class      // 응답 받을 타입
        );

        // 3-1. 테스트용 코드
        System.out.println("요청 데이터: 제목=" + request.getTitle() + ", 내용=" + request.getContent());


        // 4. 응답 본문(감정 점수 DTO) 반환
        return response.getBody();
        */

        //==================
        // ✅ 테스트용 가짜 응답
        System.out.println("[Mock AI 호출]");
        System.out.println("제목: " + request.getTitle());
        System.out.println("내용: " + request.getContent());
        System.out.println("prevScore: " + request.getPrevScore());
        System.out.println("prevState: " + request.getPrevState());

        // 임의의 점수 응답을 반환
        return ScoreResponseDTO.builder()
                .score(80.0)    // 테스트용 감정 점수
                .state(5.0)     // 테스트용 감정 상태
                .build();


    }
}