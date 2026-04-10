package com.analiticasoft.ida.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenRefreshRequest {
    private String refreshToken;
}