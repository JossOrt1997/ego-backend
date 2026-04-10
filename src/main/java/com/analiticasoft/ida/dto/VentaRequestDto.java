package com.analiticasoft.ida.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class VentaRequestDto {

    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;

    @NotEmpty(message = "La venta debe tener al menos un producto")
    @Valid // Valida cada item de la lista
    private List<VentaRequestItemDto> items;
}