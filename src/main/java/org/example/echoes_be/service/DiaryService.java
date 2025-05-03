package org.example.echoes_be.service;


import jakarta.transaction.Transactional;
import org.example.echoes_be.client.GPTClient;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.*;
import org.example.echoes_be.repository.DiaryRepository;
import org.example.echoes_be.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
//DB 작업(저장, 수정, 삭제 등)이 트랜잭션으로 처리됨. 실패하면 자동으로 롤백함.
public class DiaryService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    public DiaryService(UserRepository userRepository, DiaryRepository diaryRepository, GPTClient gptClient){
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
        this.gptClient = gptClient;
    }

    //일기 저장
    public Diary saveDiary(Long user_id, DiarySaveRequestDTO request ){
        //request는 클라이언트에서 보내는 요청 데이터를 담는 변수라는 의미로 많이 사용됨.
        //사용자 확인
        Users user = userRepository.findById(user_id)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        //데이터 형식 수동으로 파싱
        LocalDate createdDate = LocalDate.parse(request.getCreated_at());

        // 2. Diary 객체를 생성 (DTO에서 데이터 추출)
        Diary diary = Diary.builder()
                .user(user) // 작성자 정보 연결
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(createdDate)
                .build(); //객체 생성을 완료하는 메서드

        //Diary.builder()
        //Lombok의 @Builder 어노테이션을 사용해서 Diary 객체를 만드는 방식.
        //Diary 엔티티 클래스에 @Builder가 선언되어 있어야 사용 가능함.
        //빌더 패턴을 사용하면 객체를 필드별로 설정하고 최종적으로 .build()를 호출해서 객체를 생성함.
        // 3. Diary 객체를 DB에 저장
        return diaryRepository.save(diary);

    }

    //일기 삭제
//    public Diary deleteDiary(Long user_id, DiaryDeleteRequestDTO request) {
//        //사용자 확인
//        Users user = userRepository.findById(user_id)
//                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//        // 다이어리 조회
//        Diary diary = diaryRepository.findById(request.getDiaryId())
//                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));
//
//        diary.setDeleted(true);
//
//        return diaryRepository.save(diary);
//    }
    public Diary deleteDiary(Long userId, Long diaryId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        if (!diary.getUser().getId().equals(user.getId())) {
            throw new SecurityException("해당 일기를 삭제할 권한이 없습니다.");
        }

        diary.setDeleted(true); // 소프트 삭제
        return diaryRepository.save(diary);
    }

    //일기 수정
    public Diary updateDiary(Long user_id, DiaryUpdateRequestDTO request){
        //사용자 확인
        Users user = userRepository.findById(user_id)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 다이어리 조회
        Diary diary = diaryRepository.findById(request.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));
        // 소유자 및 삭제 여부 확인
        if (!diary.getUser().getId().equals(user_id)) {
            throw new SecurityException("본인의 일기만 수정할 수 있습니다.");
        }
        if (diary.isDeleted()) {
            throw new IllegalStateException("삭제된 일기는 수정할 수 없습니다.");
        }
        // 수정 필드 적용
        diary.setTitle(request.getTitle());
        diary.setContent(request.getContent());

        return diaryRepository.save(diary);
    }
    //일기 조회
    public DiaryResponseDTO getDiaryByDate(Long userId,LocalDate date) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        Diary diary = diaryRepository.findByUserIdAndCreatedAtAndIsDeletedFalse(userId, date)
                .orElseThrow(() -> new IllegalArgumentException("해당 날짜에 작성된 일기가 없습니다."));

        return DiaryResponseDTO.fromEntity(diary);
    }

    //즐겨찾기 된 일기 조회
    public List<DiaryResponseDTO> getFavoriteDiaries(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<Diary> diaries = diaryRepository.findByUserIdAndIsFavoriteTrueAndIsDeletedFalse(userId);

        return diaries.stream()
                .map(DiaryResponseDTO::fromEntity)
                .toList();
    }

    //즐겨찾기
    public Diary toggleFavoriteDiary(Long user_id, DiaryFavoriteRequestDTO request) {
        //사용자 확인
        Users user = userRepository.findById(user_id)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 다이어리 조회
        Diary diary = diaryRepository.findById(request.getDiaryId())
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        // 즐겨찾기 상태 토글
        diary.setFavorite(!diary.isFavorite());

        return diaryRepository.save(diary);
    }


    // 조언 생성하기
    private final GPTClient gptClient;

    public ReplyResponseDTO generateReply(Long diaryId) {

        // DB에서 일기 가져오기
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new RuntimeException("Diary not found"));
        String content = diary.getContent();

        // 감정 분석 (임시)
        List<String> emotionTags = List.of("#행복한", "#기쁜");

        // GPT 호출
        String reply = gptClient.generateAdvice(content, emotionTags);

        return ReplyResponseDTO.builder()
                .diaryId(diary.getId())
                .emotionTags(emotionTags)
                .reply(reply)
                .build();
    }
}
