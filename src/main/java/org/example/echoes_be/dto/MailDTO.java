package org.example.echoes_be.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter

public class MailDTO {

    @NotBlank(message = "email is required")
    private String email;

    private String code;



}
