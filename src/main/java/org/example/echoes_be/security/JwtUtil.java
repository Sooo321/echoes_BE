package org.example.echoes_be.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

//토큰 생성 & 검증 유틸
@Component
public class JwtUtil {

    private final String secretKey = "superSecretKeyThatIsLongEnough1234567890"; //JWT Signature 만들때 사용하는 비밀 키. 토큰이 위조 됐는지 검증할 때 사용.
    private final long expirationMs = 60 * 60 * 1000; // 토큰 유효 시간. 1시간 동안 유효한 토큰. 로그인한지 1시간이 지나면 토큰이 만료돼서 다시 로그인 해야 됨.

    //토큰 생성
    public String generateToken(Long userId) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date()) //지금 시간 기준으로 발급시간(issueAt) 설정
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    //검증하고 값 전달
    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    //토큰이 유효한지 체크하는 함수
    public boolean validateToken(String token) {
        try {
            extractUserId(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}

