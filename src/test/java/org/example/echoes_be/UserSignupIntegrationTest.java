package org.example.echoes_be;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.echoes_be.dto.UserSignupRequestDTO;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserSignupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공: 올바른 값이 저장된다")
    void signup_success() throws Exception {
        // given
        UserSignupRequestDTO signupRequest = new UserSignupRequestDTO(
                "testuser",
                "testuser@example.com",
                "password123"
        );

        // when
        MvcResult result = mockMvc.perform(post("/api/auth/signUp")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andDo(print()) // 요청/응답 전체 로그
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();

        System.out.println("회원가입 응답 JSON:\n" + responseBody);
        JsonNode json = objectMapper.readTree(responseBody);

        assertThat(json.get("success").asBoolean()).isTrue();
        assertThat(json.get("response").get("nickname").asText()).isEqualTo("testuser");
        assertThat(json.get("response").get("email").asText()).isEqualTo("testuser@example.com");

    }


}
