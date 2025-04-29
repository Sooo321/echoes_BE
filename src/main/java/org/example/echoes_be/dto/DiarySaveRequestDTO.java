package org.example.echoes_be.dto;

import lombok.*;


import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiarySaveRequestDTO {
    private String title;
    private String content;
    private LocalDate created_at;

//    public DiarySaveRequestDto(){
//    }
//
//    public DiarySaveRequestDto(String content,LocalDate created_at){
//        this.content = content;
//        this.created_at = created_at;
//    }
}
