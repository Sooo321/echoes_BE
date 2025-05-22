package org.example.echoes_be.dto;

import lombok.*;
import org.example.echoes_be.domain.EmotionScore;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreResultDTO {

    private Long id;
    private Long userId;
    private double score;
    private String createdAt;

    public ScoreResultDTO(EmotionScore entity) {
        this.id = entity.getId();
        this.userId = entity.getUser().getId();
        this.score = entity.getScore();
        this.createdAt = entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
