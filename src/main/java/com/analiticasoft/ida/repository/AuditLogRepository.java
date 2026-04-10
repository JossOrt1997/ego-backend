package com.analiticasoft.ida.repository;

import com.analiticasoft.ida.entity.AuditLog;
import org.springframework.data.domain.Page; // Importar
import org.springframework.data.domain.Pageable; // Importar
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // --- Para el Super Admin ---
    /**
     * Obtiene todos los logs, con paginación, ordenados por fecha descendente.
     */
    Page<AuditLog> findAllByOrderByTimestampDesc(Pageable pageable);

    // --- Para el Admin de Cliente ---
    /**
     * Obtiene los logs filtrados por ID de la empresa del usuario, con paginación.
     */
    Page<AuditLog> findByEmpresaIdOrderByTimestampDesc(Long empresaId, Pageable pageable);
}