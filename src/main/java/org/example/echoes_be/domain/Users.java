package org.example.echoes_be.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "access_date")
    private Long accessDate;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Diary> diaries = new ArrayList<>();

    //OneTomany 관계로 Diary와 연결
    //cascade = CascadeType.ALL : 유저가 삭제되면 Diary도 함께 삭제 됨
    //orphanRemoval = true : 유저에서 diary를 제거하면 diary도 삭제됨
    //List <Diary> diaries : 여러개의 diary를 가질 수 있음.

    public Users(){
        //기본 생성자
        //@Autowired 로 주입할 때나 JPA 엔티티를 사용할 때 기본 생성자가 필요함.
    }

    public Users(String nickname, String email, String password, Long accessDate) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.accessDate = accessDate;
    }

    //@Column : 칼럼 이름을 커스텀으로 지정하기 위해, NOT NULL 설정을 하기 위해,특정한 데이터 베이스 설정을 지정하기 위해, 명시적으로 매핑 의도를 분명하게 하기 위해


}


