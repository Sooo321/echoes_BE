package org.example.echoes_be.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class GptResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "diary_id")
    private Diary diary;

    private String response;
    private String emotion1;
    private String emotion2;

    private LocalDateTime createdAt = LocalDateTime.now().withSecond(0).withNano(0);
}