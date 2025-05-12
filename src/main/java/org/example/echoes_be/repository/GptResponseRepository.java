package org.example.echoes_be.repository;

import org.example.echoes_be.domain.GptResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GptResponseRepository extends JpaRepository<GptResponse, Long> {
    Optional<GptResponse> findByDiaryId(Long diaryId);
}
