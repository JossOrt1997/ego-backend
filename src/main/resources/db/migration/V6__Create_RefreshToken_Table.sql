-- V6__Create_RefreshToken_Table.sql
-- Crea la tabla para almacenar tokens de refresco de larga duración

CREATE TABLE refresh_token (
                               id BIGSERIAL PRIMARY KEY,

    -- El token en sí (debe ser único)
                               token TEXT NOT NULL UNIQUE,

    -- Vínculo al usuario (OneToOne)
                               usuario_id BIGINT UNIQUE NOT NULL REFERENCES usuario(id) ON DELETE CASCADE,

    -- Fecha de expiración (para revocar tokens antiguos)
                               expiry_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_refresh_token_user ON refresh_token(usuario_id);