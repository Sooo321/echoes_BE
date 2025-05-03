package org.example.echoes_be.controller;

import org.example.echoes_be.dto.LoginResponseDTO;
import org.example.echoes_be.security.JwtUtil;
import org.example.echoes_be.common.ApiResponse;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.UserLoginRequestDTO;
import org.example.echoes_be.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/login")

public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(@RequestBody UserLoginRequestDTO request) {
        try {
            Users user = userService.login(request);
            String token = userService.generateToken(user);

            LoginResponseDTO responseDTO = new LoginResponseDTO(
                    token,
                    user.getId(),
                    user.getNickname(),
                    user.getEmail()
            );

            return ResponseEntity.ok(ApiResponse.success(responseDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

}
