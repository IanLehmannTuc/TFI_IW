-- ============================================
-- MIGRACIÓN: Agregar columna atencion_id a ingresos
-- ============================================
-- Solo ejecutar si la tabla ingresos ya existe con datos

-- 1. Agregar columna atencion_id (nullable)
ALTER TABLE ingresos 
ADD COLUMN IF NOT EXISTS atencion_id UUID;

-- 2. Agregar FK hacia atenciones
ALTER TABLE ingresos
ADD CONSTRAINT fk_ingresos_atencion 
FOREIGN KEY (atencion_id) REFERENCES atenciones(id)
ON DELETE SET NULL;

-- 3. Crear índice para mejorar performance
CREATE INDEX IF NOT EXISTS idx_ingresos_atencion ON ingresos(atencion_id);

-- 4. Sincronizar datos existentes (si hay atenciones ya registradas)
UPDATE ingresos i
SET atencion_id = a.id
FROM atenciones a
WHERE a.ingreso_id = i.id
  AND i.atencion_id IS NULL;

-- 5. Verificar que se agregó correctamente
SELECT 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns
WHERE table_name = 'ingresos' 
  AND column_name = 'atencion_id';

-- Resultado esperado:
-- column_name  | data_type | is_nullable | column_default
-- -------------+-----------+-------------+----------------
-- atencion_id  | uuid      | YES         | 

-- Verificar constraint
SELECT conname, contype 
FROM pg_constraint 
WHERE conrelid = 'ingresos'::regclass 
  AND conname = 'fk_ingresos_atencion';

-- Resultado esperado:
-- conname              | contype
-- ---------------------+---------
-- fk_ingresos_atencion | f

