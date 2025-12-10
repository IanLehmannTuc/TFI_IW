-- ============================================
-- MIGRACIÓN: Agregar columna sexo a pacientes
-- ============================================
-- Solo ejecutar si la tabla pacientes ya existe con datos
-- Si la tabla está vacía, es mejor recrearla con schema.sql

-- Agregar columna sexo
ALTER TABLE pacientes 
ADD COLUMN IF NOT EXISTS sexo CHAR(1) CHECK (sexo IN ('M', 'F'));

-- Verificar que se agregó correctamente
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_name = 'pacientes' 
  AND column_name = 'sexo';

-- Resultado esperado:
-- column_name | data_type | character_maximum_length | is_nullable
-- ------------+-----------+--------------------------+-------------
-- sexo        | character | 1                        | YES

