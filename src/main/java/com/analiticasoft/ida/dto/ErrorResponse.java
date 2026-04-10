package com.analiticasoft.ida.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor // Constructor con todos los argumentos
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error; // Ej: "Not Found", "Bad Request"
    private String message;
    private String path;
}