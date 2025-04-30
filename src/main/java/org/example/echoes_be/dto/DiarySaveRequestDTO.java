package org.example.echoes_be.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiarySaveRequestDTO {
    private String title;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd") //프론트로부터 받는 문자열의 형태
    private String created_at;
}
