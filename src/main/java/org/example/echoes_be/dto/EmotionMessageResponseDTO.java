package org.example.echoes_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmotionMessageResponseDTO {
    private double todayScore;
    private Double yesterdayScore;  // null일 수 있음
    private Double scoreDiff;       // null일 수 있음
    private String changeType;      // "UP", "DOWN", "SAME", "ONLY_TODAY", "NO_TODAY"
    private String message;
}

