package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Almacen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlmacenRepository extends JpaRepository<Almacen, Long> {

    /**
     * Busca todos los almacenes de una empresa específica.
     */
    List<Almacen> findAllByEmpresaId(Long empresaId);

    /**
     * Busca un almacén por su ID Y el ID de la empresa (para seguridad).
     */
    Optional<Almacen> findByIdAndEmpresaId(Long id, Long empresaId);
}