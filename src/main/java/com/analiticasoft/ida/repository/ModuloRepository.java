package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Modulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuloRepository extends JpaRepository<Modulo, Long> {

    // Método para buscar un módulo por su nombre clave (ej. "CRM")
    Optional<Modulo> findByNombre(String nombre);
}