package org.example.echoes_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DiaryCalendarResponseDTO {
    private LocalDate createdAt;
    private String userEmotion;
}
