package org.example.echoes_be.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 하나의 일기에 대해 날짜별로 score 값을 저장하기 위함.
    @OneToOne
    @JoinColumn(name = "diary_id", unique = true, nullable = false)
    private Diary diary;

    // 사용자별 감정 스코어링 분석을 진행하기 위함. -> 유저 정보 필요
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;


    // 이전 감정 점수 및 상태 (요청 시 사용)
    private double prevScore;
    private double prevState;

    // 이번 감정 점수 및 상태 (응답 시 저장)
    private double score;
    private double state;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
