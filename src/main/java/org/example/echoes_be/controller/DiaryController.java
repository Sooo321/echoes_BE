package org.example.echoes_be.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.echoes_be.common.ApiResponse;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.GptResponse;
import org.example.echoes_be.dto.*;
import org.example.echoes_be.security.JwtUtil;
import org.example.echoes_be.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService diaryService;
    private final JwtUtil jwtUtil;

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Diary>> saveDiary(
            @RequestBody DiarySaveRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        try {
            String token = resolveToken(httpRequest); // Authorization 헤더에서 토큰 꺼냄
            String userId = jwtUtil.extractUserId(token); // 토큰에서 userId 추출

            Diary diary = diaryService.saveDiary(Long.parseLong(userId), request); // userId와 request를 같이 넘김
            return ResponseEntity.ok(ApiResponse.success(diary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
//    @PostMapping("/delete")
//    public ResponseEntity<ApiResponse<Diary>> deleteDiary(
//            @RequestBody DiaryDeleteRequestDTO request,
//            HttpServletRequest httpRequest
//    ){
//        try {
//            String token = resolveToken(httpRequest); // Authorization 헤더에서 토큰 꺼냄
//            String userId = jwtUtil.extractUserId(token); // 토큰에서 userId 추출
//
//            Diary diary = diaryService.deleteDiary(Long.parseLong(userId), request); // userId와 request를 같이 넘김
//            return ResponseEntity.ok(ApiResponse.success(diary));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
//        }
//    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<ApiResponse<Diary>> deleteDiary(
            @PathVariable Long diaryId,
            HttpServletRequest httpRequest
    ) {
        try {
            String token = resolveToken(httpRequest);
            String userId = jwtUtil.extractUserId(token);

            Diary diary = diaryService.deleteDiary(Long.parseLong(userId), diaryId);

            return ResponseEntity.ok(ApiResponse.success(diary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Diary>> UpdateDiary(
            @RequestBody DiaryUpdateRequestDTO request,
            HttpServletRequest httpRequest
    ){
        try {
            String token = resolveToken(httpRequest); // Authorization 헤더에서 토큰 꺼냄
            String userId = jwtUtil.extractUserId(token); // 토큰에서 userId 추출

            Diary diary = diaryService.updateDiary(Long.parseLong(userId), request); // userId와 request를 같이 넘김
            return ResponseEntity.ok(ApiResponse.success(diary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 날짜별 일기 조회
    @GetMapping("/date")
    public ResponseEntity<ApiResponse<DiaryResponseDTO>> getDiaryByDate(
            @RequestParam("date") String dateStr,
            HttpServletRequest request
    ) {
        String token = resolveToken(request);
        Long userId = Long.parseLong(jwtUtil.extractUserId(token));
        LocalDate date = LocalDate.parse(dateStr);

        DiaryResponseDTO result = diaryService.getDiaryByDate(userId, date);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<DiaryResponseDTO>>> getFavoriteDiaries(@RequestParam Long userId) {
        List<DiaryResponseDTO> result = diaryService.getFavoriteDiaries(userId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }


    @PostMapping("/favorite-toggle")
    public ResponseEntity<ApiResponse<Diary>> toggleFavoriteDiary(
            @RequestBody DiaryFavoriteRequestDTO request,
            HttpServletRequest httpRequest
    ){
        try {
            String token = resolveToken(httpRequest); // Authorization 헤더에서 토큰 꺼냄
            String userId = jwtUtil.extractUserId(token); // 토큰에서 userId 추출

            Diary diary = diaryService.toggleFavoriteDiary(Long.parseLong(userId), request); // userId와 request를 같이 넘김
            return ResponseEntity.ok(ApiResponse.success(diary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // 조언 생성하기
    @PostMapping("/{diaryId}/create")
    public ResponseEntity<GptResponseDTO> createGptResponse(@PathVariable Long diaryId) {
        GptResponseDTO response = diaryService.createGptResponse(diaryId);
        return ResponseEntity.ok(response);
    }

    // 조언 조회하기
    @GetMapping("/{diaryId}/response")
    public ResponseEntity<GptResponseDTO> getGptResponse(@PathVariable Long diaryId) {
        GptResponseDTO response = diaryService.getGptResponse(diaryId);
        return ResponseEntity.ok(response);
    }
}