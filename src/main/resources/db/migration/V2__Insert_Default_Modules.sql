-- Insertamos los módulos base del sistema
INSERT INTO modulo (nombre, descripcion) VALUES ('CRM', 'Módulo de Gestión de Clientes') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO modulo (nombre, descripcion) VALUES ('ERP', 'Módulo de Inventario y Productos') ON CONFLICT (nombre) DO NOTHING;
INSERT INTO modulo (nombre, descripcion) VALUES ('GPS', 'Módulo de Monitoreo GPS') ON CONFLICT (nombre) DO NOTHING;