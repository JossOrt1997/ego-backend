package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.dto.AuthTokenResponseDto;
import com.analiticasoft.ida.dto.LoginRequest;
import com.analiticasoft.ida.dto.RegistroRequest;
import com.analiticasoft.ida.entity.Empresa;
import com.analiticasoft.ida.entity.Rol;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.entity.RefreshToken;
import com.analiticasoft.ida.exception.TokenRefreshException;
import com.analiticasoft.ida.repository.EmpresaRepository;
import com.analiticasoft.ida.repository.RolRepository;
import com.analiticasoft.ida.repository.UsuarioRepository;
import com.analiticasoft.ida.repository.RefreshTokenRepository;
import com.analiticasoft.ida.service.JwtService;
import com.analiticasoft.ida.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final RolRepository rolRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthTokenResponseDto login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        Usuario usuario = usuarioRepository.findByEmailWithRol(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String access = jwtService.generateAccessToken(usuario);
        String refresh = jwtService.createRefreshToken(usuario);

        usuario.setLastLogin(Instant.now());
        usuarioRepository.save(usuario);

        return AuthTokenResponseDto.builder()
                .accessToken(access)
                .refreshToken(refresh)
                .build();
    }

    @Override
    public AuthTokenResponseDto refresh(String refreshToken) {
        RefreshToken rt = jwtService.validateRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Token de refresco no válido o expirado"));

        Usuario usuario = rt.getUsuario();
        String newAccess = jwtService.generateAccessToken(usuario);

        return AuthTokenResponseDto.builder()
                .accessToken(newAccess)
                .refreshToken(rt.getToken()) // sin rotación (puedes activarla luego)
                .build();
    }

    @Override
    public void registrarNuevoUsuario(RegistroRequest request) {
        Empresa empresa = new Empresa();
        empresa.setName(request.getNombreEmpresa());
        empresa.setPlanType("FREE"); // o el plan por defecto que manejes
        empresa.setStatus("ACTIVE"); // porque 'status' es String, no boolean
        Empresa empresaGuardada = empresaRepository.save(empresa);

        Rol rolAdmin = null;
        var roles = rolRepository.findByEmpresaIdOrEmpresaIdIsNull(empresaGuardada.getId());
        if (!roles.isEmpty()) {
            rolAdmin = roles.get(0);
        } else {
            rolAdmin = new Rol();
            rolAdmin.setNombre("ADMIN");
            rolAdmin.setDescripcion("Rol por defecto (auto)");
            rolAdmin.setEmpresa(empresaGuardada);
            rolAdmin = rolRepository.save(rolAdmin);
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setEmpresa(empresaGuardada);
        nuevoUsuario.setEmail(request.getEmail());
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        nuevoUsuario.setIsActive(true);
        nuevoUsuario.setRol(rolAdmin);
        usuarioRepository.save(nuevoUsuario);
    }
}
