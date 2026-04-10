package com.analiticasoft.ida.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // Mapeo a HTTP 403 Forbidden
public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String token, String message) {
        super(String.format("Fallo para el token de refresco [%s]: %s", token, message));
    }
}