-- ============================================
-- SCRIPT PARA CREAR Y POBLAR LA BASE DE DATOS DE OBRAS SOCIALES
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

-- Insertar obras sociales (usando ON CONFLICT para evitar duplicados)
INSERT INTO obras_sociales (id, nombre) VALUES
(1, 'OSDE'),
(2, 'Swiss Medical'),
(3, 'Medifé'),
(4, 'Galeno'),
(5, 'Particular'),
(6, 'Obra Social del Personal de la Industria de la Alimentación'),
(7, 'Obra Social de Empleados de Comercio'),
(8, 'Obra Social de la Unión Personal'),
(9, 'Obra Social del Personal de la Industria Textil'),
(10, 'Obra Social del Personal de la Industria Química')
ON CONFLICT (id) DO NOTHING;

-- Insertar afiliados de ejemplo
INSERT INTO afiliados (numero_afiliado, obra_social_id, nombre_paciente, apellido_paciente) VALUES
-- Afiliados OSDE (id=1)
('OSDE001234', 1, 'Juan', 'García'),
('OSDE002345', 1, 'María', 'Rodríguez'),
('OSDE003456', 1, 'Carlos', 'Fernández'),
('OSDE004567', 1, 'Ana', 'López'),
('OSDE005678', 1, 'Luis', 'Martínez'),

-- Afiliados Swiss Medical (id=2)
('SM001234', 2, 'Patricia', 'González'),
('SM002345', 2, 'Roberto', 'Pérez'),
('SM003456', 2, 'Laura', 'Sánchez'),
('SM004567', 2, 'Miguel', 'Ramírez'),
('SM005678', 2, 'Carmen', 'Torres'),

-- Afiliados Medifé (id=3)
('MED001234', 3, 'Jorge', 'Flores'),
('MED002345', 3, 'Sandra', 'Díaz'),
('MED003456', 3, 'Fernando', 'Morales'),
('MED004567', 3, 'Claudia', 'Ortiz'),
('MED005678', 3, 'Diego', 'Vargas'),

-- Afiliados Galeno (id=4)
('GAL001234', 4, 'Silvia', 'Castro'),
('GAL002345', 4, 'Alejandro', 'Ramos'),
('GAL003456', 4, 'Monica', 'Jiménez'),
('GAL004567', 4, 'Pablo', 'Herrera'),
('GAL005678', 4, 'Verónica', 'Mendoza'),

-- Afiliados Particular (id=5)
('PART001234', 5, 'Ricardo', 'Gutiérrez'),
('PART002345', 5, 'Natalia', 'Silva'),
('PART003456', 5, 'Mario', 'Ruiz'),
('PART004567', 5, 'Gabriela', 'Moreno'),
('PART005678', 5, 'Héctor', 'Alvarez'),

-- Afiliados OSPIA (id=6)
('OSPIA001234', 6, 'Sergio', 'Navarro'),
('OSPIA002345', 6, 'Andrea', 'Molina'),
('OSPIA003456', 6, 'Daniel', 'Campos'),
('OSPIA004567', 6, 'Valeria', 'Vega'),
('OSPIA005678', 6, 'Andrés', 'Guerrero'),

-- Afiliados OSPEC (id=7)
('OSPEC001234', 7, 'Gustavo', 'Rojas'),
('OSPEC002345', 7, 'Florencia', 'Medina'),
('OSPEC003456', 7, 'Eduardo', 'Cruz'),
('OSPEC004567', 7, 'Lucía', 'Aguilar'),
('OSPEC005678', 7, 'Raúl', 'Fuentes'),

-- Afiliados UP (id=8)
('UP001234', 8, 'Víctor', 'Delgado'),
('UP002345', 8, 'Paula', 'Soto'),
('UP003456', 8, 'Carolina', 'Contreras'),
('UP004567', 8, 'Daniela', 'Miranda'),
('UP005678', 8, 'Ricardo', 'Peña'),

-- Afiliados OSIT (id=9)
('OSIT001234', 9, 'Mario', 'Cárdenas'),
('OSIT002345', 9, 'Natalia', 'Valdez'),
('OSIT003456', 9, 'Héctor', 'Escobar'),
('OSIT004567', 9, 'Gabriela', 'Paredes'),
('OSIT005678', 9, 'Sergio', 'Salazar'),

-- Afiliados OSIQUIM (id=10)
('OSIQUIM001234', 10, 'Andrea', 'Villanueva'),
('OSIQUIM002345', 10, 'Daniel', 'Cordero'),
('OSIQUIM003456', 10, 'Valeria', 'Benítez'),
('OSIQUIM004567', 10, 'Andrés', 'Acosta'),
('OSIQUIM005678', 10, 'Gustavo', 'Ponce'),

-- Más afiliados para tener más datos de prueba
('OSDE006789', 1, 'Elena', 'Blanco'),
('OSDE007890', 1, 'Francisco', 'Negro'),
('SM006789', 2, 'Isabel', 'Gris'),
('SM007890', 2, 'Antonio', 'Rojo'),
('MED006789', 3, 'Dolores', 'Azul'),
('MED007890', 3, 'Manuel', 'Verde'),
('GAL006789', 4, 'Rosa', 'Amarillo'),
('GAL007890', 4, 'Javier', 'Naranja'),
('PART006789', 5, 'Teresa', 'Morado'),
('PART007890', 5, 'Alberto', 'Rosa')
ON CONFLICT (numero_afiliado) DO NOTHING;

