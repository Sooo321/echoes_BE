package org.example.echoes_be.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "Diary")

public class Diary {

    @Id
    @Column(name="diary_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "title", columnDefinition = "TEXT", nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    //사용자 감정 추가
    @Column(name = "user_emotion", length = 20)
    private String userEmotion;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Builder.Default
    @Column(name = "is_favorite")
    private boolean isFavorite = false;

    @Builder.Default
    @Column(name = "is_deleted")
    private boolean isDeleted = false;

}
