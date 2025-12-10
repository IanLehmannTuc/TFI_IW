#!/bin/bash
# Script para inicializar y poblar la base de datos de obras sociales
# Usa la instancia de PostgreSQL existente en Docker (tfi-postgres)

set -e

echo "=========================================="
echo "Inicializando base de datos de Obras Sociales"
echo "=========================================="

# Variables de configuración del contenedor Docker existente
DOCKER_CONTAINER="${DOCKER_CONTAINER:-tfi-postgres}"
DB_NAME="${DB_NAME:-obras_sociales_db}"
DB_USER="${DB_USER:-tfi_user}"
DB_PASSWORD="${DB_PASSWORD:-tfi_password}"

# Directorio del script
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

echo ""
echo "Configuración:"
echo "  Contenedor Docker: $DOCKER_CONTAINER"
echo "  Base de datos: $DB_NAME"
echo "  Usuario: $DB_USER"
echo ""

# Verificar si el contenedor está corriendo
echo "Verificando que el contenedor Docker esté corriendo..."
if ! docker ps | grep -q "$DOCKER_CONTAINER"; then
    echo "❌ Error: El contenedor '$DOCKER_CONTAINER' no está corriendo"
    echo "   Inicia el contenedor con: docker start $DOCKER_CONTAINER"
    echo "   O desde el directorio backend: docker-compose up -d"
    exit 1
fi
echo "✓ Contenedor Docker encontrado"
echo ""

# Verificar conexión a PostgreSQL
echo "Verificando conexión a PostgreSQL..."
if ! docker exec "$DOCKER_CONTAINER" psql -U "$DB_USER" -d postgres -c "SELECT 1;" > /dev/null 2>&1; then
    echo "❌ Error: No se puede conectar a PostgreSQL en el contenedor"
    exit 1
fi
echo "✓ Conexión a PostgreSQL exitosa"
echo ""

# Crear la base de datos si no existe
echo "Creando base de datos si no existe..."
docker exec -i "$DOCKER_CONTAINER" psql -U "$DB_USER" -d postgres <<EOF
SELECT 'CREATE DATABASE $DB_NAME'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '$DB_NAME')\gexec
EOF

if [ $? -eq 0 ]; then
    echo "✓ Base de datos verificada/creada"
else
    echo "❌ Error al crear la base de datos"
    exit 1
fi
echo ""

# Ejecutar script de creación de tablas
echo "Creando tablas..."
if docker exec -i "$DOCKER_CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < "$SCRIPT_DIR/create-db.sql"; then
    echo "✓ Tablas creadas correctamente"
else
    echo "❌ Error al crear las tablas"
    exit 1
fi
echo ""

# Ejecutar script de población de datos
echo "Poblando base de datos con datos de ejemplo..."
if docker exec -i "$DOCKER_CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" < "$SCRIPT_DIR/populate-db.sql"; then
    echo "✓ Base de datos poblada correctamente"
else
    echo "❌ Error al poblar la base de datos"
    exit 1
fi
echo ""

# Mostrar resumen
echo "=========================================="
echo "Resumen de la base de datos:"
echo "=========================================="
docker exec -i "$DOCKER_CONTAINER" psql -U "$DB_USER" -d "$DB_NAME" <<EOF
SELECT 
    'Obras Sociales' as tipo,
    COUNT(*) as cantidad
FROM obras_sociales
UNION ALL
SELECT 
    'Afiliados' as tipo,
    COUNT(*) as cantidad
FROM afiliados;
EOF

echo ""
echo "=========================================="
echo "✓ Base de datos inicializada correctamente"
echo "=========================================="
echo ""
echo "Puedes conectarte a la base de datos con:"
echo "  docker exec -it $DOCKER_CONTAINER psql -U $DB_USER -d $DB_NAME"
echo ""
echo "O desde fuera del contenedor:"
echo "  psql -h localhost -p 5432 -U $DB_USER -d $DB_NAME"
echo ""
echo "Para iniciar la API, ejecuta:"
echo "  cd $PROJECT_DIR"
echo "  python main.py"
echo "  o"
echo "  uvicorn main:app --reload"
echo ""

