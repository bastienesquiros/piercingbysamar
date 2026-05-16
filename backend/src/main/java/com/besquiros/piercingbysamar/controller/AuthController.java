package com.besquiros.piercingbysamar.controller;

import com.besquiros.piercingbysamar.dto.request.LoginRequest;
import com.besquiros.piercingbysamar.dto.response.AuthResponse;
import com.besquiros.piercingbysamar.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
