package com.analiticasoft.ida.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuperAdminRolDto {
    private Long id;
    private String nombre;
    private String descripcion;
    private Long empresaId; // ID de la empresa a la que pertenece
}