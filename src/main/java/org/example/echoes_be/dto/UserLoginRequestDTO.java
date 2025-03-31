package org.example.echoes_be.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserLoginRequestDTO {
    private String email;
    private String password;

    public UserLoginRequestDTO(){
    }

    public UserLoginRequestDTO(String email, String password){
        this.email = email;
        this.password = password;
    }

//    public String getEmail(){
//        return email;
//    }
//
//
//    public String getPassword(){
//        return password;
//    }
//
}
