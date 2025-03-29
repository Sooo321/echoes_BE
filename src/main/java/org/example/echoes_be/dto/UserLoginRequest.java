package org.example.echoes_be.dto;

public class UserLoginRequest {
    private String email;
    private String password;

    public UserLoginRequest(){
    }

    public UserLoginRequest(String email,String password){
        this.email = email;
        this.password = password;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(){
        this.password = password;
    }
}
