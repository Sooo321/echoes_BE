package org.example.echoes_be.controller;

import org.apache.coyote.Response;
import org.example.echoes_be.common.ApiResponse;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.dto.DiarySaveRequestDto;
import org.example.echoes_be.service.DiaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/diary")
public class DiaryController {

    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService){
        this.diaryService = diaryService;
    }
    @PostMapping("/save/{user_id}")
    public ResponseEntity<ApiResponse<Diary>> saveDiary(
            @PathVariable Long user_id,
            @RequestBody DiarySaveRequestDto requestDto
    ){
        try {
            Diary diary = diaryService.saveDiary(requestDto, user_id);
            return ResponseEntity.ok(ApiResponse.success(diary));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}