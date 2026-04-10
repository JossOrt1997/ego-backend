package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // Busca todos los productos de una empresa
    List<Producto> findAllByEmpresaId(Long empresaId);

    // Busca un producto por su ID y el ID de la empresa (para seguridad)
    Optional<Producto> findByIdAndEmpresaId(Long id, Long empresaId);

    // Busca si ya existe un SKU en esa empresa
    boolean existsBySkuAndEmpresaId(String sku, Long empresaId);
}