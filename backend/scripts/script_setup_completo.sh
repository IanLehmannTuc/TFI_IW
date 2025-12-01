#!/bin/bash

# ============================================
# SCRIPT DE CONFIGURACIÓN COMPLETA DEL SISTEMA
# ============================================
# Este script realiza las siguientes operaciones:
# 1. Registra un médico
# 2. Registra un enfermero
# 3. Hace login con el enfermero
# 4. Crea 3 pacientes completos
# 5. Crea 1 ingreso con paciente existente
# 6. Crea 1 ingreso con paciente inexistente (se crea automáticamente)
# ============================================

BASE_URL="http://localhost:8080"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Variables para almacenar tokens
ENFERMERO_TOKEN=""

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}SCRIPT DE CONFIGURACIÓN COMPLETA${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# ============================================
# 1. REGISTRAR MÉDICO
# ============================================
echo -e "${YELLOW}[1/6] Registrando médico...${NC}"
MEDICO_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/registro" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "medico@hospital.com",
    "password": "Medico123!",
    "autoridad": "MEDICO",
    "cuil": "20-12345678-6",
    "nombre": "Juan",
    "apellido": "García",
    "matricula": "MP123456"
  }')

if echo "$MEDICO_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓ Médico registrado exitosamente${NC}"
    echo "$MEDICO_RESPONSE" | jq '.' 2>/dev/null || echo "$MEDICO_RESPONSE"
else
    echo -e "${RED}✗ Error al registrar médico${NC}"
    echo "$MEDICO_RESPONSE"
fi
echo ""

# ============================================
# 2. REGISTRAR ENFERMERO
# ============================================
echo -e "${YELLOW}[2/6] Registrando enfermero...${NC}"
ENFERMERO_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/registro" \
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

if echo "$ENFERMERO_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓ Enfermero registrado exitosamente${NC}"
    echo "$ENFERMERO_RESPONSE" | jq '.' 2>/dev/null || echo "$ENFERMERO_RESPONSE"
else
    echo -e "${RED}✗ Error al registrar enfermero${NC}"
    echo "$ENFERMERO_RESPONSE"
fi
echo ""

# ============================================
# 3. LOGIN CON ENFERMERO
# ============================================
echo -e "${YELLOW}[3/6] Haciendo login con enfermero...${NC}"
LOGIN_RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "enfermero@hospital.com",
    "password": "Enfermero123!"
  }')

ENFERMERO_TOKEN=$(echo "$LOGIN_RESPONSE" | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -n "$ENFERMERO_TOKEN" ]; then
    echo -e "${GREEN}✓ Login exitoso${NC}"
    echo "Token obtenido: ${ENFERMERO_TOKEN:0:50}..."
else
    echo -e "${RED}✗ Error en el login${NC}"
    echo "$LOGIN_RESPONSE"
    exit 1
fi
echo ""

# ============================================
# 4. CREAR 3 PACIENTES COMPLETOS
# ============================================

# 4.1. Paciente Mujer: 27-41222333-6
echo -e "${YELLOW}[4.1/6] Creando paciente (Mujer - 27-41222333-6)...${NC}"
PACIENTE1_RESPONSE=$(curl -s -X POST "$BASE_URL/api/pacientes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ENFERMERO_TOKEN" \
  -d '{
    "cuil": "27-41222333-6",
    "nombre": "Ana",
    "apellido": "Martínez",
    "domicilio": {
      "calle": "Av. Corrientes",
      "numero": 1234,
      "localidad": "Buenos Aires"
    },
    "obraSocial": {
      "obraSocial": {
        "id": 1,
        "nombreObraSocial": "OSDE"
      },
      "numeroAfiliado": "12345678"
    }
  }')

if echo "$PACIENTE1_RESPONSE" | grep -q '"cuil":"27-41222333-6"'; then
    echo -e "${GREEN}✓ Paciente 1 creado exitosamente${NC}"
    echo "$PACIENTE1_RESPONSE" | jq '.' 2>/dev/null || echo "$PACIENTE1_RESPONSE"
else
    echo -e "${RED}✗ Error al crear paciente 1${NC}"
    echo "$PACIENTE1_RESPONSE"
fi
echo ""

# 4.2. Paciente Hombre: 20-41652938-9
echo -e "${YELLOW}[4.2/6] Creando paciente (Hombre - 20-41652938-9)...${NC}"
PACIENTE2_RESPONSE=$(curl -s -X POST "$BASE_URL/api/pacientes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ENFERMERO_TOKEN" \
  -d '{
    "cuil": "20-41652938-9",
    "nombre": "Carlos",
    "apellido": "Rodríguez",
    "domicilio": {
      "calle": "Av. Santa Fe",
      "numero": 5678,
      "localidad": "Córdoba"
    },
    "obraSocial": {
      "obraSocial": {
        "id": 2,
        "nombreObraSocial": "Swiss Medical"
      },
      "numeroAfiliado": "87654321"
    }
  }')

if echo "$PACIENTE2_RESPONSE" | grep -q '"cuil":"20-41652938-9"'; then
    echo -e "${GREEN}✓ Paciente 2 creado exitosamente${NC}"
    echo "$PACIENTE2_RESPONSE" | jq '.' 2>/dev/null || echo "$PACIENTE2_RESPONSE"
