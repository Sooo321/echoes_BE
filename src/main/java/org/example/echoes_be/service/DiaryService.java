package org.example.echoes_be.service;


import jakarta.transaction.Transactional;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.DiarySaveRequestDTO;
import org.example.echoes_be.repository.DiaryRepository;
import org.example.echoes_be.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@Transactional
//DB 작업(저장, 수정, 삭제 등)이 트랜잭션으로 처리됨. 실패하면 자동으로 롤백함.
public class DiaryService {

    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    public DiaryService(UserRepository userRepository, DiaryRepository diaryRepository){
        this.userRepository = userRepository;
        this.diaryRepository = diaryRepository;
    }

    public Diary saveDiary(Long user_id, DiarySaveRequestDTO request ){
        //request는 클라이언트에서 보내는 요청 데이터를 담는 변수라는 의미로 많이 사용됨.
        Users user = userRepository.findById(user_id)
                .orElseThrow(()-> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        // 2. Diary 객체를 생성 (DTO에서 데이터 추출)
        Diary diary = Diary.builder()
                .user(user) // 작성자 정보 연결
                .title(request.getTitle())
                .content(request.getContent())
                .created_at(request.getCregated_at())
                .build(); //객체 생성을 완료하는 메서드

        //Diary.builder()
        //Lombok의 @Builder 어노테이션을 사용해서 Diary 객체를 만드는 방식.
        //Diary 엔티티 클래스에 @Builder가 선언되어 있어야 사용 가능함.
        //빌더 패턴을 사용하면 객체를 필드별로 설정하고 최종적으로 .build()를 호출해서 객체를 생성함.
        // 3. Diary 객체를 DB에 저장
        return diaryRepository.save(diary);

    }
}
