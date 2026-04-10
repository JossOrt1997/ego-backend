package com.analiticasoft.ida.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
public class VentaResponseDto {

    private Long id;
    private Long clienteId;
    private String clienteNombre; // Para mostrar
    private String usuarioEmail; // Para mostrar
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal total;
    private String status;
    private Instant fechaVenta;
    private List<VentaDetalleResponseDto> detalles;

    // Sub-DTO para los detalles
    @Getter
    @Setter
    @Builder
    public static class VentaDetalleResponseDto {
        private Long productoId;
        private String productoSku;
        private String productoNombre;
        private BigDecimal cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal subtotalLinea;
    }
}