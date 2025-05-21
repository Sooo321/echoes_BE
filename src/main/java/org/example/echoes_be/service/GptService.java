package org.example.echoes_be.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.echoes_be.client.GPTClient;
import org.example.echoes_be.domain.Diary;
import org.example.echoes_be.domain.GptResponse;
import org.example.echoes_be.dto.GptAdviceDTO;
import org.example.echoes_be.dto.GptEmotionDTO;
import org.example.echoes_be.dto.GptResponseDTO;
import org.example.echoes_be.repository.DiaryRepository;
import org.example.echoes_be.repository.GptResponseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GptService {
    private final GptResponseRepository gptResponseRepository;
    private final DiaryRepository diaryRepository;

    private final GPTClient gptClient;

    // 조언 생성하기
    @Transactional
    public GptResponseDTO createGptResponse(Long diaryId) {
        // 1. 저장된 일기 가져오기
        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("일기를 찾을 수 없습니다."));

        if (gptResponseRepository.findById(diaryId).isPresent()) {
            throw new IllegalStateException("이미 해당 일기에 대한 조언이 존재합니다.");
        }

        // 2. GPT 에게 조언 및 감정 태그 요청
        GptResponseDTO gptResponseDTO = gptClient.generateAdvice(diary.getContent());

        // 3. 응답을 엔터티로 저장
        GptResponse response = new GptResponse();
        response.setDiary(diary);
        response.setResponse(gptResponseDTO.getResponse());
        response.setEmotion1(gptResponseDTO.getEmotion1());
        response.setEmotion2(gptResponseDTO.getEmotion2());

        //인지 왜곡 분류
        //response.setDistortionType(gptResponseDTO.getDistortionType());

        // 응답이 존재하면 true
        boolean isValidResponse = gptResponseDTO.getResponse() != null && !gptResponseDTO.getResponse().isBlank();
        response.setGptResponse(isValidResponse);

        // 4. 응답 저장 후, DTO 로 변환해서 반환
        GptResponse saved = gptResponseRepository.save(response);
        return new GptResponseDTO(saved);
    }


    // 조언 조회하기
    public GptAdviceDTO getGptResponse(Long diaryId) {
        GptResponse response = gptResponseRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("GPT 응답이 존재하지 않습니다."));
        return new GptAdviceDTO(response);
    }

    // 감정 태그 조회하기
    public GptEmotionDTO getGptEmotion(Long diaryId) {
        GptResponse response = gptResponseRepository.findByDiaryId(diaryId)
                .orElseThrow(() -> new IllegalArgumentException("GPT 응답이 존재하지 않습니다."));
        return new GptEmotionDTO(response);
    }
}
