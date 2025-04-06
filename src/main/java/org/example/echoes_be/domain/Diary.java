package org.example.echoes_be.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "Diary")

public class Diary {

    @Id
    @Column(name="Diary_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;


    @Column(name = "created_at", nullable = false)
    private LocalDate created_at;

    @Column(name = "is_favorite")
    @Builder.Default
    private boolean is_favorite = false;

    @Column(name = "is_deleted")
    @Builder.Default
    private boolean is_deleted = false;


    public Diary(){
        //기본 생성자
    }
    public Diary(Long id, String content, LocalDate created_at, boolean is_favorite, boolean is_deleted ){
        this.id = id;
        this.content = content;
        this.created_at = created_at;
        this.is_favorite = is_favorite;
        this.is_deleted = is_deleted;
    }


    //columnDefinition : columnDefinition은 JPA에서 데이터베이스 테이블의 컬럼을 정의할 때, 직접 SQL의 데이터 타입을 지정하기 위해 사용하는 속성.
    //TEXT : 일반 텍스트, Markdown 형식 등 다양한 포맷을 저장할 수 있음. 저장된 데이터를 가공하거나 렌더링 하기에도 적합함. 일반적인 검색도 가능함.

    //@Builder.Default
    //Lombok의 @Builder는 내부적으로 빌더 패턴을 사용하여 객체를 생성함. 이 과정에서 모든 필드는 명시적으로 값이 지정되지 않으면 기본값으로 초기화되지 않음. 따라서 @Builder.Default를 사용해서 초기값을 지정해 줘야 함.


}
