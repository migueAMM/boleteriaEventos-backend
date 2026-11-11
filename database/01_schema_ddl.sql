-- ============================================================
-- boleteriaEventos-backend  –  Oracle 21c XE  –  Schema DDL
-- Schema: BD1
-- ============================================================
SET DEFINE OFF;

-- ============================================================
-- DROP TABLES (reverse dependency order) for idempotency
-- ============================================================
DROP TABLE BD1.PAGOS_TRANSACCION    CASCADE CONSTRAINTS;
DROP TABLE BD1.TICKETS_INSCRIPCION  CASCADE CONSTRAINTS;
DROP TABLE BD1.ORDENES_COMPRA       CASCADE CONSTRAINTS;
DROP TABLE BD1.CUPONES_DESCUENTO    CASCADE CONSTRAINTS;
DROP TABLE BD1.CATEGORIAS_EVENTO    CASCADE CONSTRAINTS;
DROP TABLE BD1.EVENTOS              CASCADE CONSTRAINTS;
DROP TABLE BD1.ORGANIZADORES        CASCADE CONSTRAINTS;
DROP TABLE BD1.USUARIOS             CASCADE CONSTRAINTS;

-- ============================================================
-- 1. USUARIOS
-- ============================================================
CREATE TABLE BD1.USUARIOS (
    id_usuario        NUMBER GENERATED ALWAYS AS IDENTITY,
    tipo_documento    VARCHAR2(20),
    numero_documento  VARCHAR2(30),
    nombres           VARCHAR2(100),
    apellidos         VARCHAR2(100),
    email             VARCHAR2(150),
    password_hash     VARCHAR2(255),
    telefono          VARCHAR2(20),
    fecha_nacimiento  DATE,
    genero            VARCHAR2(20),
    rol               VARCHAR2(20),
    fecha_registro    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo            NUMBER(1) DEFAULT 1,
    CONSTRAINT PK_USUARIOS          PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_USUARIO_DOCUMENTO UNIQUE (numero_documento),
    CONSTRAINT UQ_USUARIO_EMAIL     UNIQUE (email),
    CONSTRAINT CHK_USUARIO_ROL      CHECK (rol IN ('ADMIN','ORGANIZADOR','COMPRADOR'))
);

-- ============================================================
-- 2. ORGANIZADORES
-- ============================================================
CREATE TABLE BD1.ORGANIZADORES (
    id_organizador     NUMBER GENERATED ALWAYS AS IDENTITY,
    id_usuario         NUMBER NOT NULL,
    nit                VARCHAR2(30),
    razon_social       VARCHAR2(200),
    nombre_comercial   VARCHAR2(200),
    descripcion        CLOB,
    sitio_web          VARCHAR2(300),
    telefono_contacto  VARCHAR2(20),
    verificado         NUMBER(1) DEFAULT 0,
    CONSTRAINT PK_ORGANIZADORES          PRIMARY KEY (id_organizador),
    CONSTRAINT FK_ORGANIZADORES_USUARIO  FOREIGN KEY (id_usuario) REFERENCES BD1.USUARIOS (id_usuario),
    CONSTRAINT UQ_ORGANIZADOR_NIT        UNIQUE (nit)
);

-- ============================================================
-- 3. EVENTOS
-- ============================================================
CREATE TABLE BD1.EVENTOS (
    id_evento                    NUMBER GENERATED ALWAYS AS IDENTITY,
    id_organizador               NUMBER NOT NULL,
    titulo                       VARCHAR2(300),
    slug                         VARCHAR2(300),
    descripcion                  CLOB,
    tipo_evento                  VARCHAR2(30),
    ciudad                       VARCHAR2(100),
    direccion                    VARCHAR2(300),
    lugar_nombre                 VARCHAR2(200),
    fecha_inicio_evento          TIMESTAMP,
    fecha_fin_evento             TIMESTAMP,
    fecha_apertura_inscripciones TIMESTAMP,
    fecha_cierre_inscripciones   TIMESTAMP,
    imagen_banner_url            VARCHAR2(500),
    aforo_maximo_total           NUMBER,
    estado                       VARCHAR2(20),
    CONSTRAINT PK_EVENTOS              PRIMARY KEY (id_evento),
    CONSTRAINT FK_EVENTOS_ORGANIZADOR  FOREIGN KEY (id_organizador) REFERENCES BD1.ORGANIZADORES (id_organizador),
    CONSTRAINT UQ_EVENTO_SLUG          UNIQUE (slug),
    CONSTRAINT CHK_EVENTO_TIPO         CHECK (tipo_evento IN ('DEPORTIVO','CONCIERTO','CONFERENCIA','FESTIVAL','OTRO')),
    CONSTRAINT CHK_EVENTO_ESTADO       CHECK (estado IN ('BORRADOR','PUBLICADO','AGOTADO','FINALIZADO','CANCELADO'))
);

