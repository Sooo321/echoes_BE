package org.example.echoes_be.service;

import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.UserLoginRequestDTO;
import org.example.echoes_be.dto.UserSignupRequestDTO;
import org.example.echoes_be.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
//스프링 빈을 등록하기 위해 컴포넌트 스캔
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

   public Users signup(UserSignupRequestDTO request) {
        Long today = Long.valueOf(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)); //오늘 날짜 받아오기

        Users user = new Users(
                request.getNickname(),
                request.getEmail(),
                request.getPassword(),
                today
        );
        return userRepository.save(user);
        //저장
    }


    public Users login(UserLoginRequestDTO request) {
        Optional<Users> User = userRepository.findByEmail(request.getEmail());

        if (User.isPresent()) { //존재하는 이메일이면
            Users user = User.get(); //받아옴
            if (user.getPassword().equals(request.getPassword())) { //페스워드가 같으면
                return user; //성공
            } else {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new IllegalArgumentException("이메일이 존재하지 않습니다.");
        }
    }

    // 이메일 인증 후 -> 사용자 저장
    // 사용자 등록



}
