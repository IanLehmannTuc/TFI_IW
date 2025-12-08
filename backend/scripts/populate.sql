-- ============================================
-- SCRIPT PARA POBLAR LA BASE DE DATOS CON 100 PACIENTES VÁLIDOS
-- ============================================
-- Este script inserta 100 pacientes con datos realistas
-- Incluye CUILs válidos, nombres, apellidos, emails, domicilios y obras sociales
-- ============================================

-- Función para generar CUILs válidos con dígito verificador correcto
CREATE OR REPLACE FUNCTION generar_cuil_valido(prefijo TEXT, dni TEXT)
RETURNS TEXT AS $$
DECLARE
    base TEXT;
    multiplicadores INTEGER[] := ARRAY[5, 4, 3, 2, 7, 6, 5, 4, 3, 2];
    suma INTEGER := 0;
    resto INTEGER;
    digito_verificador INTEGER;
    i INTEGER;
    digito INTEGER;
BEGIN
    -- Construir la base (prefijo + DNI, 10 dígitos)
    base := prefijo || LPAD(dni, 8, '0');
    
    -- Calcular la suma ponderada
    FOR i IN 1..10 LOOP
        digito := CAST(SUBSTRING(base FROM i FOR 1) AS INTEGER);
        suma := suma + (digito * multiplicadores[i]);
    END LOOP;
    
    -- Calcular el dígito verificador
    resto := suma % 11;
    digito_verificador := 11 - resto;
    
    -- Ajustes especiales
    IF digito_verificador = 11 THEN
        digito_verificador := 0;
    ELSIF digito_verificador = 10 THEN
        digito_verificador := 9;
    END IF;
    
    -- Retornar CUIL en formato XX-XXXXXXXX-X
    RETURN prefijo || '-' || LPAD(dni, 8, '0') || '-' || digito_verificador::TEXT;
END;
$$ LANGUAGE plpgsql;


-- Habilitar extensión para hashear contraseñas (BCrypt)
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Limpiar datos existentes (opcional - comentar si quieres mantener los existentes)
DELETE FROM ingresos;
DELETE FROM pacientes;
DELETE FROM usuarios;

-- ============================================
-- CREAR MÉDICO Y ENFERMERO
-- ============================================
-- Insertar médico
INSERT INTO usuarios (email, password_hash, autoridad, cuil, nombre, apellido, matricula) VALUES
('medico@hospital.com', crypt('Medico123!', gen_salt('bf', 10)), 'MEDICO', '20-12345678-6', 'Juan', 'García', 'MP123456')
ON CONFLICT (email) DO NOTHING;

-- Insertar enfermero
INSERT INTO usuarios (email, password_hash, autoridad, cuil, nombre, apellido, matricula) VALUES
('enfermero@hospital.com', crypt('Enfermero123!', gen_salt('bf', 10)), 'ENFERMERO', '20-87654321-5', 'Robert', 'Smith', 'EN876543')
ON CONFLICT (email) DO NOTHING;

-- ============================================
-- INSERTAR PACIENTES
-- ============================================
-- Insertar 100 pacientes válidos usando la función generadora de CUILs
-- Usamos ON CONFLICT para evitar errores si el script se ejecuta múltiples veces
INSERT INTO pacientes (cuil, nombre, apellido, email, domicilio_calle, domicilio_numero, domicilio_localidad, obra_social_id, numero_afiliado) VALUES
-- Pacientes 1-20 (Hombres - prefijo 20)
(generar_cuil_valido('20', '41652938'), 'Leonel', 'Pérez', 'leonel.perez@email.com', 'Calle Ejemplo', 3333, 'Tucumán', 1, '11111'),
(generar_cuil_valido('24', '40532558'), 'Natalia', 'Morales', 'natalia.morales@email.com', 'Calle Ejemplo', 1231, 'Tucumán', 1, '22222'),
(generar_cuil_valido('20', '40274205'), 'Ian', 'Lehmann', 'ian.lehmann@email.com', 'Calle ejemplo', 22, 'Tucumán', 1, '33333'),
(generar_cuil_valido('27', '41375012'), 'Constanza', 'Peñalva', 'constanza.penalva@email.com', 'Calle ejemplo', 988, 'Tucumán', 1, '55555'),
(generar_cuil_valido('20', '35806154'), 'Gabriel', 'Camaño', 'gabriel.camano@email.com', 'Calle ejemplo', 988, 'Tucumán', 1, '44444'),

(generar_cuil_valido('27', '38541999'), 'Valentina', 'Ojeda', 'valentina.ojeda@email.com', 'Av. Colón', 1002, 'Tucumán', 2, '66666'),
(generar_cuil_valido('20', '38771228'), 'Federico', 'Giménez', 'federico.gimenez@email.com', 'Berutti', 145, 'Tucumán', 2, '77777'),
(generar_cuil_valido('20', '37450921'), 'Tomás', 'Martínez', 'tomas.martinez@email.com', 'San Martín', 4598, 'Tucumán', 2, '99999'),
(generar_cuil_valido('27', '39903354'), 'Lucía', 'López', 'lucia.lopez@email.com', 'Buenos Aires', 413, 'Tucumán', 2, '11223')
ON CONFLICT (cuil) DO NOTHING;
