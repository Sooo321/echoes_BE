package org.example.echoes_be;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.echoes_be.dto.DiarySaveRequestDTO;
import org.example.echoes_be.dto.UserLoginRequestDTO;
import org.example.echoes_be.dto.UserSignupRequestDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc
public class SignupLoginDiaryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 → 로그인(JWT) → 토큰 인증으로 일기 저장까지 통합 테스트")
    void signup_login_saveDiary_success() throws Exception {
        // 1. 회원가입
        UserSignupRequestDTO signupRequest = new UserSignupRequestDTO(
                "jwtuser",
                "jwtUser@example.com",
                "jwtPass123"
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // 2. 로그인 → 토큰 추출
        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO(
                "jwtUser@example.com",
                "jwtPass123"
        );

        MvcResult loginResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        JsonNode loginJson = objectMapper.readTree(loginResponse);
        String token = loginJson.get("response").get("token").asText();
        assertThat(token).isNotBlank();

        // 3. 토큰으로 일기 작성
        DiarySaveRequestDTO diaryRequest = new DiarySaveRequestDTO(
                "JWT 일기 테스트",
                "JWT 토큰으로 인증된 일기 작성",
                "2025-04-30"
        );

        MvcResult diaryResult = mockMvc.perform(post("/api/diary/save")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(diaryRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String diaryResponse = diaryResult.getResponse().getContentAsString();
        System.out.println("✅ 일기 작성 응답: \n" + diaryResponse);
    }
}
