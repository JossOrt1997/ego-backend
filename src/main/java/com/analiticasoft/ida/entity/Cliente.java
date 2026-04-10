package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "cliente")
@Getter
@Setter
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa; // Vínculo CLAVE multi-tenant

    @OneToOne(fetch = FetchType.LAZY) // Un cliente puede estar o no ligado a una persona
    @JoinColumn(name = "persona_id")
    private Persona persona; // Para clientes B2C

    @Column(name = "nombre_comercial")
    private String nombreComercial; // Para clientes B2B

    @Column(name = "rfc", length = 13)
    private String rfc;

    @Column(name = "email_contacto")
    private String emailContacto;

    @Column(name = "telefono_contacto", length = 50)
    private String telefonoContacto;

    @Column(name = "status", nullable = false)
    private String status; // Ej. "ACTIVO", "INACTIVO", "PROSPECTO"

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}