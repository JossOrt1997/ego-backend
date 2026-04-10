package com.analiticasoft.ida.dto;

import com.fasterxml.jackson.databind.JsonNode; // Importante para mapear JSONB
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant; // Usamos Instant para consistencia con la DB

@Getter
@Setter
@Builder
public class AuditLogResponseDto {
    private Long id;
    private Instant timestamp;
    private String userEmail;
    private Long empresaId;
    private String accion; // Ej: CREATE_PRODUCTO, UPDATE_EMPRESA
    private JsonNode detalles; // El payload JSON con los detalles del cambio
}