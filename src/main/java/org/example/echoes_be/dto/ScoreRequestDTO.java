package org.example.echoes_be.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScoreRequestDTO {
    private String title;
    private String content;

    private double prevScore;
    private double prevState;
}
