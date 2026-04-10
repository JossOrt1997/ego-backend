-- =============================================================================
-- PROYECTO IDA - SCRIPT DE BASE DE DATOS COMPLETA PARA POSTGRESQL
-- EMPRESA: ANALITICA SOFTWARE
-- VERSION: 1.0
-- =============================================================================

-- -----------------------------------------------------------------------------
-- FUNCIÓN AUXILIAR PARA ACTUALIZAR TIMESTAMPS
-- Esta función se usará en triggers para actualizar automáticamente el campo updated_at
-- -----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION trigger_set_timestamp()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- =============================================================================
-- MÓDULO 1: NÚCLEO DEL SISTEMA (SEGURIDAD, AUDITORÍA Y CONFIGURACIÓN)
-- Tablas fundamentales para el funcionamiento de la plataforma SaaS.
-- =============================================================================

-- Tabla de Empresas (Clientes de Analitica Software)
CREATE TABLE empresa (
                         id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         rfc VARCHAR(13),
                         plan_type VARCHAR(50) NOT NULL,
                         status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TRIGGER set_timestamp_empresa BEFORE UPDATE ON empresa FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();

-- Tabla de configuración por empresa (Feature Flags)
CREATE TABLE empresa_configuracion (
                                       id BIGSERIAL PRIMARY KEY,
                                       empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                                       clave VARCHAR(100) NOT NULL,
                                       valor VARCHAR(255) NOT NULL,
                                       UNIQUE (empresa_id, clave)
);

-- Tabla de Módulos disponibles en la plataforma IDA
CREATE TABLE modulo (
                        id BIGSERIAL PRIMARY KEY,
                        nombre VARCHAR(100) NOT NULL UNIQUE,
                        descripcion TEXT
);

-- Tabla de Roles (puestos dentro de una empresa)
CREATE TABLE rol (
                     id BIGSERIAL PRIMARY KEY,
                     empresa_id BIGINT REFERENCES empresa(id) ON DELETE CASCADE, -- NULL si es un rol global
                     nombre VARCHAR(100) NOT NULL,
                     descripcion TEXT
);

-- Tabla de Personas (datos personales, desacoplado de usuarios)
CREATE TABLE persona (
                         id BIGSERIAL PRIMARY KEY,
                         nombre VARCHAR(255) NOT NULL,
                         apellido VARCHAR(255),
                         rfc VARCHAR(13),
                         curp VARCHAR(18)
);

-- Tabla de Usuarios (cuentas de acceso al sistema)
CREATE TABLE usuario (
                         id BIGSERIAL PRIMARY KEY,
                         empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                         rol_id BIGINT NOT NULL REFERENCES rol(id),
                         persona_id BIGINT REFERENCES persona(id),
                         email VARCHAR(255) NOT NULL UNIQUE,
                         password_hash TEXT NOT NULL,
                         is_active BOOLEAN NOT NULL DEFAULT TRUE,
                         last_login TIMESTAMPTZ,
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TRIGGER set_timestamp_usuario BEFORE UPDATE ON usuario FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();

-- Tabla Pivote para Permisos (conecta Roles y Módulos)
CREATE TABLE rol_acceso_modulo (
                                   rol_id BIGINT NOT NULL REFERENCES rol(id) ON DELETE CASCADE,
                                   modulo_id BIGINT NOT NULL REFERENCES modulo(id) ON DELETE CASCADE,
                                   permisos JSONB,
                                   PRIMARY KEY (rol_id, modulo_id)
);


-- =============================================================================
-- MÓDULO 2: CRM Y ERP
-- Tablas para la gestión de clientes, productos, categorías e inventarios.
-- =============================================================================

CREATE TABLE cliente (
                         id BIGSERIAL PRIMARY KEY,
                         empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                         persona_id BIGINT REFERENCES persona(id),
                         nombre_comercial VARCHAR(255),
                         rfc VARCHAR(13),
                         email_contacto VARCHAR(255),
                         telefono_contacto VARCHAR(50),
                         status VARCHAR(50) NOT NULL DEFAULT 'ACTIVO',
                         created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TRIGGER set_timestamp_cliente BEFORE UPDATE ON cliente FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TABLE categoria_producto (
                                    id BIGSERIAL PRIMARY KEY,
                                    empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                                    nombre VARCHAR(255) NOT NULL,
                                    descripcion TEXT
);

CREATE TABLE producto (
                          id BIGSERIAL PRIMARY KEY,
                          empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                          categoria_id BIGINT REFERENCES categoria_producto(id),
                          sku VARCHAR(100) NOT NULL,
                          nombre VARCHAR(255) NOT NULL,
                          descripcion TEXT,
                          precio_venta NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
                          precio_compra NUMERIC(12, 2),
                          is_active BOOLEAN NOT NULL DEFAULT TRUE,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                          UNIQUE (empresa_id, sku)
);
CREATE TRIGGER set_timestamp_producto BEFORE UPDATE ON producto FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TABLE almacen (
                         id BIGSERIAL PRIMARY KEY,
                         empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                         nombre VARCHAR(255) NOT NULL,
                         direccion TEXT
);

CREATE TABLE inventario (
                            id BIGSERIAL PRIMARY KEY,
                            producto_id BIGINT NOT NULL REFERENCES producto(id) ON DELETE CASCADE,
                            almacen_id BIGINT NOT NULL REFERENCES almacen(id) ON DELETE CASCADE,
                            cantidad NUMERIC(12, 4) NOT NULL DEFAULT 0.0000,
                            updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                            UNIQUE (producto_id, almacen_id)
);
CREATE TRIGGER set_timestamp_inventario BEFORE UPDATE ON inventario FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();


-- =============================================================================
-- MÓDULO 3: VENTAS Y FACTURACIÓN
-- =============================================================================

CREATE TABLE venta (
                       id BIGSERIAL PRIMARY KEY,
                       empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                       cliente_id BIGINT NOT NULL REFERENCES cliente(id),
                       usuario_id BIGINT REFERENCES usuario(id),
                       subtotal NUMERIC(12, 2) NOT NULL,
                       impuestos NUMERIC(12, 2) NOT NULL,
                       total NUMERIC(12, 2) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       fecha_venta TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE venta_detalle (
                               id BIGSERIAL PRIMARY KEY,
                               venta_id BIGINT NOT NULL REFERENCES venta(id) ON DELETE CASCADE,
                               producto_id BIGINT NOT NULL REFERENCES producto(id),
                               cantidad NUMERIC(12, 4) NOT NULL,
                               precio_unitario NUMERIC(12, 2) NOT NULL,
                               subtotal_linea NUMERIC(12, 2) NOT NULL
);

CREATE TABLE factura (
                         id BIGSERIAL PRIMARY KEY,
                         empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                         venta_id BIGINT NOT NULL REFERENCES venta(id),
                         uuid_fiscal VARCHAR(36) UNIQUE,
                         status_sat VARCHAR(50),
                         fecha_timbrado TIMESTAMPTZ,
                         xml_timbrado TEXT,
                         pdf_url VARCHAR(512)
);


-- =============================================================================
-- MÓDULO 4: FLOTILLAS Y GPS
-- =============================================================================

CREATE TABLE vehiculo (
                          id BIGSERIAL PRIMARY KEY,
                          empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                          placa VARCHAR(20) UNIQUE,
                          marca VARCHAR(100),
                          modelo VARCHAR(100),
                          año INT,
                          nombre_identificador VARCHAR(100) NOT NULL,
                          is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE ubicacion_gps (
                               id BIGSERIAL PRIMARY KEY,
                               vehiculo_id BIGINT NOT NULL REFERENCES vehiculo(id) ON DELETE CASCADE,
                               lat NUMERIC(9, 6) NOT NULL,
                               lon NUMERIC(9, 6) NOT NULL,
                               velocidad NUMERIC(5, 2),
                               timestamp_gps TIMESTAMPTZ NOT NULL
);


-- =============================================================================
-- MÓDULO 5: SOPORTE Y CENTRO DE AYUDA
-- =============================================================================

CREATE TABLE tickets_soporte (
                                 id BIGSERIAL PRIMARY KEY,
                                 empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                                 usuario_creador_id BIGINT NOT NULL REFERENCES usuario(id),
                                 usuario_asignado_id BIGINT REFERENCES usuario(id),
                                 titulo VARCHAR(255) NOT NULL,
                                 descripcion TEXT,
                                 estado VARCHAR(50) NOT NULL DEFAULT 'ABIERTO',
                                 prioridad VARCHAR(50) NOT NULL DEFAULT 'MEDIA',
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TRIGGER set_timestamp_tickets_soporte BEFORE UPDATE ON tickets_soporte FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TABLE comentarios_ticket (
                                    id BIGSERIAL PRIMARY KEY,
                                    ticket_id BIGINT NOT NULL REFERENCES tickets_soporte(id) ON DELETE CASCADE,
                                    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
                                    comentario TEXT NOT NULL,
                                    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);


-- =============================================================================
-- MÓDULO 6: ECOMMERCE
-- =============================================================================

CREATE TABLE carrito_compras (
                                 id BIGSERIAL PRIMARY KEY,
                                 empresa_id BIGINT NOT NULL REFERENCES empresa(id) ON DELETE CASCADE,
                                 cliente_id BIGINT REFERENCES cliente(id),
                                 session_id VARCHAR(255),
                                 created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                                 updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE TRIGGER set_timestamp_carrito_compras BEFORE UPDATE ON carrito_compras FOR EACH ROW EXECUTE PROCEDURE trigger_set_timestamp();

CREATE TABLE carrito_detalle (
                                 id BIGSERIAL PRIMARY KEY,
                                 carrito_id BIGINT NOT NULL REFERENCES carrito_compras(id) ON DELETE CASCADE,
                                 producto_id BIGINT NOT NULL REFERENCES producto(id),
                                 cantidad NUMERIC(10, 2) NOT NULL,
                                 UNIQUE (carrito_id, producto_id)
);

-- =============================================================================
-- ÍNDICES PARA MEJORAR EL RENDIMIENTO DE LAS CONSULTAS
-- Se recomienda crear índices en todas las llaves foráneas y campos de búsqueda frecuente.
-- =============================================================================

CREATE INDEX ON empresa_configuracion (empresa_id);
CREATE INDEX ON rol (empresa_id);
CREATE INDEX ON usuario (empresa_id);
CREATE INDEX ON usuario (rol_id);
CREATE INDEX ON usuario (persona_id);
CREATE INDEX ON cliente (empresa_id);
CREATE INDEX ON categoria_producto (empresa_id);
CREATE INDEX ON producto (empresa_id);
CREATE INDEX ON producto (categoria_id);
CREATE INDEX ON almacen (empresa_id);
CREATE INDEX ON inventario (producto_id);
CREATE INDEX ON inventario (almacen_id);
CREATE INDEX ON venta (empresa_id);
CREATE INDEX ON venta (cliente_id);
CREATE INDEX ON venta_detalle (venta_id);
CREATE INDEX ON venta_detalle (producto_id);
CREATE INDEX ON factura (venta_id);
CREATE INDEX ON vehiculo (empresa_id);
CREATE INDEX ON ubicacion_gps (vehiculo_id);
CREATE INDEX ON ubicacion_gps (timestamp_gps);
CREATE INDEX ON tickets_soporte (empresa_id);
CREATE INDEX ON tickets_soporte (usuario_creador_id);
CREATE INDEX ON tickets_soporte (usuario_asignado_id);
CREATE INDEX ON comentarios_ticket (ticket_id);
CREATE INDEX ON carrito_compras (empresa_id);
CREATE INDEX ON carrito_compras (cliente_id);
CREATE INDEX ON carrito_detalle (carrito_id);
CREATE INDEX ON carrito_detalle (producto_id);

-- ===== FIN DEL SCRIPT =====