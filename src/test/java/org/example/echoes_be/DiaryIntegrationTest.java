package org.example.echoes_be;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.echoes_be.dto.DiarySaveRequestDTO;
import org.example.echoes_be.dto.DiaryUpdateRequestDTO;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class DiaryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        // 회원가입
        UserSignupRequestDTO signup = new UserSignupRequestDTO(
                "testuser", "testuser@example.com", "testpassword"
        );
        mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signup)));

        // 로그인 & 토큰 추출
        UserLoginRequestDTO login = new UserLoginRequestDTO("testuser@example.com", "testpassword");
        MvcResult result = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(response);
        token = json.get("response").get("token").asText();
    }

    @Test
    @DisplayName("1. 일기 저장 → 2. 조회 → 3. 수정 → 4. 삭제 통합 테스트")
    void diaryCrudTest() throws Exception {
        // 1. 저장
        DiarySaveRequestDTO saveRequest = new DiarySaveRequestDTO(
                "첫 일기", "내용입니다.", "2025-05-01"
        );

        MvcResult saveResult = mockMvc.perform(post("/api/diary/save")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(saveRequest)))
                .andExpect(status().isOk())
                .andReturn()
                ;


        JsonNode saved = objectMapper.readTree(saveResult.getResponse().getContentAsString());
        Long diaryId = saved.get("response").get("id").asLong(); //다이어리 아이디 저장

        // 2. 조회
        mockMvc.perform(get("/api/diary/date")
                        .param("date", "2025-05-01")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response").exists())
                .andExpect(jsonPath("$.response.diaryId").isNumber())
                .andExpect(jsonPath("$.response.title").value("첫 일기"))
                .andExpect(jsonPath("$.response.content").value("내용입니다."))
                .andExpect(jsonPath("$.response.createdAt").value("2025-05-01"))
                .andExpect(jsonPath("$.response.favorite").value(false));



        // 3. 수정
        DiaryUpdateRequestDTO updateRequest = new DiaryUpdateRequestDTO(
                diaryId,
                "수정된 제목", "수정된 내용"
        );

        mockMvc.perform(post("/api/diary/update")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.response.title").value("수정된 제목"))
                .andExpect(jsonPath("$.response.content").value("수정된 내용"))
                .andDo(result -> {
                    System.out.println("응답: " + result.getResponse().getContentAsString());
                });

        // 4. 삭제
        DiaryDeleteRequestDTO deleteRequest = new DiaryDeleteRequestDTO(diaryId);

        mockMvc.perform(post("/api/diary/delete")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new DiaryDeleteRequestDTO(diaryId))))
                .andExpect(status().isOk());
    }
}

