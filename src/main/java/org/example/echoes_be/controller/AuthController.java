package org.example.echoes_be.controller;

import lombok.RequiredArgsConstructor;
import org.example.echoes_be.domain.RefreshToken;
import org.example.echoes_be.repository.RefreshTokenRepository;
import org.example.echoes_be.security.JwtUtil;
import org.example.echoes_be.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("RefreshToken") String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(401).body("리프레시 토큰이 유효하지 않습니다.");
        }

        Long userId = Long.parseLong(jwtUtil.extractUserId(refreshToken));
        RefreshToken storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("저장된 리프레시 토큰 없음"));

        if (!storedToken.getToken().equals(refreshToken)) {
            return ResponseEntity.status(403).body("리프레시 토큰 불일치");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        Long userId = Long.parseLong(jwtUtil.extractUserId(accessToken.replace("Bearer ", "")));
        userService.logout(userId);
        return ResponseEntity.ok("로그아웃 완료");
    }
}

