package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PermisoRequestDto {

    @NotNull
    private Long rolId;

    @NotNull
    private Long moduloId;

    @NotNull
    private Map<String, Boolean> permisos; // Ej: {"crear": true, "leer": true}
}