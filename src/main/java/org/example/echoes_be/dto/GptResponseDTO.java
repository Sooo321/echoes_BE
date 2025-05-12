package org.example.echoes_be.dto;

import lombok.*;
import org.example.echoes_be.domain.GptResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class GptResponseDTO {
    private Long id;             // 요청에서 넘겨받은 일기 ID
    private String response;
    private String emotion1;
    private String emotion2;
    private String createdAt;

    public GptResponseDTO(GptResponse entity) {
        this.id = entity.getId();
        this.response = entity.getResponse();
        this.emotion1 = entity.getEmotion1();
        this.emotion2 = entity.getEmotion2();

        // 원하는 형식: "yyyy-MM-dd HH:mm"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        this.createdAt = entity.getCreatedAt().format(formatter);
    }

}
