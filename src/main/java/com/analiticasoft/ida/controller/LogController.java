package com.analiticasoft.ida.controller;

import com.analiticasoft.ida.dto.AuditLogResponseDto;
import com.analiticasoft.ida.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // Importar
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1") // Prefijo principal
public class LogController {

    private final LogService logService;

    // --- ENDPOINTS PARA EL PANEL DE SUPER ADMINISTRADOR ---

    /**
     * Obtiene el historial completo de auditoría de TODAS las empresas, con paginación.
     * Endpoint: GET /api/v1/superadmin/auditoria
     * @param pageable - Parámetros de paginación (page, size, sort)
     */
    @GetMapping("/superadmin/auditoria")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDto>> getGlobalAuditLog(Pageable pageable) {
        return ResponseEntity.ok(logService.getAllLogs(pageable));
    }

    /**
     * Endpoint solicitado: GET /api/v1/superadmin/logs (Este es un alias del anterior)
     */
    @GetMapping("/superadmin/logs")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<AuditLogResponseDto>> getAllLogs(Pageable pageable) {
        return ResponseEntity.ok(logService.getAllLogs(pageable));
    }

    // --- ENDPOINTS PARA EL PANEL DE CLIENTE (ADMIN) ---

    /**
     * Obtiene el historial de auditoría SÓLO para la empresa del usuario, con paginación.
     * Endpoint: GET /api/v1/logs
     */
    @GetMapping("/logs")
    // Se asume la creación de un módulo 'SEGURIDAD' o 'AUDITORIA' para este permiso
    @PreAuthorize("hasPermission('SEGURIDAD', 'leer')")
    public ResponseEntity<Page<AuditLogResponseDto>> getMyCompanyLogs(Pageable pageable) {
        return ResponseEntity.ok(logService.getLogsByMiEmpresa(pageable));
    }

    // --- ENDPOINT PARA INGESTA EXTERNA (Microsservicios/Frontend) ---

    /**
     * Recibe logs estructurados del frontend o microservicios Python.
     * Endpoint: POST /api/v1/logs/ingest
     */
    @PostMapping("/logs/ingest")
    // Requiere autenticación, pero podríamos crear un rol 'SERVICE_ACCOUNT' o usar el SUPER_ADMIN
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> ingestLog(@RequestBody Map<String, Object> logData) {

        // Ejemplo de ingesta simple:
        String userEmail = (String) logData.getOrDefault("email", "PROCESO_EXTERNO");
        Long empresaId = Optional.ofNullable(logData.get("empresaId")).map(o -> Long.valueOf(o.toString())).orElse(null);
        String accion = (String) logData.getOrDefault("accion", "CUSTOM_INGEST");

        // Removemos los datos clave antes de pasarlos a detalles
        logData.remove("email");
        logData.remove("empresaId");
        logData.remove("accion");

        logService.ingestLog(userEmail, empresaId, accion, logData);

        return ResponseEntity.ok().build();
    }
}