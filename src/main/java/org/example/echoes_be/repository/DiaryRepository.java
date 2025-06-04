package org.example.echoes_be.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.dto.DiaryCalendarResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 소프트 삭제된 항목은 제외하고 조회
    @Query("SELECT d FROM Diary d WHERE d.user.id = :userId AND d.isDeleted = false")
    List<Diary> findByUserIdAndNotDeleted(@Param("userId") Long userId);

    //일기 조회
    Optional<Diary> findByUserIdAndCreatedAtAndIsDeletedFalse(Long userId, LocalDate createdAt);

    //즐겨찾기한 일기 조회(내림차순)
    List<Diary> findByUserIdAndIsFavoriteTrueAndIsDeletedFalseOrderByCreatedAtDesc(Long userId);


    //달력에서 해당하는 날짜에 일기가 있는지 없는지
    @Query("""
    SELECT new org.example.echoes_be.dto.DiaryCalendarResponseDTO(d.createdAt, d.userEmotion)
    FROM Diary d
    WHERE d.user.id = :userId
      AND d.isDeleted = false
      AND FUNCTION('YEAR', d.createdAt) = :year
      AND FUNCTION('MONTH', d.createdAt) = :month
""")
    List<DiaryCalendarResponseDTO> findCalendarEntriesByMonth(@Param("userId") Long userId,
                                                              @Param("year") int year,
                                                              @Param("month") int month);



    // USER 기준, 최근 일주일 일기 조회
    List<Diary> findByUserIdAndCreatedAtAfter(Long userId, LocalDate date);
}
