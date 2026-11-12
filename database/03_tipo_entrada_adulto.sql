-- Ejecutar solo si el esquema existente todavía no tiene esta columna.
ALTER TABLE BD1.TIPOS_ENTRADA ADD (es_adulto NUMBER(1));