else
    echo -e "${RED}✗ Error al crear paciente 2${NC}"
    echo "$PACIENTE2_RESPONSE"
fi
echo ""

# 4.3. Paciente Hombre: 20-16670321-3
echo -e "${YELLOW}[4.3/6] Creando paciente (Hombre - 20-16670321-3)...${NC}"
PACIENTE3_RESPONSE=$(curl -s -X POST "$BASE_URL/api/pacientes" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ENFERMERO_TOKEN" \
  -d '{
    "cuil": "20-16670321-3",
    "nombre": "Luis",
    "apellido": "Fernández",
    "domicilio": {
      "calle": "Calle Mitre",
      "numero": 901,
      "localidad": "Rosario"
    },
    "obraSocial": {
      "obraSocial": {
        "id": 3,
        "nombreObraSocial": "IOMA"
      },
      "numeroAfiliado": "11223344"
    }
  }')

if echo "$PACIENTE3_RESPONSE" | grep -q '"cuil":"20-16670321-3"'; then
    echo -e "${GREEN}✓ Paciente 3 creado exitosamente${NC}"
    echo "$PACIENTE3_RESPONSE" | jq '.' 2>/dev/null || echo "$PACIENTE3_RESPONSE"
else
    echo -e "${RED}✗ Error al crear paciente 3${NC}"
    echo "$PACIENTE3_RESPONSE"
fi
echo ""

# ============================================
# 5. CREAR INGRESO CON PACIENTE EXISTENTE
# ============================================
echo -e "${YELLOW}[5/6] Creando ingreso con paciente existente (27-41222333-6)...${NC}"
INGRESO1_RESPONSE=$(curl -s -X POST "$BASE_URL/api/urgencias" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ENFERMERO_TOKEN" \
  -d '{
    "pacienteCuil": "27-41222333-6",
    "enfermeroCuil": "20-87654321-5",
    "descripcion": "Paciente con dolor de pecho y dificultad para respirar. Presión arterial elevada.",
    "temperatura": 38.5,
    "tensionSistolica": 140,
    "tensionDiastolica": 90,
    "frecuenciaCardiaca": 95,
    "frecuenciaRespiratoria": 18,
    "nivelEmergencia": "URGENCIA"
  }')

if echo "$INGRESO1_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓ Ingreso 1 creado exitosamente${NC}"
    echo "$INGRESO1_RESPONSE" | jq '.' 2>/dev/null || echo "$INGRESO1_RESPONSE"
else
    echo -e "${RED}✗ Error al crear ingreso 1${NC}"
    echo "$INGRESO1_RESPONSE"
fi
echo ""

# ============================================
# 6. CREAR INGRESO CON PACIENTE INEXISTENTE
# ============================================
echo -e "${YELLOW}[6/6] Creando ingreso con paciente inexistente (27-20123123-5)...${NC}"
INGRESO2_RESPONSE=$(curl -s -X POST "$BASE_URL/api/urgencias" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $ENFERMERO_TOKEN" \
  -d '{
    "pacienteCuil": "27-20123123-5",
    "pacienteNombre": "María",
    "pacienteApellido": "González",
    "pacienteEmail": "maria.gonzalez@example.com",
    "pacienteDomicilio": {
      "calle": "Av. Libertador",
      "numero": 2500,
      "localidad": "Buenos Aires"
    },
    "pacienteObraSocial": {
      "obraSocial": {
        "id": 1,
        "nombreObraSocial": "OSDE"
      },
      "numeroAfiliado": "99887766"
    },
    "enfermeroCuil": "20-87654321-5",
    "descripcion": "Paciente con fiebre alta y malestar general. Llegó en ambulancia.",
    "temperatura": 39.2,
    "tensionSistolica": 130,
    "tensionDiastolica": 85,
    "frecuenciaCardiaca": 88,
    "frecuenciaRespiratoria": 16,
    "nivelEmergencia": "EMERGENCIA"
  }')

if echo "$INGRESO2_RESPONSE" | grep -q '"id"'; then
    echo -e "${GREEN}✓ Ingreso 2 creado exitosamente (paciente creado automáticamente)${NC}"
    echo "$INGRESO2_RESPONSE" | jq '.' 2>/dev/null || echo "$INGRESO2_RESPONSE"
else
    echo -e "${RED}✗ Error al crear ingreso 2${NC}"
    echo "$INGRESO2_RESPONSE"
fi
echo ""

# ============================================
# RESUMEN
# ============================================
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}RESUMEN DE OPERACIONES${NC}"
echo -e "${BLUE}========================================${NC}"
echo -e "${GREEN}✓ Médico registrado: 20-12345678-6${NC}"
echo -e "${GREEN}✓ Enfermero registrado: 20-87654321-5${NC}"
echo -e "${GREEN}✓ Login realizado exitosamente${NC}"
echo -e "${GREEN}✓ 3 pacientes creados${NC}"
echo -e "${GREEN}✓ 2 ingresos creados${NC}"
echo ""
echo -e "${BLUE}Script completado!${NC}"

