package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolDto {
    private Long id;

    @NotBlank(message = "El nombre del rol no puede estar vacío")
    private String nombre; // Ej. "VENDEDOR", "GERENTE"

    private String descripcion;
}