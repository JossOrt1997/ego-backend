package com.analiticasoft.ida.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal; // Importar para manejar dinero

@Getter
@Setter
public class ProductoDto {

    private Long id; // Para respuestas (GET)

    @NotBlank(message = "El SKU es obligatorio")
    @Size(max = 100, message = "El SKU no puede tener más de 100 caracteres")
    private String sku;

    @NotBlank(message = "El nombre del producto es obligatorio")
    private String nombre;

    private String descripcion;

    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoriaId; // El ID de la categoría a la que pertenece

    @NotNull(message = "El precio de venta es obligatorio")
    @PositiveOrZero(message = "El precio de venta no puede ser negativo")
    private BigDecimal precioVenta;

    @PositiveOrZero(message = "El precio de compra no puede ser negativo")
    private BigDecimal precioCompra;
}