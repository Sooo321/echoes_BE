package org.example.echoes_be.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.echoes_be.common.ApiResponse;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.dto.DiarySaveRequestDTO;
import org.example.echoes_be.security.JwtUtil;
import org.example.echoes_be.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

}