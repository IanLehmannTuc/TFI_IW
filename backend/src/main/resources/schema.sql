-- ============================================
-- SCRIPT DE CREACIÓN DE TABLAS PARA POSTGRESQL
-- Sistema de Gestión de Urgencias
-- ============================================

-- Habilitar extensión para generar UUIDs automáticamente
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Eliminar tablas si existen (para desarrollo)
DROP TABLE IF EXISTS ingresos CASCADE;
DROP TABLE IF EXISTS pacientes CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;
DROP TABLE IF EXISTS obras_sociales CASCADE;

-- ============================================
-- TABLA: usuarios (Personal médico: médicos y enfermeros)
-- ============================================
CREATE TABLE IF NOT EXISTS usuarios (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    autoridad VARCHAR(50) NOT NULL CHECK (autoridad IN ('MEDICO', 'ENFERMERO')),
    cuil VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    matricula VARCHAR(50) NOT NULL UNIQUE
);

-- ============================================
-- TABLA: pacientes
-- ============================================
CREATE TABLE IF NOT EXISTS pacientes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cuil VARCHAR(15) NOT NULL UNIQUE,
    nombre VARCHAR(255),
    apellido VARCHAR(255),
    email VARCHAR(255),
    fecha_nacimiento DATE,
    telefono VARCHAR(50),
    -- Domicilio
    domicilio_calle VARCHAR(255),
    domicilio_numero INTEGER,
    domicilio_localidad VARCHAR(255),
    -- Obra Social
    obra_social_id INTEGER,
    numero_afiliado VARCHAR(100)
);

-- ============================================
-- TABLA: ingresos
-- ============================================
CREATE TABLE IF NOT EXISTS ingresos (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    paciente_id UUID NOT NULL REFERENCES pacientes(id),
    enfermero_id UUID NOT NULL REFERENCES usuarios(id),
    doctor_id UUID REFERENCES usuarios(id),
    descripcion TEXT,
    informe_doctor TEXT,
    fecha_hora_ingreso TIMESTAMP NOT NULL,
    -- Signos vitales
    temperatura DOUBLE PRECISION,
    presion_sistolica INTEGER,
    presion_diastolica INTEGER,
    frecuencia_cardiaca INTEGER,
    frecuencia_respiratoria INTEGER,
    -- Estado y prioridad
    nivel_emergencia VARCHAR(50) NOT NULL CHECK (nivel_emergencia IN ('CRITICA', 'EMERGENCIA', 'URGENCIA', 'URGENCIA_MENOR', 'SIN_URGENCIA')),
    estado VARCHAR(50) NOT NULL CHECK (estado IN ('PENDIENTE', 'EN_PROCESO', 'FINALIZADO'))
);

-- ============================================
-- ÍNDICES para mejorar el rendimiento
-- ============================================
CREATE INDEX IF NOT EXISTS idx_ingresos_paciente ON ingresos(paciente_id);
CREATE INDEX IF NOT EXISTS idx_ingresos_enfermero ON ingresos(enfermero_id);
CREATE INDEX IF NOT EXISTS idx_ingresos_doctor ON ingresos(doctor_id);
CREATE INDEX IF NOT EXISTS idx_ingresos_fecha ON ingresos(fecha_hora_ingreso);
CREATE INDEX IF NOT EXISTS idx_ingresos_estado ON ingresos(estado);
CREATE INDEX IF NOT EXISTS idx_ingresos_nivel ON ingresos(nivel_emergencia);
CREATE INDEX IF NOT EXISTS idx_pacientes_obra_social ON pacientes(obra_social_id);
CREATE INDEX IF NOT EXISTS idx_pacientes_cuil ON pacientes(cuil);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_cuil ON usuarios(cuil);
CREATE INDEX IF NOT EXISTS idx_usuarios_matricula ON usuarios(matricula);
CREATE INDEX IF NOT EXISTS idx_usuarios_autoridad ON usuarios(autoridad);

-- ============================================
-- DATOS INICIALES (OPCIONAL)
-- ============================================
