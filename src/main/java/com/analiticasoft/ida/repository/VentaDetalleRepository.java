package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.VentaDetalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaDetalleRepository extends JpaRepository<VentaDetalle, Long> {
    // JpaRepository es suficiente por ahora
}