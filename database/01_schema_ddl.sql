-- ============================================================
-- boleteriaEventos-backend  –  Oracle 21c XE  –  Schema DDL
-- Schema: BD1
-- ============================================================
SET DEFINE OFF;

-- ============================================================
-- DROP TABLES (reverse dependency order) for idempotency
-- ============================================================
-- Tablas del modelo actual
DROP TABLE BD1.NOTIFICACIONES       CASCADE CONSTRAINTS;
DROP TABLE BD1.BOLETAS              CASCADE CONSTRAINTS;
DROP TABLE BD1.RESERVAS             CASCADE CONSTRAINTS;
DROP TABLE BD1.COMBOS_ATRACCIONES   CASCADE CONSTRAINTS;
DROP TABLE BD1.ZONAS                CASCADE CONSTRAINTS;
DROP TABLE BD1.FUNCIONES            CASCADE CONSTRAINTS;
DROP TABLE BD1.ATRACCIONES          CASCADE CONSTRAINTS;
DROP TABLE BD1.PERFILES_CLIENTE     CASCADE CONSTRAINTS;
DROP TABLE BD1.USUARIOS_SISTEMA     CASCADE CONSTRAINTS;
DROP TABLE BD1.PLANES               CASCADE CONSTRAINTS;
DROP TABLE BD1.PRESTADORES          CASCADE CONSTRAINTS;
DROP TABLE BD1.COMBOS               CASCADE CONSTRAINTS;
DROP TABLE BD1.TIPOS_ENTRADA        CASCADE CONSTRAINTS;
DROP TABLE BD1.ROLES                CASCADE CONSTRAINTS;
DROP TABLE BD1.TIPOS_PRESTADOR      CASCADE CONSTRAINTS;
DROP TABLE BD1.MUNICIPIOS           CASCADE CONSTRAINTS;

-- Limpieza de tablas previas/antiguas si existieran
DROP TABLE BD1.PAGOS_TRANSACCION    CASCADE CONSTRAINTS;
DROP TABLE BD1.TICKETS_INSCRIPCION  CASCADE CONSTRAINTS;
DROP TABLE BD1.ORDENES_COMPRA       CASCADE CONSTRAINTS;
DROP TABLE BD1.CUPONES_DESCUENTO    CASCADE CONSTRAINTS;
DROP TABLE BD1.CATEGORIAS_EVENTO    CASCADE CONSTRAINTS;
DROP TABLE BD1.EVENTOS              CASCADE CONSTRAINTS;
DROP TABLE BD1.ORGANIZADORES        CASCADE CONSTRAINTS;
DROP TABLE BD1.USUARIOS             CASCADE CONSTRAINTS;

-- ============================================================
-- 1. MUNICIPIOS
-- ============================================================
CREATE TABLE BD1.MUNICIPIOS (
    id_municipio        NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(100) NOT NULL,
    departamento        VARCHAR2(100) NOT NULL,
    CONSTRAINT PK_MUNICIPIOS PRIMARY KEY (id_municipio)
);

-- ============================================================
-- 2. TIPOS_PRESTADOR
-- ============================================================
CREATE TABLE BD1.TIPOS_PRESTADOR (
    id_tipo_prestador   NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(100) NOT NULL,
    CONSTRAINT PK_TIPOS_PRESTADOR PRIMARY KEY (id_tipo_prestador)
);

-- ============================================================
-- 3. PRESTADORES
-- ============================================================
CREATE TABLE BD1.PRESTADORES (
    id_prestador        NUMBER GENERATED ALWAYS AS IDENTITY,
    nit                 VARCHAR2(30)  NOT NULL,
    nombre              VARCHAR2(150) NOT NULL,
    correo              VARCHAR2(150) NOT NULL,
    id_municipio        NUMBER        NOT NULL,
    id_tipo_prestador   NUMBER        NOT NULL,
    CONSTRAINT PK_PRESTADORES PRIMARY KEY (id_prestador),
    CONSTRAINT UQ_PRESTADOR_NIT UNIQUE (nit),
    CONSTRAINT FK_PRESTADOR_MUNICIPIO FOREIGN KEY (id_municipio) REFERENCES BD1.MUNICIPIOS (id_municipio),
    CONSTRAINT FK_PRESTADOR_TIPO FOREIGN KEY (id_tipo_prestador) REFERENCES BD1.TIPOS_PRESTADOR (id_tipo_prestador)
);

