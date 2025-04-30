package org.example.echoes_be;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.echoes_be.dto.UserLoginRequestDTO;
import org.example.echoes_be.dto.UserSignupRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserLoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUpUser() throws Exception {
        // 로그인 테스트용 계정 미리 생성
        UserSignupRequestDTO signupRequest = new UserSignupRequestDTO(
                "loginuser",
                "loginuser@example.com",
                "password123"
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 성공: 올바른 토큰과 사용자 정보 반환")
    void login_success() throws Exception {
        // given
        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO(
                "loginuser@example.com",
                "password123"
        );

        // when
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("로그인 응답:\n" + responseBody);

        JsonNode json = objectMapper.readTree(responseBody);
        assertThat(json.get("success").asBoolean()).isTrue();
        assertThat(json.get("response").get("token").asText()).isNotEmpty();
        assertThat(json.get("response").get("email").asText()).isEqualTo("loginuser@example.com");
    }

//    @Test
//    @DisplayName("로그인 실패: 잘못된 비밀번호")
//    void login_fail_wrong_password() throws Exception {
//        // given
//        UserLoginRequestDTO loginRequest = new UserLoginRequestDTO(
//                "loginuser@example.com",
//                "wrongpassword"
//        );
//
//        // when
//        MvcResult result = mockMvc.perform(post("/api/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginRequest)))
//                .andExpect(status().isBadRequest()) // 실패 시 400 기대
//                .andReturn();
//
//        // then
//        String responseBody = result.getResponse().getContentAsString();
//        System.out.println("로그인 실패 응답:\n" + responseBody);
//
//        JsonNode json = objectMapper.readTree(responseBody);
//        assertThat(json.get("success").asBoolean()).isFalse();
//        assertThat(json.get("error").asText()).contains("비밀번호");
//    }
}
