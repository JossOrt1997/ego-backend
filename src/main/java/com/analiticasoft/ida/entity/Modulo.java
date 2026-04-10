package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "modulo")
@Getter
@Setter
public class Modulo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre; // Ej. "CRM", "ERP", "GPS"

    @Column(name = "descripcion")
    private String descripcion;
}