-- ============================================================
-- 4. PLANES
-- ============================================================
CREATE TABLE BD1.PLANES (
    id_plan             NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(150) NOT NULL,
    descripcion         VARCHAR2(1000),
    estado              VARCHAR2(30)  NOT NULL,
    id_prestador        NUMBER        NOT NULL,
    CONSTRAINT PK_PLANES PRIMARY KEY (id_plan),
    CONSTRAINT FK_PLANES_PRESTADOR FOREIGN KEY (id_prestador) REFERENCES BD1.PRESTADORES (id_prestador)
);

-- ============================================================
-- 5. ROLES
-- ============================================================
CREATE TABLE BD1.ROLES (
    id_rol              NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(50)  NOT NULL,
    CONSTRAINT PK_ROLES PRIMARY KEY (id_rol),
    CONSTRAINT UQ_ROL_NOMBRE UNIQUE (nombre)
);

-- ============================================================
-- 6. USUARIOS_SISTEMA
-- ============================================================
CREATE TABLE BD1.USUARIOS_SISTEMA (
    id_usuario          NUMBER GENERATED ALWAYS AS IDENTITY,
    correo              VARCHAR2(150) NOT NULL,
    clave_hash          VARCHAR2(255) NOT NULL,
    estado              VARCHAR2(30)  NOT NULL,
    id_rol              NUMBER        NOT NULL,
    CONSTRAINT PK_USUARIOS_SISTEMA PRIMARY KEY (id_usuario),
    CONSTRAINT UQ_USUARIO_CORREO UNIQUE (correo),
    CONSTRAINT FK_USUARIO_ROL FOREIGN KEY (id_rol) REFERENCES BD1.ROLES (id_rol)
);

-- ============================================================
-- 7. PERFILES_CLIENTE
-- ============================================================
CREATE TABLE BD1.PERFILES_CLIENTE (
    id_perfil           NUMBER GENERATED ALWAYS AS IDENTITY,
    documento           VARCHAR2(30)  NOT NULL,
    nombre              VARCHAR2(150) NOT NULL,
    telefono            VARCHAR2(30),
    id_usuario          NUMBER        NOT NULL,
    CONSTRAINT PK_PERFILES_CLIENTE PRIMARY KEY (id_perfil),
    CONSTRAINT UQ_PERFIL_DOCUMENTO UNIQUE (documento),
    CONSTRAINT UQ_PERFIL_USUARIO UNIQUE (id_usuario),
    CONSTRAINT FK_PERFIL_USUARIO FOREIGN KEY (id_usuario) REFERENCES BD1.USUARIOS_SISTEMA (id_usuario)
);

-- ============================================================
-- 8. ATRACCIONES
-- ============================================================
CREATE TABLE BD1.ATRACCIONES (
    id_atraccion        NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(150) NOT NULL,
    descripcion         VARCHAR2(1000),
    id_prestador        NUMBER        NOT NULL,
    CONSTRAINT PK_ATRACCIONES PRIMARY KEY (id_atraccion),
    CONSTRAINT FK_ATRACCIONES_PRESTADOR FOREIGN KEY (id_prestador) REFERENCES BD1.PRESTADORES (id_prestador)
);

-- ============================================================
-- 9. FUNCIONES
-- ============================================================
CREATE TABLE BD1.FUNCIONES (
    id_funcion          NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha               DATE          NOT NULL,
    hora_apertura       INTERVAL DAY(0) TO SECOND(6) NOT NULL,
    hora_cierre         INTERVAL DAY(0) TO SECOND(6) NOT NULL,
    estado              VARCHAR2(30)  NOT NULL,
    id_atraccion        NUMBER        NOT NULL,
    CONSTRAINT PK_FUNCIONES PRIMARY KEY (id_funcion),
    CONSTRAINT FK_FUNCIONES_ATRACCION FOREIGN KEY (id_atraccion) REFERENCES BD1.ATRACCIONES (id_atraccion)
);

