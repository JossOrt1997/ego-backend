package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "almacen")
@Getter
@Setter
public class Almacen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa; // Vínculo Multi-Tenant

    @Column(name = "nombre", nullable = false)
    private String nombre; // Ej. "Bodega Central", "Sucursal Norte"

    @Column(name = "direccion", columnDefinition = "TEXT")
    private String direccion;
}