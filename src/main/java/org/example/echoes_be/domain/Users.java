package org.example.echoes_be.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter

public class Users {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
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

}


