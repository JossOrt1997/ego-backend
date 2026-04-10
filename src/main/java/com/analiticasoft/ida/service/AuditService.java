package com.analiticasoft.ida.service;

import com.analiticasoft.ida.entity.Usuario;
import java.util.Map;

/**
 * Servicio para registrar eventos de auditoría de forma asíncrona.
 */
public interface AuditService {

    /**
     * Registra una acción de auditoría en la base de datos.
     *
     * @param usuario El usuario (ya autenticado) que realiza la acción.
     * @param accion El código de la acción (ej. "DELETE_USUARIO", "UPDATE_PERMISOS").
     * @param detalles Un mapa con el contexto de la acción (ej. {"usuarioAfectadoId": 15}).
     */
    void logAction(Usuario usuario, String accion, Map<String, Object> detalles);

    /**
     * Sobrecarga del método para cuando no tenemos el objeto Usuario completo.
     */
    void logAction(String usuarioEmail, Long empresaId, String accion, Map<String, Object> detalles);
}