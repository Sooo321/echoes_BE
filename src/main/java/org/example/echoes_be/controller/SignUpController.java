package org.example.echoes_be.controller;

import org.example.echoes_be.common.ApiResponse;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.UserSignupRequestDTO;
import org.example.echoes_be.service.EmailService;
import org.example.echoes_be.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//DTO : Data Transfer Object
//클라이언트로부터 전달받은 데이터를 캡슐화하여 서비스 계층으로 전달하기 위해 사용. 이를 통해 컨트롤러와 서비스 간의 데이터 전달을 명확하고 간결하게 할 수 있음.
@RestController
@RequestMapping("/api/auth/")
public class SignUpController {

    private final UserService userService;
    private final EmailService emailService;

    public SignUpController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }
    // 생성자 주입 방식으로 userService 객체를 주입 받음.
    // 생성자 주입(DI)
    // Spring boot 2.6+ 버전 부터는 @Autowired를 생략해도 자동 주입 가능.

    @PostMapping("/signUp")
    public ResponseEntity<ApiResponse<Users>> signup(@RequestBody UserSignupRequestDTO request) {
        Users user = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }


    // 이메일 중복 확인
    @PostMapping("/checkEmail")
    public ResponseEntity<ApiResponse<String>> checkEmail(@RequestBody UserSignupRequestDTO userSignupRequestDTO) {
        String email = userSignupRequestDTO.getEmail();
        boolean isExisted = emailService.emailCheck(email);

        if (isExisted) {
            return ResponseEntity.badRequest().body(ApiResponse.error("duplicate id"));
        }
        else {
            return ResponseEntity.ok(ApiResponse.success("email available"));
        }
    }


    // 이메일 인증 코드 전송
    @PostMapping("/sendEmail")
    public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody UserSignupRequestDTO userSignupRequestDTO){

        String email = userSignupRequestDTO.getEmail();
        String code = emailService.generateCode();

        emailService.sendEmail(email, code);
        emailService.saveCode(email, code);

        return ResponseEntity.ok(ApiResponse.success("Verification code sent via email."));
    }


    // 이메일 인증 코드 확인
    @PostMapping("/verifyEmail")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody UserSignupRequestDTO userSignupRequestDTO){
        String email = userSignupRequestDTO.getEmail();
        String code = userSignupRequestDTO.getCode();

        if (code == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Code cannot be null"));
        }

        boolean verified = emailService.verifyCode(email, code);
        if (verified) {
            return ResponseEntity.ok(ApiResponse.success("Verification completed!"));
        }

        else {
            emailService.deleteTemporaryUser(email);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error("Invalid verification code"));
        }
    }

}
