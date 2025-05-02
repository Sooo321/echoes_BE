package org.example.echoes_be.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ReplyResponseDTO {
    private Long diaryId;             // 요청에서 넘겨받은 일기 ID
    private List<String> emotionTags; // 감정 분석 태그 (예: #행복, #기쁨)
    private String reply;             // GPT 생성 답장
}
