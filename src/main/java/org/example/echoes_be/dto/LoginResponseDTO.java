package org.example.echoes_be.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private Long id;
    private String nickname;
    private String email;
    private String token;
    private String refreshToken;
}
