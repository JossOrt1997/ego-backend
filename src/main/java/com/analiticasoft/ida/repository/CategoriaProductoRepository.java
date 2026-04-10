package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaProductoRepository extends JpaRepository<CategoriaProducto, Long> {

    // Busca todas las categorías de una empresa específica
    List<CategoriaProducto> findAllByEmpresaId(Long empresaId);
}