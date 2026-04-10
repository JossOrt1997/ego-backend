package com.analiticasoft.ida.service;

import com.analiticasoft.ida.dto.AuditLogResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

public interface LogService {

    /**
     * Obtiene todos los logs del sistema, paginados (Solo para SUPER_ADMIN).
     */
    Page<AuditLogResponseDto> getAllLogs(Pageable pageable);

    /**
     * Obtiene los logs filtrados por la empresa del usuario autenticado, paginados.
     */
    Page<AuditLogResponseDto> getLogsByMiEmpresa(Pageable pageable);

    /**
     * Inserta un log en la base de datos.
     */
    void ingestLog(String userEmail, Long empresaId, String accion, Map<String, Object> detalles);
}
