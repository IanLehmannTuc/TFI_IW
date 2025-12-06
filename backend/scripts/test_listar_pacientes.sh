#!/bin/bash
# Script para probar el endpoint de listado de pacientes con paginación

BASE_URL="http://localhost:8080"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}PRUEBA DE ENDPOINT: LISTAR PACIENTES${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# ============================================
# 1. LOGIN PARA OBTENER TOKEN
# ============================================
echo -e "${YELLOW}[1/3] Obteniendo token de autenticación...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "enfermero@hospital.com",
    "password": "Enfermero123!"
  }')

TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
    echo -e "${RED}✗ Error: No se pudo obtener el token${NC}"
    echo "Respuesta del login:"
    echo "$LOGIN_RESPONSE"
    echo ""
    echo "Intentando registrar el enfermero primero..."
    
    # Intentar registrar el enfermero si no existe
    REGISTER_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/registro" \
      -H "Content-Type: application/json" \
      -d '{
        "email": "enfermero@hospital.com",
        "password": "Enfermero123!",
        "autoridad": "ENFERMERO",
        "cuil": "20-87654321-5",
        "nombre": "Leonel",
        "apellido": "Pérez",
        "matricula": "EN876543"
      }')
    
    # Intentar login nuevamente
    LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
      -H "Content-Type: application/json" \
      -d '{
        "email": "enfermero@hospital.com",
        "password": "Enfermero123!"
      }')
    
    TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
    
    if [ -z "$TOKEN" ]; then
        echo -e "${RED}✗ Error: No se pudo obtener el token después del registro${NC}"
        exit 1
    fi
fi

echo -e "${GREEN}✓ Token obtenido exitosamente${NC}"
echo ""

# ============================================
# 2. LISTAR PACIENTES - Página 0, tamaño 10
# ============================================
echo -e "${YELLOW}[2/3] Probando listado de pacientes (página 0, tamaño 10)...${NC}"
RESPONSE=$(curl -s -X GET "$BASE_URL/api/pacientes?page=0&size=10&sortBy=cuil&direction=ASC" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

if echo "$RESPONSE" | grep -q '"content"'; then
    echo -e "${GREEN}✓ Respuesta exitosa${NC}"
    echo ""
    echo "Respuesta (formateada):"
    echo "$RESPONSE" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE"
else
    echo -e "${RED}✗ Error en la respuesta${NC}"
    echo "Respuesta:"
    echo "$RESPONSE"
fi
echo ""

# ============================================
# 3. LISTAR PACIENTES - Página 1, tamaño 5, ordenado por nombre
# ============================================
echo -e "${YELLOW}[3/3] Probando listado de pacientes (página 1, tamaño 5, ordenado por nombre)...${NC}"
RESPONSE2=$(curl -s -X GET "$BASE_URL/api/pacientes?page=1&size=5&sortBy=nombre&direction=ASC" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json")

if echo "$RESPONSE2" | grep -q '"content"'; then
    echo -e "${GREEN}✓ Respuesta exitosa${NC}"
    echo ""
    echo "Respuesta (formateada):"
    echo "$RESPONSE2" | python3 -m json.tool 2>/dev/null || echo "$RESPONSE2"
else
    echo -e "${RED}✗ Error en la respuesta${NC}"
    echo "Respuesta:"
    echo "$RESPONSE2"
fi
echo ""

echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}Pruebas completadas${NC}"
echo -e "${BLUE}========================================${NC}"
