package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class VentaRequestItemDto {
    @NotNull
    private Long productoId;

    @NotNull
    private Long almacenId; // Almacén del cual se descontará

    @NotNull
    @Positive(message = "La cantidad debe ser mayor a cero")
    private BigDecimal cantidad;
}