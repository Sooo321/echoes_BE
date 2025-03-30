package org.example.echoes_be.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "users")  // 'user'는 예약어라 'users'로 테이블명 지정
public class Users {

    @Id //pk
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키(PK) 값을 자동으로 생성해주는 어노테이션

    private Long id;
    private String nickname;
    private String email;
    private String password;
    private Long accessDate;

    public Users(){
        //기본 생성자
        //클래스가 객체로 생성될 때 아무런 값을 전달하지 않아도 호출될 수 있는 생성자
        //@Autowired 로 주입할 때나 JPA 엔티티를 사용할 때 기본 생성자가 필요함.
    }
    public Users(String nickname, String email, String password, Long accessDate){
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.accessDate = accessDate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname){
        this.nickname = nickname;
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

    public void setPassword(String password){
        this.password = password;
    }

    public String getAccessDate(String accessDate){
        return accessDate;
    }

    public void setAccessDate(Long accessDate){
        this.accessDate = accessDate;
    }
}


