package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RolRepository extends JpaRepository<Rol, Long> {
    // Busca roles globales (empresa_id es NULL) o los de una empresa específica
    List<Rol> findByEmpresaIdOrEmpresaIdIsNull(Long empresaId);
}