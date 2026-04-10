package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaDto {

    private Long id;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nombre;

    private String descripcion;
}