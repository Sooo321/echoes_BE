package org.example.echoes_be.dto;

import lombok.*;
import org.example.echoes_be.domain.EmotionScore;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionScoreDTO {
    private Long id;
    private double prevScore;
    private double prevState;
    private double score;
    private double state;
    private String createdAt;

    public EmotionScoreDTO(EmotionScore entity) {
        this.id = entity.getId();
        this.prevScore = entity.getPrevScore();
        this.prevState = entity.getPrevState();
        this.score = entity.getScore();
        this.state = entity.getState();
        this.createdAt = entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
