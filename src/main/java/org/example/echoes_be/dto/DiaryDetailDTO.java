package org.example.echoes_be.dto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryDetailDTO {
    private Long diaryId;
    private String title;
    private String content;
    private String userEmotion;
    private LocalDate createdAt;
    private Boolean gptResponse;
    private String gptEmotion1;
    private String gptEmotion2;


    private Double emotionScore;
}
