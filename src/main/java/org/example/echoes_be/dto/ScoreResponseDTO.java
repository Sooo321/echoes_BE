package org.example.echoes_be.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ScoreResponseDTO {
    private double score;
    private double state;
}
