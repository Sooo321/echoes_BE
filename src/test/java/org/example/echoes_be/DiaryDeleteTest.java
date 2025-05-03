package org.example.echoes_be;

//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.mockito.ArgumentMatchers.any;
//
//
//@ExtendWith(MockitoExtension.class)
//class DiaryDeleteTest {
//
//    @InjectMocks
//    private DiaryService diaryService;
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private DiaryRepository diaryRepository;
//
//    @Test
//    @DisplayName("정상적인 사용자가 자신의 일기를 소프트 삭제한다")
//    void deleteDiary_success() {
//        // given
//        Long userId = 1L;
//        Long diaryId = 10L;
//
//        Users mockUser = Users.builder()
//                .id(userId)
//                .nickname("tester")
//                .email("tester@example.com")
//                .password("encodedPassword")
//                .build();
//
//        Diary mockDiary = Diary.builder()
//                .id(diaryId)
//                .user(mockUser)
//                .title("테스트 일기")
//                .content("내용")
//                .createdAt(LocalDate.now())
//                .isDeleted(false)
//                .build();
//
//        DiaryDeleteRequestDTO request = new DiaryDeleteRequestDTO();
//        request.setDiaryId(diaryId);
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
//        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(mockDiary));
//        when(diaryRepository.save(any(Diary.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // when
//        Diary result = diaryService.deleteDiary(userId, request);
//
//        // then
//        assertThat(result.isDeleted()).isTrue(); // isDeleted가 true로 바뀌었는지 확인
//        verify(diaryRepository).save(mockDiary); // 저장이 한 번은 호출됐는지 확인
//    }
//}
