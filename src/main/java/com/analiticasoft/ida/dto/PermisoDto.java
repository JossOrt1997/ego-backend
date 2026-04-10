package com.analiticasoft.ida.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@Getter
@Setter
public class PermisoDto {
    private Long rolId;
    private Long moduloId;
    private String moduloNombre;
    private Map<String, Boolean> permisos;
}