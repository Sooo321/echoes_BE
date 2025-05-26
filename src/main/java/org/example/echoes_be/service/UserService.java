package org.example.echoes_be.service;
import jakarta.transaction.Transactional;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.domain.RefreshToken;
import org.example.echoes_be.dto.UserLoginRequestDTO;
import org.example.echoes_be.dto.UserSignupRequestDTO;
import org.example.echoes_be.repository.RefreshTokenRepository;
import org.example.echoes_be.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.example.echoes_be.security.JwtUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

//비밀번호 암호화
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
//스프링 빈을 등록하기 위해 컴포넌트 스캔
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

   public Users signup(UserSignupRequestDTO request) {
       String encodedPassword = passwordEncoder.encode(request.getPassword()); //비밀번호 암호화
        Long today = Long.valueOf(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)); //오늘 날짜 받아오기

       Users user = Users.builder()
               .nickname(request.getNickname())
               .email(request.getEmail())
               .password(encodedPassword)
               .accessDate(today)
               .createdDate(today)
               .build();
       return userRepository.save(user);
        //저장
    }


    public Users login(UserLoginRequestDTO request) {
        Optional<Users> User = userRepository.findByEmail(request.getEmail());

        if (User.isPresent()) { //존재하는 이메일이면
            Users user = User.get(); //받아옴
            if (passwordEncoder.matches(request.getPassword(), user.getPassword())) { //페스워드가 같으면
                return user; //성공
            } else {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new IllegalArgumentException("이메일이 존재하지 않습니다.");
        }
    }

    // 토큰 발급 메서드
    public String generateAccessToken(Users user) {
        return jwtUtil.generateAccessToken(user.getId());
    }

    // 이메일 인증 후 -> 사용자 저장
    // 사용자 등록

    @Transactional
    public String generateAndSaveRefreshToken(Users user) {
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());
        refreshTokenRepository.deleteByUserId(user.getId()); // 중복 방지
        refreshTokenRepository.save(RefreshToken.builder()
                .userId(user.getId())
                .token(refreshToken)
                .build());
        return refreshToken;
    }

    public void logout(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }


}
