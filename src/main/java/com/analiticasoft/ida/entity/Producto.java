package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "producto",
        // Creamos un índice único para que no se repita el SKU *dentro de la misma empresa*
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"empresa_id", "sku"})
        }
)
@Getter
@Setter
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa; // Vínculo Multi-Tenant

    @ManyToOne(fetch = FetchType.LAZY) // Puede ser nulo si no tiene categoría
    @JoinColumn(name = "categoria_id")
    private CategoriaProducto categoria;

    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "precio_venta", nullable = false, precision = 12, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "precio_compra", precision = 12, scale = 2)
    private BigDecimal precioCompra;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}