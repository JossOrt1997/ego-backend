package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.LoginRequest;
import com.analiticasoft.ida.dto.RegistroRequest;
import com.analiticasoft.ida.dto.AuthTokenResponseDto;
import com.analiticasoft.ida.dto.TokenRefreshRequest;
import com.analiticasoft.ida.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponseDto> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistroRequest request) {
        authService.registrarNuevoUsuario(request);
        return ResponseEntity.ok().build();
    }
}
