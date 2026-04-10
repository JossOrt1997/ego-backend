package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal; // Importante para cantidades
import java.time.Instant;

@Entity
@Table(name = "inventario",
        // Un producto solo puede tener una entrada por almacén
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"producto_id", "almacen_id"})
        }
)
@Getter
@Setter
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "almacen_id", nullable = false)
    private Almacen almacen;

    // Usamos BigDecimal para cantidades por precisión (ej. 1.5 kg)
    @Column(name = "cantidad", nullable = false, precision = 12, scale = 4)
    private BigDecimal cantidad;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}