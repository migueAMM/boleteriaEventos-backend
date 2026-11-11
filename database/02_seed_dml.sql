-- ============================================================
-- boleteriaEventos-backend  –  Oracle 21c XE  –  Seed DML
-- Schema: BD1
-- ============================================================
SET DEFINE OFF;

-- ============================================================
-- USUARIOS  (5 users: 1 admin, 2 organizers, 2 buyers)
-- Password hashes are bcrypt-style placeholders
-- ============================================================
INSERT INTO BD1.USUARIOS (tipo_documento, numero_documento, nombres, apellidos, email, password_hash, telefono, fecha_nacimiento, genero, rol, activo)
VALUES ('CC', '1010101010', 'Carlos', 'Administrador López', 'admin@boleteria.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '3001234567', TO_DATE('1985-03-15','YYYY-MM-DD'), 'MASCULINO', 'ADMIN', 1);

INSERT INTO BD1.USUARIOS (tipo_documento, numero_documento, nombres, apellidos, email, password_hash, telefono, fecha_nacimiento, genero, rol, activo)
VALUES ('CC', '2020202020', 'María', 'Rodríguez Gómez', 'maria.organizadora@correo.com', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36WQoeG6Lruj3vjPGga31lW', '3109876543', TO_DATE('1990-07-22','YYYY-MM-DD'), 'FEMENINO', 'ORGANIZADOR', 1);

INSERT INTO BD1.USUARIOS (tipo_documento, numero_documento, nombres, apellidos, email, password_hash, telefono, fecha_nacimiento, genero, rol, activo)
VALUES ('NIT', '3030303030', 'Andrés', 'Martínez Ruiz', 'andres.organizador@correo.com', '$2a$10$Dow5elC3zByMH2raGTJbaOCdQAfhDB3WkTbSTVNLPVjc5D3Ag7DKe', '3205551234', TO_DATE('1988-11-05','YYYY-MM-DD'), 'MASCULINO', 'ORGANIZADOR', 1);

INSERT INTO BD1.USUARIOS (tipo_documento, numero_documento, nombres, apellidos, email, password_hash, telefono, fecha_nacimiento, genero, rol, activo)
VALUES ('CC', '4040404040', 'Laura', 'Pérez Sánchez', 'laura.comprador@correo.com', '$2a$10$hKQzO8eTQ2S9Xr1EpcvOBuPJXxVx5Gw7NCdISaKrQfnLmZVWq9bXi', '3156667788', TO_DATE('1995-01-30','YYYY-MM-DD'), 'FEMENINO', 'COMPRADOR', 1);

INSERT INTO BD1.USUARIOS (tipo_documento, numero_documento, nombres, apellidos, email, password_hash, telefono, fecha_nacimiento, genero, rol, activo)
VALUES ('CE', '5050505050', 'Juan', 'García Torres', 'juan.comprador@correo.com', '$2a$10$RSuSx4zzU8Bp7.TY3DRDJ.N7b6v8Zx2Jz3aWj5F1WnRh0N9xH2kIS', '3187778899', TO_DATE('1992-06-18','YYYY-MM-DD'), 'MASCULINO', 'COMPRADOR', 1);

-- ============================================================
-- ORGANIZADORES  (2 linked to organizer users id 2 and 3)
-- ============================================================
INSERT INTO BD1.ORGANIZADORES (id_usuario, nit, razon_social, nombre_comercial, descripcion, sitio_web, telefono_contacto, verificado)
VALUES (2, '900123456-1', 'Eventos Deportivos Colombia S.A.S.', 'DeporEventos', 'Organizamos las mejores carreras y maratones en Colombia con recorridos certificados y experiencia premium.', 'https://www.deporeventos.co', '6012345678', 1);

INSERT INTO BD1.ORGANIZADORES (id_usuario, nit, razon_social, nombre_comercial, descripcion, sitio_web, telefono_contacto, verificado)
VALUES (3, '900789012-3', 'Productora Musical Andina Ltda.', 'Rock Andino Producciones', 'Productora líder de conciertos y festivales de rock en Medellín y el Eje Cafetero.', 'https://www.rockandino.co', '6047654321', 1);

-- ============================================================
-- EVENTOS  (2 events)
-- ============================================================
INSERT INTO BD1.EVENTOS (id_organizador, titulo, slug, descripcion, tipo_evento, ciudad, direccion, lugar_nombre, fecha_inicio_evento, fecha_fin_evento, fecha_apertura_inscripciones, fecha_cierre_inscripciones, imagen_banner_url, aforo_maximo_total, estado)
VALUES (1, 'Maratón Deportiva Bogotá 2025', 'maraton-deportiva-bogota-2025',
        'La carrera más importante de Colombia. Recorre las calles de Bogotá en distancias de 5K, 10K y 21K. Incluye kit deportivo, hidratación y medalla finisher.',
        'DEPORTIVO', 'Bogotá', 'Parque Simón Bolívar, Calle 63 #68-95', 'Parque Simón Bolívar',
        TIMESTAMP '2025-09-14 06:00:00', TIMESTAMP '2025-09-14 14:00:00',
        TIMESTAMP '2025-05-01 00:00:00', TIMESTAMP '2025-09-10 23:59:59',
        'https://cdn.boleteria.com/banners/maraton-bogota-2025.jpg', 15000, 'PUBLICADO');

INSERT INTO BD1.EVENTOS (id_organizador, titulo, slug, descripcion, tipo_evento, ciudad, direccion, lugar_nombre, fecha_inicio_evento, fecha_fin_evento, fecha_apertura_inscripciones, fecha_cierre_inscripciones, imagen_banner_url, aforo_maximo_total, estado)
VALUES (2, 'Concierto Rock en Medellín', 'concierto-rock-medellin-2025',
        'Una noche épica de rock con las mejores bandas nacionales e internacionales. Disfruta de 8 horas de música en vivo en el corazón de Medellín.',
        'CONCIERTO', 'Medellín', 'Estadio Atanasio Girardot, Carrera 74 #48-140', 'Estadio Atanasio Girardot',
        TIMESTAMP '2025-11-22 16:00:00', TIMESTAMP '2025-11-23 00:00:00',
        TIMESTAMP '2025-07-01 00:00:00', TIMESTAMP '2025-11-20 23:59:59',
        'https://cdn.boleteria.com/banners/rock-medellin-2025.jpg', 40000, 'PUBLICADO');

-- ============================================================
-- CATEGORIAS_EVENTO
-- Marathon: 5K, 10K, 21K   |  Concert: VIP, General
-- ============================================================
-- Marathon 5K
INSERT INTO BD1.CATEGORIAS_EVENTO (id_evento, nombre, descripcion, precio_base, cargo_servicio, cupo_maximo, cupos_disponibles, edad_minima, edad_maxima, incluye_kit, fecha_inicio_venta, fecha_fin_venta, estado)
VALUES (1, '5K', 'Carrera recreativa de 5 kilómetros. Ideal para principiantes. Incluye camiseta y medalla.', 45000.00, 5000.00, 5000, 4998, 15, 99, 1, TIMESTAMP '2025-05-01 00:00:00', TIMESTAMP '2025-09-10 23:59:59', 'ACTIVO');

-- Marathon 10K
INSERT INTO BD1.CATEGORIAS_EVENTO (id_evento, nombre, descripcion, precio_base, cargo_servicio, cupo_maximo, cupos_disponibles, edad_minima, edad_maxima, incluye_kit, fecha_inicio_venta, fecha_fin_venta, estado)
VALUES (1, '10K', 'Carrera atlética de 10 kilómetros con recorrido certificado. Incluye kit completo con camiseta técnica.', 70000.00, 7000.00, 5000, 4999, 16, 99, 1, TIMESTAMP '2025-05-01 00:00:00', TIMESTAMP '2025-09-10 23:59:59', 'ACTIVO');

-- Marathon 21K
INSERT INTO BD1.CATEGORIAS_EVENTO (id_evento, nombre, descripcion, precio_base, cargo_servicio, cupo_maximo, cupos_disponibles, edad_minima, edad_maxima, incluye_kit, fecha_inicio_venta, fecha_fin_venta, estado)
VALUES (1, '21K', 'Media maratón 21.1 km. Para corredores experimentados. Kit premium con camiseta técnica, morral y medalla.', 120000.00, 12000.00, 5000, 5000, 18, 99, 1, TIMESTAMP '2025-05-01 00:00:00', TIMESTAMP '2025-09-10 23:59:59', 'ACTIVO');

-- Concert VIP
INSERT INTO BD1.CATEGORIAS_EVENTO (id_evento, nombre, descripcion, precio_base, cargo_servicio, cupo_maximo, cupos_disponibles, edad_minima, edad_maxima, incluye_kit, fecha_inicio_venta, fecha_fin_venta, estado)
VALUES (2, 'VIP', 'Zona VIP con barra libre, baños exclusivos y vista preferencial al escenario.', 350000.00, 35000.00, 5000, 4999, 18, 99, 0, TIMESTAMP '2025-07-01 00:00:00', TIMESTAMP '2025-11-20 23:59:59', 'ACTIVO');

-- Concert General
INSERT INTO BD1.CATEGORIAS_EVENTO (id_evento, nombre, descripcion, precio_base, cargo_servicio, cupo_maximo, cupos_disponibles, edad_minima, edad_maxima, incluye_kit, fecha_inicio_venta, fecha_fin_venta, estado)
VALUES (2, 'General', 'Acceso a zona general con vista al escenario principal.', 150000.00, 15000.00, 35000, 34999, 18, 99, 0, TIMESTAMP '2025-07-01 00:00:00', TIMESTAMP '2025-11-20 23:59:59', 'ACTIVO');

-- ============================================================
-- CUPONES_DESCUENTO  (1 per event)
-- ============================================================
INSERT INTO BD1.CUPONES_DESCUENTO (id_evento, codigo, porcentaje_descuento, usos_maximos, usos_actuales, fecha_expiracion, activo)
VALUES (1, 'MARATON15', 15.00, 500, 1, TIMESTAMP '2025-09-01 23:59:59', 1);

INSERT INTO BD1.CUPONES_DESCUENTO (id_evento, codigo, porcentaje_descuento, usos_maximos, usos_actuales, fecha_expiracion, activo)
VALUES (2, 'ROCK20', 20.00, 300, 1, TIMESTAMP '2025-11-15 23:59:59', 1);

-- ============================================================
-- ORDENES_COMPRA  (1 per event, both PAGADA)
-- ============================================================
-- Order for marathon (buyer Laura, id 4) – uses coupon 1 (15% off)
-- 5K ticket = 45000 + 5000, 10K ticket = 70000 + 7000
-- subtotal = 115000, cargos = 12000, descuento = 115000*0.15 = 17250, total = 115000 + 12000 - 17250 = 109750
INSERT INTO BD1.ORDENES_COMPRA (codigo_orden, id_comprador, id_evento, id_cupon, subtotal, total_cargos_servicio, total_descuento, monto_total, estado_orden, fecha_expiracion_reserva)
VALUES ('ORD-2025-000001', 4, 1, 1, 115000.00, 12000.00, 17250.00, 109750.00, 'PAGADA', TIMESTAMP '2025-08-15 12:30:00');

-- Order for concert (buyer Juan, id 5) – uses coupon 2 (20% off)
-- VIP ticket = 350000 + 35000, General ticket = 150000 + 15000
-- subtotal = 500000, cargos = 50000, descuento = 500000*0.20 = 100000, total = 500000 + 50000 - 100000 = 450000
INSERT INTO BD1.ORDENES_COMPRA (codigo_orden, id_comprador, id_evento, id_cupon, subtotal, total_cargos_servicio, total_descuento, monto_total, estado_orden, fecha_expiracion_reserva)
VALUES ('ORD-2025-000002', 5, 2, 2, 500000.00, 50000.00, 100000.00, 450000.00, 'PAGADA', TIMESTAMP '2025-10-20 18:00:00');

-- ============================================================
-- TICKETS_INSCRIPCION  (4 total: 2 marathon, 2 concert)
-- ============================================================
-- Marathon – 5K ticket (Laura)
INSERT INTO BD1.TICKETS_INSCRIPCION (id_orden, id_categoria, codigo_ticket_qr, tipo_documento_asistente, documento_asistente, nombre_completo_asistente, email_asistente, telefono_asistente, talla_camiseta, contacto_emergencia_nombre, contacto_emergencia_telefono, tipo_sangre, numero_dorsal, precio_unitario_pagado, check_in_realizado, estado_ticket)
VALUES (1, 1, 'TKT-MAR5K-2025-0001', 'CC', '4040404040', 'Laura Pérez Sánchez', 'laura.comprador@correo.com', '3156667788', 'S', 'Pedro Pérez', '3101112233', 'O+', 1042, 50000.00, 0, 'EMITIDO');

-- Marathon – 10K ticket (friend of Laura)
INSERT INTO BD1.TICKETS_INSCRIPCION (id_orden, id_categoria, codigo_ticket_qr, tipo_documento_asistente, documento_asistente, nombre_completo_asistente, email_asistente, telefono_asistente, talla_camiseta, contacto_emergencia_nombre, contacto_emergencia_telefono, tipo_sangre, numero_dorsal, precio_unitario_pagado, check_in_realizado, estado_ticket)
VALUES (1, 2, 'TKT-MAR10K-2025-0002', 'CC', '6060606060', 'Camila Ríos Vargas', 'camila.rios@correo.com', '3124445566', 'M', 'Laura Pérez', '3156667788', 'A+', 2087, 77000.00, 0, 'EMITIDO');

-- Concert – VIP ticket (Juan)
INSERT INTO BD1.TICKETS_INSCRIPCION (id_orden, id_categoria, codigo_ticket_qr, tipo_documento_asistente, documento_asistente, nombre_completo_asistente, email_asistente, telefono_asistente, talla_camiseta, contacto_emergencia_nombre, contacto_emergencia_telefono, tipo_sangre, numero_dorsal, precio_unitario_pagado, check_in_realizado, estado_ticket)
VALUES (2, 4, 'TKT-RCKVIP-2025-0003', 'CE', '5050505050', 'Juan García Torres', 'juan.comprador@correo.com', '3187778899', NULL, 'Ana García', '3199990011', 'B+', NULL, 385000.00, 0, 'EMITIDO');

-- Concert – General ticket (friend of Juan)
INSERT INTO BD1.TICKETS_INSCRIPCION (id_orden, id_categoria, codigo_ticket_qr, tipo_documento_asistente, documento_asistente, nombre_completo_asistente, email_asistente, telefono_asistente, talla_camiseta, contacto_emergencia_nombre, contacto_emergencia_telefono, tipo_sangre, numero_dorsal, precio_unitario_pagado, check_in_realizado, estado_ticket)
VALUES (2, 5, 'TKT-RCKGEN-2025-0004', 'CC', '7070707070', 'Sebastián Muñoz Díaz', 'sebastian.munoz@correo.com', '3001122334', NULL, 'Juan García', '3187778899', 'AB-', NULL, 165000.00, 0, 'EMITIDO');

-- ============================================================
-- PAGOS_TRANSACCION  (1 per order, approved)
-- ============================================================
INSERT INTO BD1.PAGOS_TRANSACCION (id_orden, pasarela_pago, referencia_externa, metodo_pago, monto, estado_pago, fecha_pago)
VALUES (1, 'MercadoPago', 'MP-REF-2025-ABC123', 'PSE', 109750.00, 'APROBADO', TIMESTAMP '2025-08-15 10:05:32');

INSERT INTO BD1.PAGOS_TRANSACCION (id_orden, pasarela_pago, referencia_externa, metodo_pago, monto, estado_pago, fecha_pago)
VALUES (2, 'PayU', 'PU-REF-2025-XYZ789', 'TARJETA_CREDITO', 450000.00, 'APROBADO', TIMESTAMP '2025-10-20 15:22:17');

COMMIT;
