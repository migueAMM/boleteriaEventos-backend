-- ============================================================
-- boleteriaEventos-backend  –  Oracle 21c XE  –  Seed DML
-- Schema: BD1
-- ============================================================
SET DEFINE OFF;

-- ============================================================
-- 1. MUNICIPIOS
-- ============================================================
INSERT INTO BD1.MUNICIPIOS (nombre, departamento) VALUES ('Medellín', 'Antioquia');
INSERT INTO BD1.MUNICIPIOS (nombre, departamento) VALUES ('Guatapé', 'Antioquia');
INSERT INTO BD1.MUNICIPIOS (nombre, departamento) VALUES ('Bogotá D.C.', 'Cundinamarca');
INSERT INTO BD1.MUNICIPIOS (nombre, departamento) VALUES ('Armenia', 'Quindío');
INSERT INTO BD1.MUNICIPIOS (nombre, departamento) VALUES ('Cartagena', 'Bolívar');

-- ============================================================
-- 2. TIPOS_PRESTADOR
-- ============================================================
INSERT INTO BD1.TIPOS_PRESTADOR (nombre) VALUES ('Parque Temático');
INSERT INTO BD1.TIPOS_PRESTADOR (nombre) VALUES ('Ecoparque');
INSERT INTO BD1.TIPOS_PRESTADOR (nombre) VALUES ('Centro Recreativo');
INSERT INTO BD1.TIPOS_PRESTADOR (nombre) VALUES ('Museo Interactivo');

-- ============================================================
-- 3. PRESTADORES
-- ============================================================
INSERT INTO BD1.PRESTADORES (nit, nombre, correo, id_municipio, id_tipo_prestador)
VALUES ('900123456-1', 'Parque Explora', 'contacto@parqueexplora.org', 1, 4);

INSERT INTO BD1.PRESTADORES (nit, nombre, correo, id_municipio, id_tipo_prestador)
VALUES ('900654321-2', 'Parque del Café S.A.S.', 'info@parquedelcafe.co', 4, 1);

INSERT INTO BD1.PRESTADORES (nit, nombre, correo, id_municipio, id_tipo_prestador)
VALUES ('901987654-3', 'Ecoparque El Peñol', 'reservas@ecoparquepenol.com', 2, 2);

-- ============================================================
-- 4. PLANES
-- ============================================================
INSERT INTO BD1.PLANES (nombre, descripcion, estado, id_prestador)
VALUES ('Plan Aventura Cafetera', 'Acceso ilimitado a atracciones mecánicas, show del café y almuerzo típico.', 'ACTIVO', 2);

INSERT INTO BD1.PLANES (nombre, descripcion, estado, id_prestador)
VALUES ('Plan Ciencia y Biodiversidad', 'Recorrido guiado por salas interactivas, acuario y planetario.', 'ACTIVO', 1);

INSERT INTO BD1.PLANES (nombre, descripcion, estado, id_prestador)
VALUES ('Plan Naturaleza y Embalse', 'Senderismo ecológico y deportes náuticos en la represa.', 'ACTIVO', 3);

-- ============================================================
-- 5. ROLES
-- ============================================================
INSERT INTO BD1.ROLES (nombre) VALUES ('ADMIN');
INSERT INTO BD1.ROLES (nombre) VALUES ('OPERADOR');
INSERT INTO BD1.ROLES (nombre) VALUES ('CLIENTE');

-- ============================================================
-- 6. USUARIOS_SISTEMA
-- ============================================================
INSERT INTO BD1.USUARIOS_SISTEMA (correo, clave_hash, estado, id_rol)
VALUES ('admin@boleteria.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVO', 1);

