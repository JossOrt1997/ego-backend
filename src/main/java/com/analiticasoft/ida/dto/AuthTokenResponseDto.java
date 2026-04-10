package com.analiticasoft.ida.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder // Usamos Builder para facilitar la construcción en el servicio
public class AuthTokenResponseDto {
    private String accessToken;  // Token de corta vida (Access Token)
    private String refreshToken; // Token de larga vida (Refresh Token)
    private String tokenType = "Bearer";
}