package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal; // Importar
import java.util.List;
import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {

    // Busca un registro de inventario por producto y almacén
    Optional<Inventario> findByProductoIdAndAlmacenId(Long productoId, Long almacenId);

    // Busca todo el inventario de un producto (en todos los almacenes)
    List<Inventario> findByProductoId(Long productoId);

    // (Optimización) Obtiene el stock total de un producto en todos los almacenes de una empresa
    @Query("SELECT SUM(i.cantidad) FROM Inventario i " +
            "WHERE i.producto.id = :productoId AND i.producto.empresa.id = :empresaId")
    Optional<BigDecimal> getStockTotalByProductoId(Long productoId, Long empresaId);

    // (Optimización) Obtiene el inventario de una empresa con sus entidades cargadas
    @Query("SELECT i FROM Inventario i " +
            "JOIN FETCH i.producto p " +
            "JOIN FETCH i.almacen a " +
            "WHERE a.empresa.id = :empresaId")
    List<Inventario> findAllByEmpresaIdWithDetails(Long empresaId);
}