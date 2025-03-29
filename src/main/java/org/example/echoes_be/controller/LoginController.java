package org.example.echoes_be.controller;


import org.example.echoes_be.common.ApiResponse;
import org.example.echoes_be.domain.Users;
import org.example.echoes_be.dto.UserLoginRequest;
import org.example.echoes_be.dto.UserSignupRequest;
import org.example.echoes_be.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/login")

public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService){
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Users>> login(@RequestBody UserLoginRequest request) {
        Users user = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

}
