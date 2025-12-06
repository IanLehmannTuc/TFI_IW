#!/bin/bash
# Script para poblar la base de datos con 100 pacientes válidos

echo "Poblando base de datos con 100 pacientes válidos..."

# Verificar si PostgreSQL está corriendo
if ! docker ps | grep -q tfi-postgres; then
    echo "PostgreSQL no está corriendo. Iniciando con Docker Compose..."
    docker-compose up -d
    echo "Esperando 10 segundos para que PostgreSQL se inicie..."
    sleep 10
fi

echo "Ejecutando script de inserción de pacientes..."
docker exec -i tfi-postgres psql -U tfi_user -d tfi < "$(dirname "$0")/populate-pacientes.sql"

if [ $? -eq 0 ]; then
    echo "✓ Base de datos poblada correctamente con 100 pacientes"
    echo ""
    echo "Para verificar, puedes ejecutar:"
    echo "  docker exec -it tfi-postgres psql -U tfi_user -d tfi -c 'SELECT COUNT(*) FROM pacientes;'"
    echo ""
    echo "Para ver algunos pacientes:"
    echo "  docker exec -it tfi-postgres psql -U tfi_user -d tfi -c 'SELECT cuil, nombre, apellido FROM pacientes LIMIT 10;'"
else
    echo "✗ Error al poblar la base de datos"
    exit 1
fi
