package com.analiticasoft.ida.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LogDto {
    private Long id;
    private LocalDateTime timestamp;
    private String userEmail;
    private Long userId;
    private Long empresaId;
    private String actionType; // Ej: CREATE_PRODUCTO, UPDATE_EMPRESA
    private JsonNode details; // El payload JSON con los detalles del cambio
    private String ipAddress;
}