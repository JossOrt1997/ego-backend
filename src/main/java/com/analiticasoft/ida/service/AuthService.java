package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.LoginRequest;
import com.analiticasoft.ida.dto.RegistroRequest;
import com.analiticasoft.ida.dto.AuthTokenResponseDto;

public interface AuthService {

    AuthTokenResponseDto login(LoginRequest request);

    AuthTokenResponseDto refresh(String refreshToken);

    void registrarNuevoUsuario(RegistroRequest request);
}
