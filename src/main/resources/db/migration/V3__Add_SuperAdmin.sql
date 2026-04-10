-- 1. Crear la empresa "Sistema" para Analitica Software
-- (Usamos ON CONFLICT por si ya la creaste en pruebas)
INSERT INTO empresa (id, name, plan_type, status)
VALUES (1, 'Analitica Software (Sistema)', 'SYSTEM', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

-- 2. Crear el Rol de SUPER_ADMIN (asociado a la empresa 1)
INSERT INTO rol (id, nombre, descripcion, empresa_id)
VALUES (1, 'SUPER_ADMIN', 'Administrador global del sistema', 1)
ON CONFLICT (id) DO NOTHING;

-- 3. Crear el Usuario SUPER_ADMIN
-- (IMPORTANTE: ¡Reemplaza el hash de la contraseña!)
-- Este hash es para "password123". Cópialo de un usuario de prueba existente.
INSERT INTO usuario (email, password_hash, empresa_id, rol_id, is_active)
VALUES (
           'superadmin@analiticasoft.com',
           '$2a$10$fDffFDbC20L5b5wT8YvmbOxbBsQuaZvnBHJHSQ/Ijn6LiZmjeIoO.', -- <-- ESTE HASH ES PARA 'password123'. CÓPIALO DE TU BD
           1,
           1,
           true
       )
ON CONFLICT (email) DO NOTHING;

-- Actualizar la secuencia de IDs para evitar conflictos si la BD está limpia
-- (Opcional pero recomendado para PostgreSQL)
SELECT setval('empresa_id_seq', (SELECT MAX(id) FROM empresa));
SELECT setval('rol_id_seq', (SELECT MAX(id) FROM rol));