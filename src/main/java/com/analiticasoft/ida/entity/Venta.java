package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Getter
@Setter
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa; // Vínculo Multi-Tenant

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente; // Cliente (del CRM)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario; // Vendedor/Usuario que registró la venta

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "impuestos", nullable = false, precision = 12, scale = 2)
    private BigDecimal impuestos;

    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // Ej. "PENDIENTE", "COMPLETADA", "CANCELADA"

    @CreationTimestamp
    @Column(name = "fecha_venta", nullable = false, updatable = false)
    private Instant fechaVenta;

    // Relación uno-a-muchos: Una Venta tiene muchos VentaDetalle
    // CascadeType.ALL: Si borro la venta, se borran sus detalles.
    // orphanRemoval = true: Si quito un detalle de la lista, se borra de la BD.
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<VentaDetalle> detalles = new ArrayList<>();
}