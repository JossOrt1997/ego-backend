package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

// =================================================================
// 2. DEFINICIÓN DE LA ENTIDAD PRINCIPAL
// Esta es la entidad que usa la clave compuesta.
// =================================================================
@Entity
@Table(name = "rol_acceso_modulo")
@Getter
@Setter
public class RolAccesoModulo {

    @EmbeddedId // <-- Le decimos a JPA que use la clase de arriba como ID
    private RolAccesoModuloId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("rolId") // <-- Mapea el campo 'rolId' de la clave a la relación 'rol'
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("moduloId") // <-- Mapea el campo 'moduloId' de la clave a la relación 'modulo'
    @JoinColumn(name = "modulo_id")
    private Modulo modulo;

    // Mapeo para el campo JSONB de PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "permisos", columnDefinition = "jsonb")
    private Map<String, Boolean> permisos; // Ej: {"crear": true, "leer": true}
}
