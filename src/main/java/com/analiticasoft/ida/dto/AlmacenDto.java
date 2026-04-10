package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AlmacenDto {

    private Long id; // Se usa para mostrar, no para crear

    @NotBlank(message = "El nombre del almacén es obligatorio")
    private String nombre;

    private String direccion;
}