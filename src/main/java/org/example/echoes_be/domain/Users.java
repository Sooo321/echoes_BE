package org.example.echoes_be.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", nullable = false)
    private String nickname;
    //@Column : 칼럼 이름을 커스텀으로 지정하기 위해, NOT NULL 설정을 하기 위해,특정한 데이터 베이스 설정을 지정하기 위해, 명시적으로 매핑 의도를 분명하게 하기 ㅜ이해

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    private Long createdDate; // 가입 날짜

    private Long accessDate; // 접속 날짜


    public Users(){
        //기본 생성자
        //클래스가 객체로 생성될 때 아무런 값을 전달하지 않아도 호출될 수 있는 생성자
        //@Autowired 로 주입할 때나 JPA 엔티티를 사용할 때 기본 생성자가 필요함.
    }

    public Users(String nickname, String email, String password, Long accessDate, Long createdDate) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.createdDate = createdDate;
        this.accessDate = accessDate;

    }


}