INSERT INTO BD1.USUARIOS_SISTEMA (correo, clave_hash, estado, id_rol)
VALUES ('operador@explora.org', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', 'ACTIVO', 2);

INSERT INTO BD1.USUARIOS_SISTEMA (correo, clave_hash, estado, id_rol)
VALUES ('carlos.gomez@gmail.com', '$2a$10$Dow5elC3zByMH2raGTJbaOCdQAfhDB3WkTbSTVNLPVjc5D3Ag7DKe', 'ACTIVO', 3);

INSERT INTO BD1.USUARIOS_SISTEMA (correo, clave_hash, estado, id_rol)
VALUES ('maria.lopez@hotmail.com', '$2a$10$hKQzO8eTQ2S9Xr1EpcvOBuPJXxVx5Gw7NCdISaKrQfnLmZVWq9bXi', 'ACTIVO', 3);

-- ============================================================
-- 7. PERFILES_CLIENTE
-- ============================================================
INSERT INTO BD1.PERFILES_CLIENTE (documento, nombre, telefono, id_usuario)
VALUES ('1010101010', 'Carlos Gómez Pérez', '3001234567', 3);

INSERT INTO BD1.PERFILES_CLIENTE (documento, nombre, telefono, id_usuario)
VALUES ('2020202020', 'María López Rodríguez', '3109876543', 4);

-- ============================================================
-- 8. ATRACCIONES
-- ============================================================
INSERT INTO BD1.ATRACCIONES (nombre, descripcion, id_prestador)
VALUES ('Acuario de Medellín', 'Acuario de agua dulce y salada con más de 400 especies protegidas.', 1);

INSERT INTO BD1.ATRACCIONES (nombre, descripcion, id_prestador)
VALUES ('Planetario Jesús Emilio Ramírez', 'Domo de inmersión digital con proyecciones astronómicas 4K.', 1);

INSERT INTO BD1.ATRACCIONES (nombre, descripcion, id_prestador)
VALUES ('Montaña Rusa Kráter', 'Montaña rusa de alta velocidad con giros e inversión de 360 grados.', 2);

INSERT INTO BD1.ATRACCIONES (nombre, descripcion, id_prestador)
VALUES ('Teleférico Panorámico', 'Recorrido aéreo por paisajes cafeteros y cordillera andina.', 2);

-- ============================================================
-- 9. FUNCIONES
-- ============================================================
INSERT INTO BD1.FUNCIONES (fecha, hora_apertura, hora_cierre, estado, id_atraccion)
VALUES (TO_DATE('2025-10-15','YYYY-MM-DD'), INTERVAL '09:00:00' HOUR TO SECOND, INTERVAL '12:00:00' HOUR TO SECOND, 'PROGRAMADA', 1);

INSERT INTO BD1.FUNCIONES (fecha, hora_apertura, hora_cierre, estado, id_atraccion)
VALUES (TO_DATE('2025-10-15','YYYY-MM-DD'), INTERVAL '14:00:00' HOUR TO SECOND, INTERVAL '17:00:00' HOUR TO SECOND, 'PROGRAMADA', 1);

INSERT INTO BD1.FUNCIONES (fecha, hora_apertura, hora_cierre, estado, id_atraccion)
VALUES (TO_DATE('2025-10-15','YYYY-MM-DD'), INTERVAL '10:00:00' HOUR TO SECOND, INTERVAL '18:00:00' HOUR TO SECOND, 'PROGRAMADA', 3);

-- ============================================================
-- 10. ZONAS
-- ============================================================
INSERT INTO BD1.ZONAS (nombre, aforo, precio, id_funcion)
VALUES ('Acceso General Acuario', 200, 28000.00, 1);

INSERT INTO BD1.ZONAS (nombre, aforo, precio, id_funcion)
VALUES ('Zona VIP Experiencia Marina', 30, 45000.00, 1);

INSERT INTO BD1.ZONAS (nombre, aforo, precio, id_funcion)
VALUES ('Acceso Rápido Kráter', 100, 35000.00, 3);

-- ============================================================
-- 11. TIPOS_ENTRADA
-- ============================================================
INSERT INTO BD1.TIPOS_ENTRADA (nombre, porcentaje_descuento, requiere_adulto)
VALUES ('General Adulto', 0.00, 0);

INSERT INTO BD1.TIPOS_ENTRADA (nombre, porcentaje_descuento, requiere_adulto)
VALUES ('Niño (menor de 12 años)', 20.00, 1);

INSERT INTO BD1.TIPOS_ENTRADA (nombre, porcentaje_descuento, requiere_adulto)
VALUES ('Adulto Mayor (60+)', 25.00, 0);

INSERT INTO BD1.TIPOS_ENTRADA (nombre, porcentaje_descuento, requiere_adulto)
VALUES ('Estudiante con Carnet', 15.00, 0);

-- ============================================================
-- 12. COMBOS
-- ============================================================
INSERT INTO BD1.COMBOS (nombre, precio, dias_vigencia, estado)
VALUES ('Super Combo Explora + Planetario', 55000.00, 30, 'ACTIVO');

INSERT INTO BD1.COMBOS (nombre, precio, dias_vigencia, estado)
VALUES ('Pasaporte Doble Diversión Café', 60000.00, 15, 'ACTIVO');

-- ============================================================
-- 13. COMBOS_ATRACCIONES
-- ============================================================
INSERT INTO BD1.COMBOS_ATRACCIONES (id_combo, id_atraccion, porcentaje_precio)
VALUES (1, 1, 50.00);

INSERT INTO BD1.COMBOS_ATRACCIONES (id_combo, id_atraccion, porcentaje_precio)
VALUES (1, 2, 50.00);

INSERT INTO BD1.COMBOS_ATRACCIONES (id_combo, id_atraccion, porcentaje_precio)
VALUES (2, 3, 60.00);

INSERT INTO BD1.COMBOS_ATRACCIONES (id_combo, id_atraccion, porcentaje_precio)
VALUES (2, 4, 40.00);

-- ============================================================
-- 14. RESERVAS
-- ============================================================
INSERT INTO BD1.RESERVAS (fecha_hora, estado, id_perfil, id_plan)
VALUES (TIMESTAMP '2025-10-10 14:30:00', 'CONFIRMADA', 1, 2);

INSERT INTO BD1.RESERVAS (fecha_hora, estado, id_perfil, id_plan)
VALUES (TIMESTAMP '2025-10-11 10:15:00', 'CONFIRMADA', 2, 1);

-- ============================================================
-- 15. BOLETAS
-- ============================================================
INSERT INTO BD1.BOLETAS (codigo, estado, precio_pagado, id_reserva, id_zona, id_tipo_entrada, id_combo)
VALUES ('BOL-EXP-2025-001', 'ACTIVA', 28000.00, 1, 1, 1, NULL);

INSERT INTO BD1.BOLETAS (codigo, estado, precio_pagado, id_reserva, id_zona, id_tipo_entrada, id_combo)
VALUES ('BOL-EXP-2025-002', 'ACTIVA', 22400.00, 1, 1, 2, NULL);

INSERT INTO BD1.BOLETAS (codigo, estado, precio_pagado, id_reserva, id_zona, id_tipo_entrada, id_combo)
VALUES ('BOL-CAF-2025-001', 'ACTIVA', 60000.00, 2, NULL, 1, 2);

-- ============================================================
-- 16. NOTIFICACIONES
-- ============================================================
INSERT INTO BD1.NOTIFICACIONES (asunto, mensaje, fecha_hora, leida, id_usuario, id_reserva)
VALUES ('Confirmación de Reserva', 'Tu reserva para el Plan Ciencia y Biodiversidad ha sido confirmada exitosamente.', TIMESTAMP '2025-10-10 14:30:05', 1, 3, 1);

INSERT INTO BD1.NOTIFICACIONES (asunto, mensaje, fecha_hora, leida, id_usuario, id_reserva)
VALUES ('Recordatorio de Visita', 'Recuerda llegar 15 minutos antes de la hora de inicio de tu función.', TIMESTAMP '2025-10-14 08:00:00', 0, 3, 1);

INSERT INTO BD1.NOTIFICACIONES (asunto, mensaje, fecha_hora, leida, id_usuario, id_reserva)
VALUES ('Confirmación de Reserva', 'Tu reserva para el Plan Aventura Cafetera ha sido confirmada.', TIMESTAMP '2025-10-11 10:15:10', 0, 4, 2);

COMMIT;