-- ============================================================
-- 10. ZONAS
-- ============================================================
CREATE TABLE BD1.ZONAS (
    id_zona             NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(100) NOT NULL,
    aforo               NUMBER(10)    NOT NULL,
    precio              NUMBER(12,2)  NOT NULL,
    id_funcion          NUMBER        NOT NULL,
    CONSTRAINT PK_ZONAS PRIMARY KEY (id_zona),
    CONSTRAINT FK_ZONAS_FUNCION FOREIGN KEY (id_funcion) REFERENCES BD1.FUNCIONES (id_funcion)
);

-- ============================================================
-- 11. TIPOS_ENTRADA
-- ============================================================
CREATE TABLE BD1.TIPOS_ENTRADA (
    id_tipo_entrada     NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(100) NOT NULL,
    porcentaje_descuento NUMBER(5,2)  NOT NULL,
    requiere_adulto     NUMBER(1)     DEFAULT 0 NOT NULL,
    es_adulto           NUMBER(1),
    CONSTRAINT PK_TIPOS_ENTRADA PRIMARY KEY (id_tipo_entrada)
);

-- ============================================================
-- 12. COMBOS
-- ============================================================
CREATE TABLE BD1.COMBOS (
    id_combo            NUMBER GENERATED ALWAYS AS IDENTITY,
    nombre              VARCHAR2(150) NOT NULL,
    precio              NUMBER(12,2)  NOT NULL,
    dias_vigencia       NUMBER(5)     NOT NULL,
    estado              VARCHAR2(30)  NOT NULL,
    CONSTRAINT PK_COMBOS PRIMARY KEY (id_combo)
);

-- ============================================================
-- 13. COMBOS_ATRACCIONES
-- ============================================================
CREATE TABLE BD1.COMBOS_ATRACCIONES (
    id_combo            NUMBER        NOT NULL,
    id_atraccion        NUMBER        NOT NULL,
    porcentaje_precio   NUMBER(5,2)   NOT NULL,
    CONSTRAINT PK_COMBOS_ATRACCIONES PRIMARY KEY (id_combo, id_atraccion),
    CONSTRAINT FK_CA_COMBO FOREIGN KEY (id_combo) REFERENCES BD1.COMBOS (id_combo),
    CONSTRAINT FK_CA_ATRACCION FOREIGN KEY (id_atraccion) REFERENCES BD1.ATRACCIONES (id_atraccion)
);

-- ============================================================
-- 14. RESERVAS
-- ============================================================
CREATE TABLE BD1.RESERVAS (
    id_reserva          NUMBER GENERATED ALWAYS AS IDENTITY,
    fecha_hora          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    estado              VARCHAR2(30)  NOT NULL,
    id_perfil           NUMBER        NOT NULL,
    id_plan             NUMBER,
    CONSTRAINT PK_RESERVAS PRIMARY KEY (id_reserva),
    CONSTRAINT FK_RESERVAS_PERFIL FOREIGN KEY (id_perfil) REFERENCES BD1.PERFILES_CLIENTE (id_perfil),
    CONSTRAINT FK_RESERVAS_PLAN FOREIGN KEY (id_plan) REFERENCES BD1.PLANES (id_plan)
);

