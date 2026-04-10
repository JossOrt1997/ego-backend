-- Seed de usuario admin por defecto para primer acceso
-- email: admin@kedevel.com
-- password: Password123

INSERT INTO empresa (id, name, plan_type, status)
VALUES (100, 'Kedevel Demo', 'FREE', 'ACTIVE')
ON CONFLICT (id) DO NOTHING;

INSERT INTO rol (id, nombre, descripcion, empresa_id)
VALUES (100, 'ADMIN', 'Administrador tenant por defecto', 100)
ON CONFLICT (id) DO NOTHING;

INSERT INTO usuario (email, password_hash, empresa_id, rol_id, is_active)
VALUES (
  'admin@kedevel.com',
  '$2a$10$uZ5t/EjMqjn2cdSKoMJe1.GrbAMPf6JridReYEwYChPCYkRzsrlGi',
  100,
  100,
  true
)
ON CONFLICT (email) DO NOTHING;

SELECT setval('empresa_id_seq', GREATEST((SELECT COALESCE(MAX(id),1) FROM empresa), 100));
SELECT setval('rol_id_seq', GREATEST((SELECT COALESCE(MAX(id),1) FROM rol), 100));
