#!/bin/bash

BASE_URL="http://localhost:8080"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}REGISTRAR INGRESO A URGENCIAS${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# ============================================
# 1. LOGIN COMO ENFERMERO
# ============================================
echo -e "${YELLOW}[1/2] Obteniendo token de enfermero...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "enfermero@hospital.com",
    "password": "Enfermero123!"
  }')

# Extraer el token de la respuesta
TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}✗ Error al obtener el token${NC}"
    echo "$LOGIN_RESPONSE"
    exit 1
fi

echo -e "${GREEN}✓ Token obtenido exitosamente${NC}"
echo ""

# ============================================
# 2. REGISTRAR INGRESO
# ============================================
echo -e "${YELLOW}[2/2] Registrando ingreso...${NC}"
INGRESO_RESPONSE=$(curl -s -X POST "$BASE_URL/api/urgencias" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "pacienteCuil":"20-38771228-4",
    "pacienteNombre":"Federico",
    "pacienteApellido":"Giménez",
    "pacienteDomicilio":{"calle":"Berutti","numero":145,"localidad":"Tucumán"},
    "pacienteObraSocial":{"obraSocial":{"id":2,"nombre":"Swiss Medical"},"numeroAfiliado":"77777"},
    "enfermeroCuil":"20-87654321-5",
    "descripcion":"Revive",
    "temperatura":36,
    "tensionSistolica":120,
    "tensionDiastolica":80,
    "frecuenciaCardiaca":-80,
    "frecuenciaRespiratoria":-12,
    "nivelEmergencia":"EMERGENCIA"
  }')

if echo "$INGRESO_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓ Ingreso registrado exitosamente${NC}"
    echo "$INGRESO_RESPONSE" | jq '.' 2>/dev/null || echo "$INGRESO_RESPONSE"
else
    echo -e "${RED}✗ Error al registrar ingreso${NC}"
    echo "$INGRESO_RESPONSE"
    exit 1
fi

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}PROCESO COMPLETADO${NC}"
echo -e "${GREEN}========================================${NC}"
