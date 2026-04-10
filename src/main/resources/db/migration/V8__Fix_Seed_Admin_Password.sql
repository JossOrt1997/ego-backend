-- Ajusta hash del usuario seed para asegurar acceso inicial
-- email: admin@kedevel.com
-- password: Admin12345

UPDATE usuario
SET password_hash = '$2a$10$uZ5t/EjMqjn2cdSKoMJe1.GrbAMPf6JridReYEwYChPCYkRzsrlGi'
WHERE email = 'admin@kedevel.com';
