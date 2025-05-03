package org.example.echoes_be.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter

public class MailDTO {

    @NotBlank(message = "email is required")
    private String email;

    private String code;

    @Data
    @Builder
    public static class DiaryReplyResponse {
        private Long diaryId;             // 요청에서 넘겨받은 일기 ID
        private List<String> emotionTags; // 감정 분석 태그 (예: #행복, #기쁨)
        private String reply;             // GPT 생성 답장
    }
}