-- ============================================================
-- 15. BOLETAS
-- ============================================================
CREATE TABLE BD1.BOLETAS (
    id_boleta           NUMBER GENERATED ALWAYS AS IDENTITY,
    codigo              VARCHAR2(100) NOT NULL,
    estado              VARCHAR2(30)  NOT NULL,
    precio_pagado       NUMBER(12,2)  NOT NULL,
    id_reserva          NUMBER        NOT NULL,
    id_zona             NUMBER,
    id_tipo_entrada     NUMBER,
    id_combo            NUMBER,
    CONSTRAINT PK_BOLETAS PRIMARY KEY (id_boleta),
    CONSTRAINT UQ_BOLETA_CODIGO UNIQUE (codigo),
    CONSTRAINT FK_BOLETAS_RESERVA FOREIGN KEY (id_reserva) REFERENCES BD1.RESERVAS (id_reserva),
    CONSTRAINT FK_BOLETAS_ZONA FOREIGN KEY (id_zona) REFERENCES BD1.ZONAS (id_zona),
    CONSTRAINT FK_BOLETAS_TIPO FOREIGN KEY (id_tipo_entrada) REFERENCES BD1.TIPOS_ENTRADA (id_tipo_entrada),
    CONSTRAINT FK_BOLETAS_COMBO FOREIGN KEY (id_combo) REFERENCES BD1.COMBOS (id_combo)
);

-- ============================================================
-- 16. NOTIFICACIONES
-- ============================================================
CREATE TABLE BD1.NOTIFICACIONES (
    id_notificacion     NUMBER GENERATED ALWAYS AS IDENTITY,
    asunto              VARCHAR2(200) NOT NULL,
    mensaje             VARCHAR2(1000) NOT NULL,
    fecha_hora          TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    leida               NUMBER(1)     DEFAULT 0 NOT NULL,
    id_usuario          NUMBER        NOT NULL,
    id_reserva          NUMBER,
    CONSTRAINT PK_NOTIFICACIONES PRIMARY KEY (id_notificacion),
    CONSTRAINT FK_NOTIF_USUARIO FOREIGN KEY (id_usuario) REFERENCES BD1.USUARIOS_SISTEMA (id_usuario),
    CONSTRAINT FK_NOTIF_RESERVA FOREIGN KEY (id_reserva) REFERENCES BD1.RESERVAS (id_reserva)
);

-- ============================================================
-- ÍNDICES PARA OPTIMIZAR BÚSQUEDAS Y CLAVES FORÁNEAS
-- ============================================================
CREATE INDEX BD1.IDX_PRESTADORES_MUNICIPIO   ON BD1.PRESTADORES(id_municipio);
CREATE INDEX BD1.IDX_PRESTADORES_TIPO        ON BD1.PRESTADORES(id_tipo_prestador);
CREATE INDEX BD1.IDX_PLANES_PRESTADOR        ON BD1.PLANES(id_prestador);
CREATE INDEX BD1.IDX_USUARIOS_ROL            ON BD1.USUARIOS_SISTEMA(id_rol);
CREATE INDEX BD1.IDX_ATRACCIONES_PRESTADOR   ON BD1.ATRACCIONES(id_prestador);
CREATE INDEX BD1.IDX_FUNCIONES_ATRACCION     ON BD1.FUNCIONES(id_atraccion);
CREATE INDEX BD1.IDX_FUNCIONES_FECHA         ON BD1.FUNCIONES(fecha);
CREATE INDEX BD1.IDX_ZONAS_FUNCION           ON BD1.ZONAS(id_funcion);
CREATE INDEX BD1.IDX_RESERVAS_PERFIL         ON BD1.RESERVAS(id_perfil);
CREATE INDEX BD1.IDX_RESERVAS_PLAN           ON BD1.RESERVAS(id_plan);
CREATE INDEX BD1.IDX_BOLETAS_RESERVA         ON BD1.BOLETAS(id_reserva);
CREATE INDEX BD1.IDX_BOLETAS_ZONA            ON BD1.BOLETAS(id_zona);
CREATE INDEX BD1.IDX_BOLETAS_TIPO            ON BD1.BOLETAS(id_tipo_entrada);
CREATE INDEX BD1.IDX_BOLETAS_COMBO           ON BD1.BOLETAS(id_combo);
CREATE INDEX BD1.IDX_NOTIFICACIONES_USUARIO  ON BD1.NOTIFICACIONES(id_usuario);
