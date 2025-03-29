package org.example.echoes_be.controller;

import org.example.echoes_be.common.ApiResponse;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.UserSignupRequest;
import org.example.echoes_be.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//DTO : Data Transfer Object
//클라이언트로부터 전달받은 데이터를 캡슐화하여 서비스 계층으로 전달하기 위해 사용. 이를 통해 컨트롤러와 서비스 간의 데이터 전달을 명확하고 간결하게 할 수 있음.
@RestController
@RequestMapping("/api/auth/user")
public class SignUpController {

    private final UserService userService;

    public SignUpController(UserService userService) {
        this.userService = userService;
    }
    // 생성자 주입 방식으로 userService 객체를 주입 받음.
    // 생성자 주입(DI)
    // Spring boot 2.6+ 버전 부터는 @Autowired를 생략해도 자동 주입 가능.

    @PostMapping
    public ResponseEntity<ApiResponse<Users>> signup(@RequestBody UserSignupRequest request) {
        Users user = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
