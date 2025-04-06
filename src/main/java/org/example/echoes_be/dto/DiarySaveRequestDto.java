package org.example.echoes_be.dto;

import lombok.Data;


import java.time.LocalDate;

@Data
public class DiarySaveRequestDto {
    private String content;
    private LocalDate created_at;

    public DiarySaveRequestDto(){
    }

    public DiarySaveRequestDto(String content,LocalDate created_at){
        this.content = content;
        this.created_at = created_at;
    }
}
