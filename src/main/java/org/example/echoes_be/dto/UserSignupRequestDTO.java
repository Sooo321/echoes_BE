package org.example.echoes_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignupRequestDTO {

    private String nickname;
    private String email;
    private String password;
    private String code;

    public UserSignupRequestDTO() {
    }

    public UserSignupRequestDTO(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
    }
}

