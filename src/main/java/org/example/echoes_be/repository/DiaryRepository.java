package org.example.echoes_be.repository;

import org.example.echoes_be.domain.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary,Long> {
}
