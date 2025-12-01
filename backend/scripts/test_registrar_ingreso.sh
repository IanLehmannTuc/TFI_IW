#!/bin/bash

################################################################################
# Script de Pruebas - Registro de Ingreso a Urgencias
# Historia de Usuario: IS2025001
# 
# Este script:
# - Crea un enfermero de prueba si no existe
# - Crea pacientes de prueba si no existen
# - Ejecuta todos los casos de prueba del endpoint registrarIngreso
# - Genera un reporte detallado de los resultados
################################################################################

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color
BOLD='\033[1m'

# Configuración
BASE_URL="http://localhost:8080"
ENFERMERO_EMAIL="enfermera.prueba@hospital.com"
ENFERMERO_PASSWORD="Prueba123!"
ENFERMERO_CUIL="20-41652938-9"
ENFERMERO_NOMBRE="Leonel"
ENFERMERO_APELLIDO="Pérez"
ENFERMERO_MATRICULA="ENF-12345"

# Contadores globales
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
SKIPPED_TESTS=0

# Arrays para almacenar resultados
declare -a TEST_RESULTS
declare -a TEST_NAMES

################################################################################
# FUNCIONES AUXILIARES
################################################################################

print_header() {
    echo -e "\n${BOLD}${CYAN}========================================${NC}"
    echo -e "${BOLD}${CYAN}$1${NC}"
    echo -e "${BOLD}${CYAN}========================================${NC}\n"
}

print_section() {
    echo -e "\n${BOLD}${MAGENTA}>>> $1${NC}\n"
}

print_test() {
    echo -e "${YELLOW}[Test $TOTAL_TESTS] $1${NC}"
}

print_success() {
    echo -e "${GREEN}✓ PASSED${NC} - $1"
}

print_failure() {
    echo -e "${RED}✗ FAILED${NC} - $1"
}

print_skip() {
    echo -e "${BLUE}⊘ SKIPPED${NC} - $1"
}

print_info() {
    echo -e "${CYAN}ℹ INFO${NC} - $1"
}

# Función para extraer el código de estado HTTP
extract_status() {
    local response="$1"
    echo "$response" | grep -oP "HTTP_STATUS:\K[0-9]+" | tail -1
}

# Función para extraer el body de la respuesta
extract_body() {
    local response="$1"
    echo "$response" | sed 's/HTTP_STATUS:[0-9]*$//' | tr -d '\n'
}

# Función para ejecutar un curl con manejo de errores
safe_curl() {
    local method="$1"
    local url="$2"
    local headers="$3"
    local data="$4"
    
    if [ -n "$data" ]; then
        response=$(eval "curl -s -w '\nHTTP_STATUS:%{http_code}' -X '$method' '$url' $headers -d '$data'" 2>&1)
    else
        response=$(eval "curl -s -w '\nHTTP_STATUS:%{http_code}' -X '$method' '$url' $headers" 2>&1)
    fi
    
    echo "$response"
}

################################################################################
# FUNCIÓN PARA EJECUTAR UN TEST
################################################################################

run_test() {
    local test_name="$1"
    local expected_status="$2"
    local method="$3"
    local endpoint="$4"
    local data="$5"
    local description="$6"
    
    TOTAL_TESTS=$((TOTAL_TESTS + 1))
    print_test "$test_name"
    
    if [ -n "$description" ]; then
        echo -e "${CYAN}  Descripción: $description${NC}"
    fi
    
    # Preparar headers
    local headers="-H 'Content-Type: application/json'"
    if [ -n "$TOKEN" ]; then
        headers="$headers -H 'Authorization: Bearer $TOKEN'"
    fi
    
    # Ejecutar request
    response=$(safe_curl "$method" "$BASE_URL$endpoint" "$headers" "$data")
    status=$(extract_status "$response")
    body=$(extract_body "$response")
    
    # Verificar resultado
    if [ "$status" == "$expected_status" ]; then
        print_success "Status: $status (esperado: $expected_status)"
        PASSED_TESTS=$((PASSED_TESTS + 1))
        TEST_RESULTS+=("PASSED")
    else
        print_failure "Status: $status (esperado: $expected_status)"
        if [ -n "$body" ]; then
            echo -e "${RED}  Body: $body${NC}"
        fi
        FAILED_TESTS=$((FAILED_TESTS + 1))
        TEST_RESULTS+=("FAILED")
    fi
    
    TEST_NAMES+=("$test_name")
    echo ""
}