-- ============================================================
-- 4. CATEGORIAS_EVENTO
-- ============================================================
CREATE TABLE BD1.CATEGORIAS_EVENTO (
    id_categoria       NUMBER GENERATED ALWAYS AS IDENTITY,
    id_evento          NUMBER NOT NULL,
    nombre             VARCHAR2(100),
    descripcion        VARCHAR2(500),
    precio_base        NUMBER(12,2),
    cargo_servicio     NUMBER(12,2),
    cupo_maximo        NUMBER,
    cupos_disponibles  NUMBER,
    edad_minima        NUMBER,
    edad_maxima        NUMBER,
    incluye_kit        NUMBER(1) DEFAULT 0,
    fecha_inicio_venta TIMESTAMP,
    fecha_fin_venta    TIMESTAMP,
    estado             VARCHAR2(20) DEFAULT 'ACTIVO',
    CONSTRAINT PK_CATEGORIAS_EVENTO         PRIMARY KEY (id_categoria),
    CONSTRAINT FK_CATEGORIAS_EVENTO_EVENTO  FOREIGN KEY (id_evento) REFERENCES BD1.EVENTOS (id_evento)
);

-- ============================================================
-- 5. CUPONES_DESCUENTO
-- ============================================================
CREATE TABLE BD1.CUPONES_DESCUENTO (
    id_cupon              NUMBER GENERATED ALWAYS AS IDENTITY,
    id_evento             NUMBER NOT NULL,
    codigo                VARCHAR2(50),
    porcentaje_descuento  NUMBER(5,2),
    usos_maximos          NUMBER,
    usos_actuales         NUMBER DEFAULT 0,
    fecha_expiracion      TIMESTAMP,
    activo                NUMBER(1) DEFAULT 1,
    CONSTRAINT PK_CUPONES_DESCUENTO         PRIMARY KEY (id_cupon),
    CONSTRAINT FK_CUPONES_DESCUENTO_EVENTO  FOREIGN KEY (id_evento) REFERENCES BD1.EVENTOS (id_evento),
    CONSTRAINT UQ_CUPON_EVENTO_CODIGO       UNIQUE (id_evento, codigo)
);

-- ============================================================
-- 6. ORDENES_COMPRA
-- ============================================================
CREATE TABLE BD1.ORDENES_COMPRA (
    id_orden                  NUMBER GENERATED ALWAYS AS IDENTITY,
    codigo_orden              VARCHAR2(50),
    id_comprador              NUMBER NOT NULL,
    id_evento                 NUMBER NOT NULL,
    id_cupon                  NUMBER,
    subtotal                  NUMBER(12,2),
    total_cargos_servicio     NUMBER(12,2),
    total_descuento           NUMBER(12,2) DEFAULT 0,
    monto_total               NUMBER(12,2),
    estado_orden              VARCHAR2(30),
    fecha_creacion            TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion_reserva  TIMESTAMP,
    CONSTRAINT PK_ORDENES_COMPRA           PRIMARY KEY (id_orden),
    CONSTRAINT UQ_ORDEN_CODIGO             UNIQUE (codigo_orden),
    CONSTRAINT FK_ORDENES_COMPRADOR        FOREIGN KEY (id_comprador) REFERENCES BD1.USUARIOS (id_usuario),
    CONSTRAINT FK_ORDENES_EVENTO           FOREIGN KEY (id_evento)    REFERENCES BD1.EVENTOS (id_evento),
    CONSTRAINT FK_ORDENES_CUPON            FOREIGN KEY (id_cupon)     REFERENCES BD1.CUPONES_DESCUENTO (id_cupon),
    CONSTRAINT CHK_ORDEN_ESTADO            CHECK (estado_orden IN ('PENDIENTE_PAGO','PAGADA','EXPIRADA','CANCELADA','REEMBOLSADA'))
);

