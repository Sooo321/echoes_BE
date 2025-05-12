package org.example.echoes_be.domain;


import jakarta.persistence.*;
import lombok.*;

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

    @OneToOne
    @JoinColumn(name = "diary_id", unique = true)
    private Diary diary;

    // 이전 감정 점수 및 상태 (요청 시 사용)
    private double prevScore;
    private double prevState;

    // 이번 감정 점수 및 상태 (응답 시 저장)
    private double score;
    private double state;
}
