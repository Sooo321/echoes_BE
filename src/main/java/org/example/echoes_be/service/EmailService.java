package org.example.echoes_be.service;

import java.util.Map;
import java.util.Random;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;
import org.example.echoes_be.config.RedisConfig;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    // 이메일 인증
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    private final Random random = new Random();

    // 이메일 중복확인

    public boolean emailCheck(String email) {
        Optional<Users> users = userRepository.findByEmail(email);
        return users.isPresent();
    }

    // 인증번호 생성
    public String generateCode() {
        int code = 1000 + random.nextInt(9000);
        return String.valueOf(code);
    }

    // 인증번호 저장
    public void saveCode(String identifier, String code) {
        redisTemplate.opsForValue().set(identifier, code, 5, TimeUnit.MINUTES); // 5분 동안 인증번호 유효
    }

    // 이메일 전송
    private final JavaMailSender mailSender;

    public void sendEmail(String email, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Verification Code");
        message.setText("Your verification code is: " + code);
        mailSender.send(message);
    }

    // 인증번호 확인
    public boolean verifyCode(String identifier, String code) {
        String cachedCode = redisTemplate.opsForValue().get(identifier);
        return code.equals(cachedCode);
    }

    // 임시 데이터 저장
    public void saveTemporaryUser(String identifier, Map<String, String> userData) {
        String key = "tempUser:" + identifier;
        redisTemplate.opsForHash().putAll(key, userData);
        redisTemplate.expire(key, 10, TimeUnit.MINUTES); // 10분 동안 유효
    }

    // 임시 데이터 가져오기
    public Map<Object, Object> getTemporaryUser(String identifier) {
        String key = "tempUser:" + identifier;
        return redisTemplate.opsForHash().entries(key);
    }

    // 임시 데이터 삭제
    public boolean deleteTemporaryUser(String identifier) {
        String key = "tempUser:" + identifier;
        return redisTemplate.delete(key); // 삭제 성공 여부를 반환
    }
}