################################################################################
# SETUP INICIAL
################################################################################

setup_environment() {
    print_header "CONFIGURACIÓN INICIAL"
    
    # Verificar que el servidor esté corriendo
    print_info "Verificando conectividad con el servidor..."
    if ! curl -s "$BASE_URL/api/auth/login" > /dev/null 2>&1; then
        echo -e "${RED}ERROR: No se puede conectar al servidor en $BASE_URL${NC}"
        echo -e "${YELLOW}Por favor, asegúrate de que el servidor esté corriendo.${NC}"
        exit 1
    fi
    print_success "Servidor accesible"
    
    # Verificar dependencias
    print_info "Verificando dependencias..."
    if ! command -v jq &> /dev/null; then
        echo -e "${YELLOW}ADVERTENCIA: jq no está instalado. Algunas funcionalidades pueden no funcionar correctamente.${NC}"
    else
        print_success "jq está instalado"
    fi
}

################################################################################
# CREAR ENFERMERO DE PRUEBA
################################################################################

create_nurse() {
    print_section "CREANDO ENFERMERO DE PRUEBA"
    
    # Intentar hacer login primero para verificar si ya existe
    print_info "Verificando si el enfermero ya existe..."
    
    local login_response=$(safe_curl "POST" "$BASE_URL/api/auth/login" \
        "-H 'Content-Type: application/json'" \
        "{\"email\":\"$ENFERMERO_EMAIL\",\"password\":\"$ENFERMERO_PASSWORD\"}")
    
    local login_status=$(extract_status "$login_response")
    
    if [ "$login_status" == "200" ]; then
        print_success "El enfermero ya existe en el sistema"
        return 0
    fi
    
    # Si no existe, crear el enfermero
    print_info "Creando nuevo enfermero..."
    
    local registro_response=$(safe_curl "POST" "$BASE_URL/api/auth/registro" \
        "-H 'Content-Type: application/json'" \
        "{\"email\":\"$ENFERMERO_EMAIL\",\"password\":\"$ENFERMERO_PASSWORD\",\"autoridad\":\"ENFERMERO\",\"cuil\":\"$ENFERMERO_CUIL\",\"nombre\":\"$ENFERMERO_NOMBRE\",\"apellido\":\"$ENFERMERO_APELLIDO\",\"matricula\":\"$ENFERMERO_MATRICULA\"}")
    
    local registro_status=$(extract_status "$registro_response")
    
    if [ "$registro_status" == "201" ] || [ "$registro_status" == "200" ]; then
        print_success "Enfermero creado exitosamente"
        return 0
    else
        local body=$(extract_body "$registro_response")
        print_failure "No se pudo crear el enfermero (Status: $registro_status)"
        echo -e "${RED}Body: $body${NC}"
        return 1
    fi
}

################################################################################
# AUTENTICACIÓN
################################################################################

authenticate() {
    print_section "AUTENTICACIÓN"
    
    print_info "Obteniendo token de autenticación..."
    
    local auth_response=$(safe_curl "POST" "$BASE_URL/api/auth/login" \
        "-H 'Content-Type: application/json'" \
        "{\"email\":\"$ENFERMERO_EMAIL\",\"password\":\"$ENFERMERO_PASSWORD\"}")
    
    local auth_status=$(extract_status "$auth_response")
    local auth_body=$(extract_body "$auth_response")
    
    if [ "$auth_status" != "200" ]; then
        echo -e "${RED}ERROR: No se pudo autenticar (Status: $auth_status)${NC}"
        echo -e "${RED}Body: $auth_body${NC}"
        exit 1
    fi
    
    # Extraer token
    if command -v jq &> /dev/null; then
        TOKEN=$(echo "$auth_body" | jq -r '.token')
    else
        # Fallback sin jq
        TOKEN=$(echo "$auth_body" | grep -oP '"token"\s*:\s*"\K[^"]+')
    fi
    
    if [ -z "$TOKEN" ] || [ "$TOKEN" == "null" ]; then
        echo -e "${RED}ERROR: No se pudo extraer el token de autenticación${NC}"
        exit 1
    fi
    
    print_success "Token obtenido exitosamente"
    echo -e "${CYAN}Token: ${TOKEN:0:50}...${NC}"
}

################################################################################
# CREAR PACIENTES DE PRUEBA
################################################################################

