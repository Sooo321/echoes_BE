package org.example.echoes_be.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.DiaryCalendarResponseDTO;
import org.example.echoes_be.dto.DiaryDetailDTO;
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

    //즐겨찾기한 일기 조회
//    List<Diary> findByUserIdAndIsFavoriteTrueAndIsDeletedFalse(Long userId);

    //즐겨찾기한 일기 조회
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
           

//    //일기조회 + DiaryDetail
//    @Query("""
//    SELECT new org.example.echoes_be.dto.DiaryDetailDTO(
//        d.id,
//        d.title,
//        d.content,
//        d.userEmotion,
//        d.createdAt,
//        g.gptResponse,
//        g.emotion1,
//        g.emotion2,
//        e.score
//    )
//    FROM Diary d
//    LEFT JOIN GptResponse g ON d = g.diary
//    LEFT JOIN EmotionScore e ON d = e.diary
//    WHERE d.user.id = :userId
//      AND d.createdAt = :createdAt
//      AND d.isDeleted = false
//""")
//    Optional<DiaryDetailDTO> findDetailByUserIdAndCreatedAt(
//            @Param("userId") Long userId,
//            @Param("createdAt") LocalDate createdAt
//    );



    //일기 저장 시에 오늘의 감정 - 답장이 오면 확인할 수 있어요
    //를 위해 left join으로 null 처리
    //응답 부분도 일단 null처리해서
//    //응답이 올 경우 변화함.
//    @Query("""
//    SELECT new org.example.echoes_be.dto.DiaryDetailDTO(
//        d.id,
//        d.title,
//        d.content,
//        d.userEmotion,
//        d.createdAt,
//        g.gptResponse,
//        g.emotion1,
//        g.emotion2,
//        e.score
//    )
//    FROM Diary d
//    LEFT JOIN GptResponse g ON d = g.diary
//    LEFT JOIN EmotionScore e ON d = e.diary
//    WHERE d.user.id = :userId
//      AND d.id = :diaryId
//      AND d.isDeleted = false
//""")
//    Optional<DiaryDetailDTO> findDetailByDiaryId(@Param("userId") Long userId, @Param("diaryId") Long diaryId);



}
