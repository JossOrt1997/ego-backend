package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.RolAccesoModulo;
import com.analiticasoft.ida.entity.RolAccesoModuloId; // Importa la clase de la clave
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional; // Asegúrate de tener este import

@Repository
public interface RolAccesoModuloRepository extends JpaRepository<RolAccesoModulo, RolAccesoModuloId> {

    List<RolAccesoModulo> findByRolId(Long rolId);

    Optional<RolAccesoModulo> findByRolIdAndModuloId(Long rolId, Long moduloId);

    //Nuevo método para buscar por rolId y nombre del módulo
    // Busca en la tabla RolAccesoModulo (ram) uniéndola (join) con la tabla Modulo (m)
    // y filtrando por el rolId y el nombre del módulo.
    @Query("SELECT ram FROM RolAccesoModulo ram JOIN ram.modulo m WHERE ram.rol.id = :rolId AND m.nombre = :moduloNombre")
    Optional<RolAccesoModulo> findByRolIdAndModuloNombre(Long rolId, String moduloNombre);

    // --- MÉTODO NUEVO ---
    boolean existsByModuloId(Long moduloId);
}

