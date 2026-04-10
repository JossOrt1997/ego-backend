-- V5__Create_Audit_Log_Table.sql
-- Crea la tabla para el registro de auditoría de acciones críticas

CREATE TABLE audit_log (
                           id BIGSERIAL PRIMARY KEY,
                           timestamp TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Quién hizo la acción (email del usuario)
                           usuario_email VARCHAR(255) NOT NULL,

    -- Qué acción se realizó
                           accion VARCHAR(100) NOT NULL, -- Ej. "DELETE_USUARIO", "UPDATE_PERMISOS"

    -- A qué empresa afectó (para filtrar por inquilino)
                           empresa_id BIGINT REFERENCES empresa(id) ON DELETE SET NULL,

    -- Detalles adicionales de la acción
                           detalles JSONB
);

-- Índices para acelerar las búsquedas en el panel de auditoría
CREATE INDEX idx_audit_log_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_log_usuario_email ON audit_log(usuario_email);
CREATE INDEX idx_audit_log_accion ON audit_log(accion);
CREATE INDEX idx_audit_log_empresa_id ON audit_log(empresa_id);