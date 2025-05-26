package org.example.echoes_be.dto;

import lombok.*;
import org.example.echoes_be.domain.GptResponse;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GptEmotionDTO {
    private Long id;
    private String emotion1;
    private String emotion2;
    private String createdAt;

    public GptEmotionDTO(GptResponse entity) {
        this.id = entity.getId();
        this.emotion1 = entity.getEmotion1();
        this.emotion2 = entity.getEmotion2();
        this.createdAt = entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
