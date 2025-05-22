package org.example.echoes_be.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
// FROM AI -> 응답받는 형태
public class ScoreResponseDTO {
    private double score;
    private double state;
}
