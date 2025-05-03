package org.example.echoes_be.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.echoes_be.domain.Diary;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiaryResponseDTO {
    private Long diaryId;
    private String title;
    private String content;
    private String createdAt;
    private boolean isFavorite;

    public static DiaryResponseDTO fromEntity(Diary diary) {
        return DiaryResponseDTO.builder()
                .diaryId(diary.getId())
                .title(diary.getTitle())
                .content(diary.getContent())
                .createdAt(diary.getCreatedAt().toString())
                .isFavorite(diary.isFavorite())
                .build();
    }
}