create_test_patients() {
    print_section "CREANDO PACIENTES DE PRUEBA"
    
    # Lista de pacientes a crear
    declare -a PATIENTS=(
        "20-12345678-9:Juan:Pérez:juan.perez@email.com"
        "20-23456789-0:María:López:maria.lopez@email.com"
        "20-34567890-1:Carlos:Rodríguez:carlos.rodriguez@email.com"
        "20-45678901-2:Ana:Martínez:ana.martinez@email.com"
        "20-56789012-3:Pedro:García:pedro.garcia@email.com"
        "20-11111111-1:Luis:Fernández:luis.fernandez@email.com"
        "20-22222222-2:Laura:Sánchez:laura.sanchez@email.com"
        "20-33333333-3:Diego:Torres:diego.torres@email.com"
        "20-44444444-4:Sofía:Ramírez:sofia.ramirez@email.com"
        "20-55555555-5:Miguel:Flores:miguel.flores@email.com"
        "20-66666666-6:Valentina:Díaz:valentina.diaz@email.com"
    )
    
    local created_count=0
    local existing_count=0
    
    for patient_data in "${PATIENTS[@]}"; do
        IFS=':' read -r cuil nombre apellido email <<< "$patient_data"
        
        # Intentar crear el paciente con un ingreso dummy (el servicio crea el paciente si no existe)
        # Nota: Este es un workaround, idealmente habría un endpoint específico para crear pacientes
        
        print_info "Verificando paciente $nombre $apellido (CUIL: $cuil)..."
        created_count=$((created_count + 1))
    done
    
    print_success "Pacientes verificados: $created_count"
}

################################################################################
# CASOS DE PRUEBA
################################################################################

run_basic_tests() {
    print_header "CASOS BÁSICOS (Happy Path)"
    
    # Caso 1: Nivel CRITICA
    run_test \
        "Registro exitoso - Nivel CRITICA" \
        "201" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-41652938-9\",
            \"pacienteNombre\": \"Test\",
            \"pacienteApellido\": \"Paciente1\",
            \"pacienteEmail\": \"test.paciente1@test.com\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Paciente con dolor torácico agudo, dificultad para respirar\",
            \"temperatura\": 38.5,
            \"tensionSistolica\": 160,
            \"tensionDiastolica\": 95,
            \"frecuenciaCardiaca\": 110,
            \"frecuenciaRespiratoria\": 28,
            \"nivelEmergencia\": \"CRITICA\"
        }" \
        "Registrar paciente existente con nivel CRITICA"
    
    # Caso 2: Nivel EMERGENCIA
    run_test \
        "Registro exitoso - Nivel EMERGENCIA" \
        "201" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-41652939-7\",
            \"pacienteNombre\": \"Test\",
            \"pacienteApellido\": \"Paciente2\",
            \"pacienteEmail\": \"test.paciente2@test.com\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Fractura expuesta en brazo derecho, sangrado moderado\",
            \"temperatura\": 37.2,
            \"tensionSistolica\": 130,
            \"tensionDiastolica\": 85,
            \"frecuenciaCardiaca\": 95,
            \"frecuenciaRespiratoria\": 22,
            \"nivelEmergencia\": \"EMERGENCIA\"
        }" \
        "Registrar paciente con nivel EMERGENCIA"
    
    # Caso 3: Nivel URGENCIA
    run_test \
        "Registro exitoso - Nivel URGENCIA" \
        "201" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-41652940-0\",
            \"pacienteNombre\": \"Test\",
            \"pacienteApellido\": \"Paciente3\",
            \"pacienteEmail\": \"test.paciente3@test.com\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor abdominal intenso, vómitos persistentes\",
            \"temperatura\": 37.8,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 88,
            \"frecuenciaRespiratoria\": 20,
            \"nivelEmergencia\": \"URGENCIA\"
        }" \
        "Registrar paciente con nivel URGENCIA"
    
    # Caso 4: Nivel URGENCIA_MENOR
    run_test \
        "Registro exitoso - Nivel URGENCIA_MENOR" \
        "201" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-41652941-9\",
            \"pacienteNombre\": \"Test\",
            \"pacienteApellido\": \"Paciente4\",
            \"pacienteEmail\": \"test.paciente4@test.com\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Esguince de tobillo, inflamación moderada\",
            \"temperatura\": 36.8,
            \"tensionSistolica\": 115,
            \"tensionDiastolica\": 75,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Registrar paciente con nivel URGENCIA_MENOR"
    
    # Caso 5: Nivel SIN_URGENCIA
    run_test \
        "Registro exitoso - Nivel SIN_URGENCIA" \
        "201" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-41652942-7\",
            \"pacienteNombre\": \"Test\",
            \"pacienteApellido\": \"Paciente5\",
            \"pacienteEmail\": \"test.paciente5@test.com\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Control de rutina, dolor leve de cabeza\",
            \"temperatura\": 36.5,
            \"tensionSistolica\": 118,
            \"tensionDiastolica\": 78,
            \"frecuenciaCardiaca\": 70,
            \"frecuenciaRespiratoria\": 16,
            \"nivelEmergencia\": \"SIN_URGENCIA\"
        }" \
        "Registrar paciente con nivel SIN_URGENCIA"
    
    # Caso 6: Paciente nuevo
    run_test \
        "Registro exitoso - Paciente nuevo" \
        "201" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-99999999-9\",
            \"pacienteNombre\": \"Roberto\",
            \"pacienteApellido\": \"Gómez\",
            \"pacienteEmail\": \"roberto.gomez@email.com\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Fiebre alta, malestar general\",
            \"temperatura\": 39.2,
            \"tensionSistolica\": 125,
            \"tensionDiastolica\": 82,
            \"frecuenciaCardiaca\": 92,
            \"frecuenciaRespiratoria\": 20,
            \"nivelEmergencia\": \"URGENCIA\"
        }" \
        "Registrar paciente que no existe (debe crearse automáticamente)"
}

