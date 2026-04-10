package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioUpdateDto {

    @NotNull(message = "El ID del rol es obligatorio")
    private Long rolId;

    @NotNull(message = "El ID de la empresa es obligatorio")
    private Long empresaId;

    @NotNull(message = "El estado (activo/inactivo) es obligatorio")
    private Boolean isActive;
}