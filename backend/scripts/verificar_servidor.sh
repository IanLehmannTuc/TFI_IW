#!/bin/bash

################################################################################
# Script auxiliar - Verificar estado del servidor
################################################################################

BASE_URL="http://localhost:8080"

echo "=========================================="
echo "Verificando estado del servidor..."
echo "=========================================="
echo ""

# Verificar conectividad básica
echo -n "🔍 Conectividad: "
if curl -s "$BASE_URL/api/auth/login" > /dev/null 2>&1; then
    echo "✓ OK"
else
    echo "✗ FALLO - El servidor no está respondiendo en $BASE_URL"
    echo ""
    echo "💡 Solución: Inicia el servidor con:"
    echo "   ./mvnw spring-boot:run"
    exit 1
fi

# Verificar endpoint de autenticación
echo -n "🔐 Endpoint de autenticación: "
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE_URL/api/auth/login" \
    -H "Content-Type: application/json" \
    -d '{"cuil": "test", "password": "test"}')

if [ "$HTTP_CODE" == "401" ] || [ "$HTTP_CODE" == "400" ]; then
    echo "✓ OK (responde correctamente)"
elif [ "$HTTP_CODE" == "200" ]; then
    echo "✓ OK (autenticación exitosa)"
else
    echo "✗ Estado inesperado: $HTTP_CODE"
fi

# Verificar endpoint de urgencias
echo -n "🚑 Endpoint de urgencias: "
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X GET "$BASE_URL/api/urgencias")

if [ "$HTTP_CODE" == "401" ]; then
    echo "✓ OK (requiere autenticación correctamente)"
elif [ "$HTTP_CODE" == "200" ]; then
    echo "✓ OK (accesible)"
else
    echo "⚠️  Estado: $HTTP_CODE"
fi

echo ""
echo "=========================================="
echo "✅ El servidor está listo para las pruebas"
echo "=========================================="
echo ""
echo "Ejecuta las pruebas con:"
echo "  ./test_registrar_ingreso.sh"
echo ""

