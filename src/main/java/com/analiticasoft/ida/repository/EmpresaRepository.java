package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    // Cuenta todas las empresas
    long count();

    // (Consulta más avanzada) Encuentra el nombre del planType más común
    @Query("SELECT e.planType FROM Empresa e GROUP BY e.planType ORDER BY COUNT(e) DESC LIMIT 1")
    String findMostPopularPlanType();
}