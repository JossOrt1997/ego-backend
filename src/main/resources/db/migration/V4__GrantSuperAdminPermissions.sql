-- V4__GrantSuperAdminPermissions.sql
-- Asigna todos los permisos (CRUDL) a ROLE_SUPER_ADMIN (rol_id = 1)
-- para los módulos base (CRM, ERP, GPS).

-- 1. Permisos para CRM (modulo_id = 1)
INSERT INTO rol_acceso_modulo (rol_id, modulo_id, permisos)
VALUES (
           1, -- rol_id 1 = SUPER_ADMIN
           1, -- modulo_id 1 = CRM
           '{"leer": true, "crear": true, "actualizar": true, "borrar": true}'::jsonb
       )
ON CONFLICT (rol_id, modulo_id) DO UPDATE SET permisos = EXCLUDED.permisos;

-- 2. Permisos para ERP (modulo_id = 2)
INSERT INTO rol_acceso_modulo (rol_id, modulo_id, permisos)
VALUES (
           1, -- rol_id 1 = SUPER_ADMIN
           2, -- modulo_id 2 = ERP
           '{"leer": true, "crear": true, "actualizar": true, "borrar": true}'::jsonb
       )
ON CONFLICT (rol_id, modulo_id) DO UPDATE SET permisos = EXCLUDED.permisos;

-- 3. Permisos para GPS (modulo_id = 3)
INSERT INTO rol_acceso_modulo (rol_id, modulo_id, permisos)
VALUES (
           1, -- rol_id 1 = SUPER_ADMIN
           3, -- modulo_id 3 = GPS
           '{"leer": true, "crear": true, "actualizar": true, "borrar": true}'::jsonb
       )
ON CONFLICT (rol_id, modulo_id) DO UPDATE SET permisos = EXCLUDED.permisos;