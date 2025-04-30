package org.example.echoes_be;

import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.DiaryFavoriteRequestDTO;
import org.example.echoes_be.repository.DiaryRepository;
import org.example.echoes_be.repository.UserRepository;
import org.example.echoes_be.service.DiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;

class DiaryServiceTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DiaryService diaryService;

    private Users testUser;
    private Diary testDiary;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = Users.builder()
                .id(1L)
                .nickname("tester")
                .email("tester@example.com")
                .password("encodedPassword")
                .build();

        testDiary = Diary.builder()
                .id(1L)
                .title("Sample Title")
                .content("Sample Content")
                .createdAt(LocalDate.now())
                .isFavorite(false)
                .isDeleted(false)
                .build();
    }

    @Test
    void testToggleFavorite_Success() {
        // given
        DiaryFavoriteRequestDTO request = new DiaryFavoriteRequestDTO();
        request.setDiaryId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(diaryRepository.findById(1L)).thenReturn(Optional.of(testDiary));
        when(diaryRepository.save(any(Diary.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Diary updatedDiary = diaryService.toggleFavoriteDiary(1L, request);

        // then
        assertTrue(updatedDiary.isFavorite()); // 원래 false였으니 -> true
        verify(diaryRepository, times(1)).save(any(Diary.class));
    }

    @Test
    void testToggleFavorite_UserNotFound() {
        // given
        DiaryFavoriteRequestDTO request = new DiaryFavoriteRequestDTO();
        request.setDiaryId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(IllegalArgumentException.class, () -> {
            diaryService.toggleFavoriteDiary(1L, request);
        });
    }

    @Test
    void testToggleFavorite_DiaryNotFound() {
        // given
        DiaryFavoriteRequestDTO request = new DiaryFavoriteRequestDTO();
        request.setDiaryId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(diaryRepository.findById(1L)).thenReturn(Optional.empty());

        // when + then
        assertThrows(IllegalArgumentException.class, () -> {
            diaryService.toggleFavoriteDiary(1L, request);
        });
    }
}

