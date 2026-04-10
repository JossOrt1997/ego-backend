package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Busca todas las ventas de una empresa
    List<Venta> findAllByEmpresaId(Long empresaId);

    // Busca una venta específica, validando la empresa (seguridad)
    Optional<Venta> findByIdAndEmpresaId(Long id, Long empresaId);

    // (Optimización) Carga una venta y todos sus detalles y productos en una sola consulta
    @Query("SELECT v FROM Venta v LEFT JOIN FETCH v.detalles d LEFT JOIN FETCH d.producto WHERE v.id = :id AND v.empresa.id = :empresaId")
    Optional<Venta> findByIdWithDetails(Long id, Long empresaId);
}