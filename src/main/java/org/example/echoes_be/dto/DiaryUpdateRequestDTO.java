package org.example.echoes_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryUpdateRequestDTO {
    private Long diaryId;            // 어떤 일기를 수정할지
    private String title;            // 수정할 제목 (optional)
    private String content;          // 수정할 내용 (optional)
}