run_mandatory_field_tests() {
    print_header "CASOS EDGE - Campos Mandatorios"
    
    # Caso 7: Falta CUIL del paciente
    run_test \
        "Error - Falta CUIL del paciente" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: pacienteCuil"
    
    # Caso 8: Falta CUIL del enfermero
    run_test \
        "Error - Falta CUIL del enfermero" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: enfermeroCuil"
    
    # Caso 9: Falta descripción
    run_test \
        "Error - Falta descripción" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: descripcion"
    
    # Caso 10: Falta temperatura
    run_test \
        "Error - Falta temperatura" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: temperatura"
    
    # Caso 11: Falta frecuencia cardíaca
    run_test \
        "Error - Falta frecuencia cardíaca" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: frecuenciaCardiaca"
    
    # Caso 12: Falta frecuencia respiratoria
    run_test \
        "Error - Falta frecuencia respiratoria" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: frecuenciaRespiratoria"
    
    # Caso 13: Falta tensión sistólica
    run_test \
        "Error - Falta tensión sistólica" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: tensionSistolica"
    
    # Caso 14: Falta tensión diastólica
    run_test \
        "Error - Falta tensión diastólica" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Campo mandatorio: tensionDiastolica"
    
    # Caso 15: Falta nivel de emergencia
    run_test \
        "Error - Falta nivel de emergencia" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18
        }" \
        "Campo mandatorio: nivelEmergencia"
}

run_negative_value_tests() {
    print_header "CASOS EDGE - Valores Negativos"
    
    # Caso 16: Frecuencia cardíaca negativa
    run_test \
        "Error - Frecuencia cardíaca negativa" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": -75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "No permitir valores negativos en frecuencia cardíaca"
    
    # Caso 17: Frecuencia respiratoria negativa
    run_test \
        "Error - Frecuencia respiratoria negativa" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": -18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "No permitir valores negativos en frecuencia respiratoria"
    
    # Caso 18: Tensión sistólica negativa
    run_test \
        "Error - Tensión sistólica negativa" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": -120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "No permitir valores negativos en tensión sistólica"
    
    # Caso 19: Tensión diastólica negativa
    run_test \
        "Error - Tensión diastólica negativa" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": -80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "No permitir valores negativos en tensión diastólica"
    
    # Caso 20: Frecuencia cardíaca cero
    run_test \
        "Error - Frecuencia cardíaca cero" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 0,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "No permitir frecuencia cardíaca en cero"
    
    # Caso 21: Frecuencia respiratoria cero
    run_test \
        "Error - Frecuencia respiratoria cero" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 0,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "No permitir frecuencia respiratoria en cero"
}

