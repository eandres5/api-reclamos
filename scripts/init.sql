-- Crear el usuario
CREATE USER user_reclamos WITH PASSWORD 'useradmin';

-- Otorgar permisos para crear bases de datos
ALTER USER user_reclamos CREATEDB;

-- Crear la base de datos asignada al nuevo usuario
CREATE DATABASE banco_reclamos OWNER user_reclamos;

-- Otorgar todos los privilegios
GRANT ALL PRIVILEGES ON DATABASE banco_reclamos TO user_reclamos;

-- Conectar a la base de datos
\c banco_reclamos;

-- Otorgar permisos en el esquema
GRANT ALL ON SCHEMA public TO user_reclamos;

CREATE USER user_reclamos WITH PASSWORD 'useradmin';
ALTER USER user_reclamos CREATEDB;
CREATE DATABASE banco_reclamos OWNER user_reclamos;
GRANT ALL PRIVILEGES ON DATABASE banco_reclamos TO user_reclamos;
\c banco_reclamos;
GRANT ALL ON SCHEMA public TO user_reclamos;
\q

CREATE TABLE IF NOT EXISTS clientes (
                                        id              BIGSERIAL       PRIMARY KEY,
                                        identificacion  VARCHAR(13)     NOT NULL UNIQUE,
    nombres         VARCHAR(100)    NOT NULL,
    apellidos       VARCHAR(100)    NOT NULL,
    password        VARCHAR(255)    NOT NULL,
    activo          BOOLEAN         NOT NULL DEFAULT TRUE
    );

-- Tabla de reclamos
CREATE TABLE IF NOT EXISTS reclamos (
                                        id              BIGSERIAL       PRIMARY KEY,
                                        cliente_id      BIGINT          NOT NULL REFERENCES clientes(id),
    tipo_reclamo    VARCHAR(30)     NOT NULL,
    detalle_reclamo VARCHAR(500)    NOT NULL,
    fecha_creacion  TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_tipo_reclamo CHECK (
                                          tipo_reclamo IN ('TARJETAS_CREDITO', 'TRANSFERENCIAS', 'PAGO_SERVICIOS')
    )
    );

-- Índices
CREATE INDEX IF NOT EXISTS idx_clientes_identificacion ON clientes(identificacion);
CREATE INDEX IF NOT EXISTS idx_reclamos_cliente_id ON reclamos(cliente_id);


-- CREDENCIALES DE PRUEBA
--
-- | Identificación | Nombres         | Password    |
-- |----------------|-----------------|-------------|
-- | 1712345678     | Juan Carlos     | juan1234    |
-- | 1798765432     | María Elena     | maria1234   |
-- | 0501234567     | Carlos Andrés   | carlos1234  |
-- | 0912345678     | Ana Gabriela    | ana1234     |
-- | 1104567890     | Luis Fernando   | luis1234    |
--
-- RECOMENDACIÓN: Dejar que el DataLoader cargue los datos automáticamente.

-- ══════════════════════════════════════════════════════════════
-- VERIFICACIÓN
-- ══════════════════════════════════════════════════════════════
-- SELECT id, identificacion, nombres, apellidos, activo FROM clientes;
-- SELECT r.*, c.nombres, c.apellidos FROM reclamos r JOIN clientes c ON r.cliente_id = c.id;