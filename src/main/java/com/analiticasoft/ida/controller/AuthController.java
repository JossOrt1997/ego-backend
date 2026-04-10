package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.LoginRequest;
import com.analiticasoft.ida.dto.RegistroRequest;
import com.analiticasoft.ida.dto.AuthTokenResponseDto;
import com.analiticasoft.ida.dto.TokenRefreshRequest;
import com.analiticasoft.ida.dto.UsuarioDto;
import com.analiticasoft.ida.dto.CambioPasswordRequest;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthTokenResponseDto> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponseDto> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthTokenResponseDto> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistroRequest request) {
        authService.registrarNuevoUsuario(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioDto> me(Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmailWithRol(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Usuario autenticado no encontrado"));

        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setEmail(usuario.getEmail());
        dto.setEmpresaId(usuario.getEmpresa().getId());
        dto.setRolNombre(usuario.getRol().getNombre());
        dto.setIsActive(usuario.getIsActive());
        dto.setLastLogin(usuario.getLastLogin());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody CambioPasswordRequest request, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Usuario autenticado no encontrado"));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new BadCredentialsException("La contraseña actual no coincide");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getNuevaPassword()));
        usuarioRepository.save(usuario);
        return ResponseEntity.noContent().build();
    }
}