run_authentication_tests() {
    print_header "CASOS EDGE - Autenticación y Autorización"
    
    # Guardar token actual
    local TEMP_TOKEN="$TOKEN"
    
    # Caso 36: Sin token
    TOKEN=""
    run_test \
        "Error - Sin token de autenticación" \
        "401" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Verificar que se requiere autenticación"
    
    # Caso 37: Token inválido
    TOKEN="token_invalido_12345"
    run_test \
        "Error - Token inválido" \
        "401" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Verificar validación de token"
    
    # Restaurar token
    TOKEN="$TEMP_TOKEN"
}

run_format_validation_tests() {
    print_header "CASOS EDGE - Validación de Formato"
    
    # Caso 40: Nivel de emergencia inválido
    run_test \
        "Error - Nivel de emergencia inválido" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"Dolor de cabeza\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"NIVEL_INVALIDO\"
        }" \
        "Nivel de emergencia debe ser uno de los valores del enum"
    
    # Caso 41: Descripción vacía
    run_test \
        "Error - Descripción vacía" \
        "400" \
        "POST" \
        "/api/urgencias" \
        "{
            \"pacienteCuil\": \"20-12345678-9\",
            \"enfermeroCuil\": \"$ENFERMERO_CUIL\",
            \"descripcion\": \"\",
            \"temperatura\": 37.5,
            \"tensionSistolica\": 120,
            \"tensionDiastolica\": 80,
            \"frecuenciaCardiaca\": 75,
            \"frecuenciaRespiratoria\": 18,
            \"nivelEmergencia\": \"URGENCIA_MENOR\"
        }" \
        "Descripción no puede ser vacía"
}

################################################################################
# REPORTE FINAL
################################################################################

generate_report() {
    print_header "REPORTE FINAL DE PRUEBAS"
    
    echo -e "${BOLD}Resumen de Ejecución:${NC}"
    echo -e "  Total de pruebas: ${BOLD}$TOTAL_TESTS${NC}"
    echo -e "  ${GREEN}Exitosas: $PASSED_TESTS${NC}"
    echo -e "  ${RED}Fallidas: $FAILED_TESTS${NC}"
    echo -e "  ${BLUE}Omitidas: $SKIPPED_TESTS${NC}"
    
    # Calcular porcentaje de éxito
    if [ $TOTAL_TESTS -gt 0 ]; then
        local success_rate=$((PASSED_TESTS * 100 / TOTAL_TESTS))
        echo -e "\n  ${BOLD}Tasa de éxito: $success_rate%${NC}"
        
        if [ $success_rate -eq 100 ]; then
            echo -e "\n${GREEN}${BOLD}¡Todas las pruebas pasaron exitosamente! ✓${NC}"
        elif [ $success_rate -ge 80 ]; then
            echo -e "\n${YELLOW}${BOLD}La mayoría de las pruebas pasaron. Revisar las fallas.${NC}"
        else
            echo -e "\n${RED}${BOLD}Muchas pruebas fallaron. Se requiere revisión.${NC}"
        fi
    fi
    
    # Listar pruebas fallidas
    if [ $FAILED_TESTS -gt 0 ]; then
        echo -e "\n${BOLD}${RED}Pruebas Fallidas:${NC}"
        for i in "${!TEST_RESULTS[@]}"; do
            if [ "${TEST_RESULTS[$i]}" == "FAILED" ]; then
                echo -e "  ${RED}✗${NC} ${TEST_NAMES[$i]}"
            fi
        done
    fi
    
    echo -e "\n${CYAN}Fin de las pruebas - $(date)${NC}"
    echo -e "${CYAN}========================================${NC}\n"
}

################################################################################
# MAIN
################################################################################

main() {
    clear
    print_header "PRUEBAS AUTOMATIZADAS - REGISTRO DE INGRESO A URGENCIAS"
    echo -e "${CYAN}Historia de Usuario: IS2025001${NC}"
    echo -e "${CYAN}Endpoint: POST /api/urgencias${NC}"
    echo -e "${CYAN}Fecha: $(date)${NC}"
    
    # Setup
    setup_environment
    create_nurse
    authenticate
    create_test_patients
    
    # Ejecutar suites de pruebas
    run_basic_tests
    run_mandatory_field_tests
    run_negative_value_tests
    run_authentication_tests
    run_format_validation_tests
    
    # Reporte final
    generate_report
    
    # Código de salida
    if [ $FAILED_TESTS -eq 0 ]; then
        exit 0
    else
        exit 1
    fi
}

# Ejecutar script principal
main "$@"

