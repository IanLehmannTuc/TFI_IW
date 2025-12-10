-- ============================================
-- SCRIPT PARA CREAR LA BASE DE DATOS DE OBRAS SOCIALES
-- ============================================
-- Este script crea las tablas necesarias para la API de obras sociales
-- ============================================

-- Crear tabla de obras sociales
CREATE TABLE IF NOT EXISTS obras_sociales (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Crear tabla de afiliados
CREATE TABLE IF NOT EXISTS afiliados (
    id SERIAL PRIMARY KEY,
    numero_afiliado VARCHAR(50) NOT NULL UNIQUE,
    obra_social_id INTEGER NOT NULL,
    nombre_paciente VARCHAR(255),
    apellido_paciente VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (obra_social_id) REFERENCES obras_sociales(id) ON DELETE CASCADE
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_afiliados_numero_afiliado ON afiliados(numero_afiliado);
CREATE INDEX IF NOT EXISTS idx_afiliados_obra_social_id ON afiliados(obra_social_id);

-- Comentarios en las tablas
COMMENT ON TABLE obras_sociales IS 'Tabla que almacena las obras sociales disponibles';
COMMENT ON TABLE afiliados IS 'Tabla que almacena los afiliados a las obras sociales';
COMMENT ON COLUMN afiliados.numero_afiliado IS 'Número único de afiliado del paciente';

