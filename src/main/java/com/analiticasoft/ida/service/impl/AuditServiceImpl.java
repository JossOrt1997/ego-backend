package com.analiticasoft.ida.service.impl;

import com.analiticasoft.ida.entity.AuditLog;
import com.analiticasoft.ida.entity.Empresa;
import com.analiticasoft.ida.entity.Usuario;
import com.analiticasoft.ida.repository.AuditLogRepository;
import com.analiticasoft.ida.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async; // Importar
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Implementación de logAction.
     * @Async asegura que se ejecute en un hilo separado.
     * Propagation.REQUIRES_NEW asegura que esta escritura en la BD
     * ocurra en su propia transacción, independiente de la acción principal.
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void logAction(Usuario usuario, String accion, Map<String, Object> detalles) {
        try {
            AuditLog log = new AuditLog();
            log.setUsuarioEmail(usuario.getEmail());
            log.setEmpresa(usuario.getEmpresa()); // Asignamos la empresa del usuario
            log.setAccion(accion.toUpperCase());
            log.setDetalles(detalles);

            auditLogRepository.save(log);
        } catch (Exception e) {
            // Loggear el error si la auditoría falla, pero NO detener la app
            System.err.println("FALLO AL GUARDAR EL LOG DE AUDITORÍA: " + e.getMessage());
        }
    }

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void logAction(String usuarioEmail, Long empresaId, String accion, Map<String, Object> detalles) {
        try {
            AuditLog log = new AuditLog();
            log.setUsuarioEmail(usuarioEmail);
            if (empresaId != null) {
                Empresa e = new Empresa();
                e.setId(empresaId); // Creamos una referencia "stub"
                log.setEmpresa(e);
            }
            log.setAccion(accion.toUpperCase());
            log.setDetalles(detalles);

            auditLogRepository.save(log);
        } catch (Exception e) {
            System.err.println("FALLO AL GUARDAR EL LOG DE AUDITORÍA: " + e.getMessage());
        }
    }
}