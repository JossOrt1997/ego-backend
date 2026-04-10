package com.analiticasoft.ida.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects; // Importar para Objects.hash

// =================================================================
// 1. DEFINICIÓN DE LA CLAVE PRIMARIA COMPUESTA
// Clase PÚBLICA para que sea visible desde el paquete 'repository'
// =================================================================
@Embeddable
@Getter
@Setter
public class RolAccesoModuloId implements Serializable {

    @Column(name = "rol_id")
    private Long rolId;

    @Column(name = "modulo_id")
    private Long moduloId;

    // --- Métodos equals() y hashCode() OBLIGATORIOS ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RolAccesoModuloId that = (RolAccesoModuloId) o;
        return Objects.equals(rolId, that.rolId) &&
                Objects.equals(moduloId, that.moduloId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rolId, moduloId); // Forma más moderna de generar el hash
    }
}


