# API REST - TFI Ing Software

Documentación completa de la API REST para el sistema de gestión hospitalaria.

## Tabla de Contenidos

- [Información General](#información-general)
- [Base URL](#base-url)
- [Autenticación](#autenticación)
- [Endpoints de Autenticación](#endpoints-de-autenticación)
- [Endpoints de Pacientes](#endpoints-de-pacientes)
- [Formatos de Respuesta](#formatos-de-respuesta)
- [Códigos de Estado HTTP](#códigos-de-estado-http)
- [Validaciones](#validaciones)
- [Ejemplos de Uso](#ejemplos-de-uso)

---

## Información General

Esta API REST proporciona funcionalidades para:
- **Autenticación**: Registro y login de usuarios con JWT
- **Gestión de Pacientes**: Creación y consulta de pacientes

La API utiliza **JSON Web Tokens (JWT)** para la autenticación. Todos los endpoints protegidos requieren un token válido en el header `Authorization`.

---

## Base URL

```
http://localhost:8080
```

---

## Autenticación

La API utiliza autenticación basada en **JWT (JSON Web Tokens)**.

### Cómo obtener un token

1. **Registrar un nuevo usuario** mediante `POST /api/auth/registro`
2. **Iniciar sesión** mediante `POST /api/auth/login`

Ambos endpoints retornan un token JWT que debe incluirse en las peticiones subsiguientes.

### Uso del token

Incluir el token en el header `Authorization` de todas las peticiones protegidas:

```
Authorization: Bearer <tu-token-jwt>
```

### Expiración del token

Por defecto, los tokens expiran después de **24 horas** (86400000 ms). Este valor puede configurarse en `application.properties`.

---

## Endpoints de Autenticación

### 1. Registrar Usuario

Registra un nuevo usuario en el sistema.

**Endpoint:** `POST /api/auth/registro`

**Autenticación:** No requerida (endpoint público)

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "Password123!",
  "autoridad": "MEDICO"
}
```

**Campos:**
- `email` (string, requerido): Email válido del usuario
- `password` (string, requerido): Contraseña (mínimo 8 caracteres, debe contener mayúsculas, minúsculas, números y caracteres especiales)
- `autoridad` (string, requerido): Rol del usuario. Valores posibles: `"MEDICO"` o `"ENFERMERA"`

**Response 201 Created:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "usuario@example.com",
  "autoridad": "MEDICO",
  "expiresIn": 86400000
}
```

**Errores posibles:**
- `400 Bad Request`: Email inválido, contraseña no cumple requisitos, email ya existe, autoridad inválida
- `500 Internal Server Error`: Error interno del servidor

---

### 2. Iniciar Sesión

Autentica un usuario existente y retorna un token JWT.

**Endpoint:** `POST /api/auth/login`

**Autenticación:** No requerida (endpoint público)

**Request Body:**
```json
{
  "email": "usuario@example.com",
  "password": "Password123!"
}
```

**Campos:**
- `email` (string, requerido): Email del usuario
- `password` (string, requerido): Contraseña del usuario

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "email": "usuario@example.com",
  "autoridad": "MEDICO",
  "expiresIn": 86400000
}
```

**Errores posibles:**
- `400 Bad Request`: Campos faltantes o inválidos
- `401 Unauthorized`: Usuario o contraseña inválidos
- `500 Internal Server Error`: Error interno del servidor

---

### 3. Obtener Perfil

Obtiene la información del usuario autenticado.

**Endpoint:** `GET /api/auth/perfil`

**Autenticación:** Requerida (JWT)

**Headers:**
```
Authorization: Bearer <token>
```

**Response 200 OK:**
```json
{
  "email": "usuario@example.com",
  "autoridad": "MEDICO"
}
```

**Errores posibles:**
- `401 Unauthorized`: Token faltante, inválido o expirado

---

### 4. Verificar Autenticación

Endpoint de prueba para verificar que el token JWT es válido.

**Endpoint:** `GET /api/auth/verificar`

**Autenticación:** Requerida (JWT)

**Headers:**
```
Authorization: Bearer <token>
```

**Response 200 OK:**
```
Token válido. Usuario autenticado: usuario@example.com con autoridad: MEDICO
```

**Errores posibles:**
- `401 Unauthorized`: Token faltante, inválido o expirado

---

## Endpoints de Pacientes

### 1. Crear Paciente

Crea un nuevo paciente en el sistema.

**Endpoint:** `POST /api/pacientes`

**Autenticación:** Requerida (JWT)

**Autoridad requerida:** `ENFERMERA` (solo enfermeras pueden crear pacientes)

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "cuil": "20-20304050-5",
  "nombre": "Juan",
  "apellido": "Pérez",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "obraSocial": {
    "obraSocial": {
      "idObraSocial": 1,
      "nombreObraSocial": "OSDE"
    },
    "numeroAfiliado": "12345678"
  }
}
```

**Campos:**
- `cuil` (string, requerido): CUIL válido en formato `XX-XXXXXXXX-X`
- `nombre` (string, requerido): Nombre del paciente
- `apellido` (string, requerido): Apellido del paciente
- `domicilio` (object, requerido): Domicilio del paciente
  - `calle` (string, requerido): Nombre de la calle
  - `numero` (integer, requerido): Número de la dirección
  - `localidad` (string, requerido): Localidad/ciudad
- `obraSocial` (object, opcional): Información de afiliación a obra social
  - `obraSocial` (object, requerido si se especifica afiliación)
    - `idObraSocial` (integer, requerido): ID de la obra social
    - `nombreObraSocial` (string, opcional): Nombre de la obra social
  - `numeroAfiliado` (string, requerido si se especifica afiliación): Número de afiliado

**Ejemplo sin obra social:**
```json
{
  "cuil": "20-27272727-9",
  "nombre": "María",
  "apellido": "González",
  "domicilio": {
    "calle": "Av. Santa Fe",
    "numero": 5678,
    "localidad": "Córdoba"
  },
  "obraSocial": null
}
```

**Response 201 Created:**
```json
{
  "cuil": "20-20304050-5",
  "nombre": "Juan",
  "apellido": "Pérez",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "obraSocial": {
    "obraSocial": {
      "idObraSocial": 1,
      "nombreObraSocial": "OSDE"
    },
    "numeroAfiliado": "12345678"
  }
}
```

**Errores posibles:**
- `400 Bad Request`: 
  - Datos inválidos (CUIL inválido, campos faltantes)
  - CUIL ya existe en el sistema
  - Validaciones de formato fallidas
- `401 Unauthorized`: Token faltante, inválido o expirado
- `403 Forbidden`: Usuario no tiene autoridad `ENFERMERA`
- `500 Internal Server Error`: Error interno del servidor

---

### 2. Buscar Paciente por CUIL

Busca un paciente por su CUIL.

**Endpoint:** `GET /api/pacientes/{cuil}`

**Autenticación:** Requerida (JWT)

**Autoridad requerida:** Cualquier usuario autenticado (`MEDICO` o `ENFERMERA`)

**Headers:**
```
Authorization: Bearer <token>
```

**Path Parameters:**
- `cuil` (string, requerido): CUIL del paciente en formato `XX-XXXXXXXX-X`

**Response 200 OK:**
```json
{
  "cuil": "20-20304050-5",
  "nombre": "Juan",
  "apellido": "Pérez",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "obraSocial": {
    "obraSocial": {
      "idObraSocial": 1,
      "nombreObraSocial": "OSDE"
    },
    "numeroAfiliado": "12345678"
  }
}
```

**Response 200 OK (sin obra social):**
```json
{
  "cuil": "20-27272727-9",
  "nombre": "María",
  "apellido": "González",
  "domicilio": {
    "calle": "Av. Santa Fe",
    "numero": 5678,
    "localidad": "Córdoba"
  },
  "obraSocial": null
}
```

**Errores posibles:**
- `401 Unauthorized`: Token faltante, inválido o expirado
- `404 Not Found`: Paciente no encontrado con el CUIL especificado
- `500 Internal Server Error`: Error interno del servidor

---

## Formatos de Respuesta

### Respuesta de Error

Todas las respuestas de error siguen el siguiente formato:

```json
{
  "mensaje": "Descripción del error",
  "timestamp": "2025-11-14T04:08:25.215271095",
  "status": 400
}
```

**Campos:**
- `mensaje` (string): Descripción del error
- `timestamp` (string): Fecha y hora del error en formato ISO 8601
- `status` (integer): Código de estado HTTP

---

## Códigos de Estado HTTP

| Código | Significado | Descripción |
|--------|-------------|-------------|
| 200 | OK | Petición exitosa |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Datos inválidos o validaciones fallidas |
| 401 | Unauthorized | No autenticado o token inválido/expirado |
| 403 | Forbidden | Autenticado pero sin permisos suficientes |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error interno del servidor |

---

## Validaciones

### Email

- Debe tener un formato válido de email
- Ejemplos válidos: `usuario@example.com`, `medico@hospital.com`
- Ejemplos inválidos: `usuario`, `@example.com`, `usuario@`

### Contraseña

La contraseña debe cumplir los siguientes requisitos:
- Mínimo 8 caracteres
- Al menos una letra mayúscula
- Al menos una letra minúscula
- Al menos un número
- Al menos un carácter especial

Ejemplos válidos: `Password123!`, `MiPass2024#`, `Secure@Pass1`
Ejemplos inválidos: `password`, `12345678`, `PASSWORD123!`

### CUIL

El CUIL debe cumplir con el formato y validaciones oficiales:

**Formato:** `XX-XXXXXXXX-X`
- 2 dígitos (prefijo)
- Guion
- 8 dígitos (DNI)
- Guion
- 1 dígito (dígito verificador)

**Prefijos válidos:** `20`, `23`, `24`, `27`, `30`, `33`, `34`

**Validación:**
- Se valida el formato con expresión regular
- Se valida que el prefijo sea uno de los permitidos
- Se valida el dígito verificador usando el algoritmo oficial

**Ejemplos válidos:**
- `20-20304050-5`
- `23-27272727-9`
- `27-12345678-3`

**Ejemplos inválidos:**
- `20-12345678-9` (dígito verificador incorrecto)
- `99-12345678-1` (prefijo inválido)
- `20-1234567-8` (formato incorrecto)

### Autoridad

Valores permitidos:
- `"MEDICO"`: Personal médico
- `"ENFERMERA"`: Personal de enfermería

---

## Ejemplos de Uso

### Ejemplo 1: Flujo completo de registro y creación de paciente

```bash
# 1. Registrar una enfermera
curl -X POST http://localhost:8080/api/auth/registro \
  -H "Content-Type: application/json" \
  -d '{
    "email": "enfermera@hospital.com",
    "password": "Password123!",
    "autoridad": "ENFERMERA"
  }'

# Respuesta:
# {
#   "token": "eyJhbGciOiJIUzUxMiJ9...",
#   "email": "enfermera@hospital.com",
#   "autoridad": "ENFERMERA",
#   "expiresIn": 86400000
# }

# 2. Crear un paciente (usando el token obtenido)
curl -X POST http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "cuil": "20-20304050-5",
    "nombre": "Juan",
    "apellido": "Pérez",
    "domicilio": {
      "calle": "Av. Corrientes",
      "numero": 1234,
      "localidad": "Buenos Aires"
    },
    "obraSocial": {
      "obraSocial": {
        "idObraSocial": 1,
        "nombreObraSocial": "OSDE"
      },
      "numeroAfiliado": "12345678"
    }
  }'

# 3. Buscar el paciente creado
curl -X GET http://localhost:8080/api/pacientes/20-20304050-5 \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Ejemplo 2: Login y consulta de perfil

```bash
# 1. Iniciar sesión
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "medico@hospital.com",
    "password": "Password123!"
  }'

# 2. Obtener perfil del usuario autenticado
curl -X GET http://localhost:8080/api/auth/perfil \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

# 3. Verificar autenticación
curl -X GET http://localhost:8080/api/auth/verificar \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### Ejemplo 3: Manejo de errores

```bash
# Intentar crear paciente sin autenticación
curl -X POST http://localhost:8080/api/pacientes \
  -H "Content-Type: application/json" \
  -d '{
    "cuil": "20-20304050-5",
    "nombre": "Juan",
    "apellido": "Pérez",
    "domicilio": {
      "calle": "Av. Corrientes",
      "numero": 1234,
      "localidad": "Buenos Aires"
    }
  }'

# Respuesta: 401 Unauthorized
# {
#   "mensaje": "No autenticado. Token JWT requerido.",
#   "timestamp": "2025-11-14T04:08:37.93298288",
#   "status": 401
# }

# Intentar crear paciente como MEDICO (sin permisos)
curl -X POST http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer <token-de-medico>" \
  -H "Content-Type: application/json" \
  -d '{
    "cuil": "20-11111111-2",
    "nombre": "Test",
    "apellido": "Test",
    "domicilio": {
      "calle": "Test",
      "numero": 1,
      "localidad": "Test"
    }
  }'

# Respuesta: 403 Forbidden
# {
#   "mensaje": "No tiene permisos para esta operación. Se requiere: ENFERMERA",
#   "timestamp": "2025-11-14T04:08:43.135376697",
#   "status": 403
# }
```

---

## Notas Adicionales

### CORS

La API está configurada para aceptar peticiones desde cualquier origen (`*`). En producción, se recomienda restringir los orígenes permitidos.

### Seguridad

- Los tokens JWT contienen información del usuario (email y autoridad)
- Las contraseñas se almacenan hasheadas usando BCrypt
- Los tokens tienen un tiempo de expiración configurable
- Se recomienda usar HTTPS en producción

### Persistencia

Actualmente, la aplicación utiliza un repositorio en memoria. Los datos se pierden al reiniciar la aplicación.

---