-- ============================================================
-- 7. TICKETS_INSCRIPCION
-- ============================================================
CREATE TABLE BD1.TICKETS_INSCRIPCION (
    id_ticket                      NUMBER GENERATED ALWAYS AS IDENTITY,
    id_orden                       NUMBER NOT NULL,
    id_categoria                   NUMBER NOT NULL,
    codigo_ticket_qr               VARCHAR2(100),
    tipo_documento_asistente       VARCHAR2(20),
    documento_asistente            VARCHAR2(30),
    nombre_completo_asistente      VARCHAR2(200),
    email_asistente                VARCHAR2(150),
    telefono_asistente             VARCHAR2(20),
    talla_camiseta                 VARCHAR2(5),
    contacto_emergencia_nombre     VARCHAR2(200),
    contacto_emergencia_telefono   VARCHAR2(20),
    tipo_sangre                    VARCHAR2(10),
    numero_dorsal                  NUMBER,
    precio_unitario_pagado         NUMBER(12,2),
    check_in_realizado             NUMBER(1) DEFAULT 0,
    fecha_check_in                 TIMESTAMP,
    estado_ticket                  VARCHAR2(20),
    CONSTRAINT PK_TICKETS_INSCRIPCION        PRIMARY KEY (id_ticket),
    CONSTRAINT FK_TICKETS_ORDEN              FOREIGN KEY (id_orden)     REFERENCES BD1.ORDENES_COMPRA (id_orden),
    CONSTRAINT FK_TICKETS_CATEGORIA          FOREIGN KEY (id_categoria) REFERENCES BD1.CATEGORIAS_EVENTO (id_categoria),
    CONSTRAINT UQ_TICKET_QR                  UNIQUE (codigo_ticket_qr),
    CONSTRAINT CHK_TICKET_TALLA              CHECK (talla_camiseta IN ('XS','S','M','L','XL') OR talla_camiseta IS NULL),
    CONSTRAINT CHK_TICKET_ESTADO             CHECK (estado_ticket IN ('RESERVADO','EMITIDO','UTILIZADO','ANULADO'))
);

-- ============================================================
-- 8. PAGOS_TRANSACCION
-- ============================================================
CREATE TABLE BD1.PAGOS_TRANSACCION (
    id_pago             NUMBER GENERATED ALWAYS AS IDENTITY,
    id_orden            NUMBER NOT NULL,
    pasarela_pago       VARCHAR2(50),
    referencia_externa  VARCHAR2(100),
    metodo_pago         VARCHAR2(30),
    monto               NUMBER(12,2),
    estado_pago         VARCHAR2(20),
    fecha_pago          TIMESTAMP,
    CONSTRAINT PK_PAGOS_TRANSACCION      PRIMARY KEY (id_pago),
    CONSTRAINT FK_PAGOS_ORDEN            FOREIGN KEY (id_orden) REFERENCES BD1.ORDENES_COMPRA (id_orden),
    CONSTRAINT CHK_PAGO_METODO           CHECK (metodo_pago IN ('PSE','TARJETA_CREDITO','TARJETA_DEBITO','NEQUI','EFECTIVO')),
    CONSTRAINT CHK_PAGO_ESTADO           CHECK (estado_pago IN ('APROBADO','RECHAZADO','PENDIENTE','ERROR'))
);

-- ============================================================
-- INDEXES – FK columns and common filter / lookup columns
-- ============================================================
-- ORGANIZADORES
CREATE INDEX IDX_ORGANIZADORES_USUARIO   ON BD1.ORGANIZADORES (id_usuario);

-- EVENTOS
CREATE INDEX IDX_EVENTOS_ORGANIZADOR     ON BD1.EVENTOS (id_organizador);
CREATE INDEX IDX_EVENTOS_CIUDAD          ON BD1.EVENTOS (ciudad);
CREATE INDEX IDX_EVENTOS_TIPO            ON BD1.EVENTOS (tipo_evento);
CREATE INDEX IDX_EVENTOS_ESTADO          ON BD1.EVENTOS (estado);

-- CATEGORIAS_EVENTO
CREATE INDEX IDX_CATEGORIAS_EVENTO       ON BD1.CATEGORIAS_EVENTO (id_evento);

-- CUPONES_DESCUENTO
CREATE INDEX IDX_CUPONES_EVENTO          ON BD1.CUPONES_DESCUENTO (id_evento);

-- ORDENES_COMPRA
CREATE INDEX IDX_ORDENES_COMPRADOR       ON BD1.ORDENES_COMPRA (id_comprador);
CREATE INDEX IDX_ORDENES_EVENTO          ON BD1.ORDENES_COMPRA (id_evento);
CREATE INDEX IDX_ORDENES_CUPON           ON BD1.ORDENES_COMPRA (id_cupon);
CREATE INDEX IDX_ORDENES_ESTADO          ON BD1.ORDENES_COMPRA (estado_orden);

-- TICKETS_INSCRIPCION
CREATE INDEX IDX_TICKETS_ORDEN           ON BD1.TICKETS_INSCRIPCION (id_orden);
CREATE INDEX IDX_TICKETS_CATEGORIA       ON BD1.TICKETS_INSCRIPCION (id_categoria);
CREATE INDEX IDX_TICKETS_ESTADO          ON BD1.TICKETS_INSCRIPCION (estado_ticket);

-- PAGOS_TRANSACCION
CREATE INDEX IDX_PAGOS_ORDEN             ON BD1.PAGOS_TRANSACCION (id_orden);
CREATE INDEX IDX_PAGOS_ESTADO            ON BD1.PAGOS_TRANSACCION (estado_pago);

COMMIT;
