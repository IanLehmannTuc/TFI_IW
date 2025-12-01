#!/bin/bash
# Script para inicializar la base de datos PostgreSQL

echo "Inicializando base de datos PostgreSQL para TFI..."

# Verificar si PostgreSQL está corriendo
if ! docker ps | grep -q tfi-postgres; then
    echo "PostgreSQL no está corriendo. Iniciando con Docker Compose..."
    docker-compose up -d
    echo "Esperando 10 segundos para que PostgreSQL se inicie..."
    sleep 10
fi

echo "Ejecutando script de creación de tablas..."
docker exec -i tfi-postgres psql -U tfi_user -d tfi < src/main/resources/schema.sql

if [ $? -eq 0 ]; then
    echo "✓ Base de datos inicializada correctamente"
    echo ""
    echo "Puedes conectarte a PostgreSQL con:"
    echo "  docker exec -it tfi-postgres psql -U tfi_user -d tfi"
    echo ""
    echo "Para ver las tablas creadas, ejecuta:"
    echo "  \\dt"
else
    echo "✗ Error al inicializar la base de datos"
    exit 1
fi

