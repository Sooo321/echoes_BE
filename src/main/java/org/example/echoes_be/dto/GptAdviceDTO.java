package org.example.echoes_be.dto;

import lombok.*;
import org.example.echoes_be.domain.GptResponse;

import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class GptAdviceDTO {
    private Long id;
    private String response;
    private String createdAt;

    public GptAdviceDTO(GptResponse entity) {
        this.id = entity.getId();
        this.response = entity.getResponse();
        this.createdAt = entity.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }
}
