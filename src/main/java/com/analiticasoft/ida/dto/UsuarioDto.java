package com.analiticasoft.ida.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.Instant;

@Getter
@Setter
public class UsuarioDto {
    private Long id;
    private String email;
    private Long empresaId;
    private String rolNombre; // Nombre del rol
    private Boolean isActive;
    private Instant lastLogin;
}