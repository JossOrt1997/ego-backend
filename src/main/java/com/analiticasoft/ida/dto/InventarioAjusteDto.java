package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class InventarioAjusteDto {

    @NotNull
    private Long productoId;

    @NotNull
    private Long almacenId;

    @NotNull
    private BigDecimal cantidad; // La cantidad a sumar (positiva) o restar (negativa)

    @NotNull
    private String tipoMovimiento; // "AJUSTE_MANUAL", "VENTA", "COMPRA", etc.
}