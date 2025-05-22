package org.example.echoes_be.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
// AI <- 감정 스코어링 요청
public class ScoreRequestDTO {
    private String title;
    private String content;

    private double prevScore;
    private double prevState;
}
