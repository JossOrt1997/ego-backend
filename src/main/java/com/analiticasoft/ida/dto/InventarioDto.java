package com.analiticasoft.ida.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Builder // Usamos @Builder para facilitar la construcción
public class InventarioDto {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String productoSku;
    private Long almacenId;
    private String almacenNombre;
    private BigDecimal cantidad;
    private Instant updatedAt;
}