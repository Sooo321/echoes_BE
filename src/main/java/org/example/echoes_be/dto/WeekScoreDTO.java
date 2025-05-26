package org.example.echoes_be.dto;


import lombok.*;
import org.example.echoes_be.domain.EmotionScore;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class WeekScoreDTO {
    private double score;
    private String createdAt;

    public WeekScoreDTO(EmotionScore entity) {
        this.score = entity.getScore();
        this.createdAt = entity.getCreatedAt().toLocalDate().toString();
    }

}
