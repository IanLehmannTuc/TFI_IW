# 📚 API Reference - Sistema de Gestión de Urgencias

## Tabla de Contenidos

- [Información General](#información-general)
- [Autenticación](#autenticación)
- [Endpoints](#endpoints)
  - [Autenticación](#endpoints-autenticación)
  - [Pacientes](#endpoints-pacientes)
  - [Ingresos](#endpoints-ingresos)
  - [Cola de Atención](#endpoints-cola-de-atención)
  - [Atenciones](#endpoints-atenciones)
  - [Obras Sociales](#endpoints-obras-sociales)
- [Modelos de Datos](#modelos-de-datos)
- [Códigos de Error](#códigos-de-error)

---

## Información General

### Base URL

```
http://localhost:8080
```

### Formato

- **Request Body**: JSON
- **Response Body**: JSON
- **Charset**: UTF-8

### Headers Comunes

```http
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>  (excepto endpoints públicos)
```

---

## Autenticación

El sistema utiliza **JWT (JSON Web Tokens)** para autenticación.

### Flujo de Autenticación

```
1. Cliente llama a POST /api/auth/login con credenciales
2. Server valida y retorna JWT token
3. Cliente incluye token en header Authorization de requests posteriores
4. Server valida token en cada request
```

### Token JWT

- **Duración**: 24 horas (configurable en `application.properties`)
- **Algoritmo**: HS256 (HMAC con SHA-256)
- **Claims incluidos**:
  - `sub`: ID del usuario
  - `email`: Email del usuario
  - `autoridad`: Rol (MEDICO o ENFERMERO)
  - `iat`: Fecha de emisión
  - `exp`: Fecha de expiración

### Endpoints Públicos (Sin Autenticación)

- `POST /api/auth/registro`
- `POST /api/auth/login`

### Endpoints Protegidos (Requieren JWT)

Todos los demás endpoints requieren header:

```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Endpoints

---

## Endpoints: Autenticación

### 🟢 POST /api/auth/registro

Registra un nuevo usuario (médico o enfermero) en el sistema.

**Acceso**: Público (sin autenticación)

#### Request

```http
POST /api/auth/registro
Content-Type: application/json
```

```json
{
  "email": "medico@hospital.com",
  "password": "password123",
  "cuil": "20-30405060-7",
  "nombre": "Juan",
  "apellido": "Pérez",
  "matricula": "MN12345",
  "autoridad": "MEDICO"
}
```

**Campos**:
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| email | string | ✅ | Email válido, único en el sistema |
| password | string | ✅ | Contraseña (mínimo 6 caracteres) |
| cuil | string | ✅ | CUIL argentino formato XX-XXXXXXXX-X |
| nombre | string | ✅ | Nombre del usuario |
| apellido | string | ✅ | Apellido del usuario |
| matricula | string | ✅ | Matrícula profesional (única) |
| autoridad | string | ✅ | Rol: "MEDICO" o "ENFERMERO" |

#### Response

**201 Created**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI...",
  "usuario": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "medico@hospital.com",
    "cuil": "20-30405060-7",
    "nombre": "Juan",
    "apellido": "Pérez",
    "matricula": "MN12345",
    "autoridad": "MEDICO"
  }
}
```

**400 Bad Request**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email ya está registrado",
  "path": "/api/auth/registro"
}
```

---

### 🟢 POST /api/auth/login

Autentica un usuario y retorna un token JWT.

**Acceso**: Público (sin autenticación)

#### Request

```http
POST /api/auth/login
Content-Type: application/json
```

```json
{
  "email": "medico@hospital.com",
  "password": "password123"
}
```

**Campos**:
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| email | string | ✅ | Email registrado |
| password | string | ✅ | Contraseña |

#### Response

**200 OK**

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI...",
  "usuario": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "email": "medico@hospital.com",
    "cuil": "20-30405060-7",
    "nombre": "Juan",
    "apellido": "Pérez",
    "matricula": "MN12345",
    "autoridad": "MEDICO"
  }
}
```

**401 Unauthorized**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Credenciales inválidas",
  "path": "/api/auth/login"
}
```

---

## Endpoints: Pacientes

### 🔒 GET /api/pacientes

Obtiene la lista de todos los pacientes registrados.

**Acceso**: Requiere autenticación (MEDICO o ENFERMERO)

#### Request

```http
GET /api/pacientes
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "cuil": "20-20304050-5",
    "nombre": "María",
    "apellido": "González",
    "email": "maria@example.com",
    "telefono": "+54 9 11 1234-5678",
    "fechaNacimiento": "1985-05-15",
    "edad": 40,
    "sexo": "F",
    "domicilio": {
      "calle": "Av. Corrientes",
      "numero": 1234,
      "localidad": "Buenos Aires"
    },
    "afiliado": {
      "obraSocial": {
        "id": 1,
        "nombre": "OSDE"
      },
      "numeroAfiliado": "123456789"
    }
  }
]
```

---

### 🔒 GET /api/pacientes/{id}

Obtiene un paciente específico por ID.

**Acceso**: Requiere autenticación (MEDICO o ENFERMERO)

#### Request

```http
GET /api/pacientes/123e4567-e89b-12d3-a456-426614174000
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "cuil": "20-20304050-5",
  "nombre": "María",
  "apellido": "González",
  "email": "maria@example.com",
  "telefono": "+54 9 11 1234-5678",
  "fechaNacimiento": "1985-05-15",
  "edad": 40,
  "sexo": "F",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "afiliado": {
    "obraSocial": {
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "123456789"
  }
}
```

**404 Not Found**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Paciente no encontrado",
  "path": "/api/pacientes/123e4567-e89b-12d3-a456-426614174000"
}
```

---

### 🔒 POST /api/pacientes

Registra un nuevo paciente en el sistema.

**Acceso**: Requiere autenticación y autoridad ENFERMERO

#### Request

```http
POST /api/pacientes
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "cuil": "20-20304050-5",
  "nombre": "María",
  "apellido": "González",
  "email": "maria@example.com",
  "telefono": "+54 9 11 1234-5678",
  "fechaNacimiento": "1985-05-15",
  "sexo": "F",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "obraSocial": {
    "obraSocial": {
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "123456789"
  }
}
```

**Campos**:
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| cuil | string | ✅ | CUIL argentino único |
| nombre | string | ✅ | Nombre del paciente |
| apellido | string | ✅ | Apellido del paciente |
| email | string | ❌ | Email válido |
| telefono | string | ❌ | Teléfono formato internacional |
| fechaNacimiento | string | ❌ | Formato YYYY-MM-DD |
| sexo | string | ❌ | "M" (Masculino) o "F" (Femenino) |
| domicilio | object | ✅ | Domicilio del paciente |
| domicilio.calle | string | ✅ | Calle |
| domicilio.numero | integer | ✅ | Número |
| domicilio.localidad | string | ✅ | Localidad |
| obraSocial | object | ❌ | Afiliación a obra social |
| obraSocial.obraSocial.id | integer | ✅* | ID de obra social (*si se incluye obraSocial) |
| obraSocial.numeroAfiliado | string | ✅* | Número de afiliado |

#### Response

**201 Created**

```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "cuil": "20-20304050-5",
  "nombre": "María",
  "apellido": "González",
  "email": "maria@example.com",
  "telefono": "+54 9 11 1234-5678",
  "fechaNacimiento": "1985-05-15",
  "edad": 40,
  "sexo": "F",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "afiliado": {
    "obraSocial": {
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "123456789"
  }
}
```

**400 Bad Request**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "CUIL ya está registrado",
  "path": "/api/pacientes"
}
```

---

## Endpoints: Ingresos

### 🔒 POST /api/ingresos

Registra un nuevo ingreso de paciente a urgencias.

**Acceso**: Requiere autenticación y autoridad ENFERMERO

**Nota**: Si el paciente no existe, se crea automáticamente con los datos proporcionados.

#### Request

```http
POST /api/ingresos
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "pacienteCuil": "20-20304050-5",
  "enfermeroCuil": "27-12345678-9",
  "descripcion": "Dolor torácico intenso, dificultad respiratoria",
  "temperatura": 37.5,
  "tensionSistolica": 140,
  "tensionDiastolica": 90,
  "frecuenciaCardiaca": 110,
  "frecuenciaRespiratoria": 22,
  "nivelEmergencia": "EMERGENCIA",
  "pacienteNombre": "María",
  "pacienteApellido": "González",
  "pacienteEmail": "maria@example.com",
  "pacienteTelefono": "+54 9 11 1234-5678",
  "pacienteFechaNacimiento": "1985-05-15",
  "pacienteSexo": "F",
  "pacienteDomicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "pacienteObraSocial": {
    "obraSocial": {
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "123456789"
  }
}
```

**Campos Obligatorios**:
| Campo | Tipo | Descripción |
|-------|------|-------------|
| pacienteCuil | string | CUIL del paciente |
| enfermeroCuil | string | CUIL del enfermero |
| descripcion | string | Descripción del motivo de ingreso |
| temperatura | number | Temperatura en °C (35.0 - 42.0) |
| tensionSistolica | integer | Presión sistólica (60 - 250 mmHg) |
| tensionDiastolica | integer | Presión diastólica (40 - 150 mmHg) |
| frecuenciaCardiaca | integer | Latidos por minuto (40 - 200) |
| frecuenciaRespiratoria | integer | Respiraciones por minuto (8 - 60) |
| nivelEmergencia | string | CRITICA, EMERGENCIA, URGENCIA, URGENCIA_MENOR, SIN_URGENCIA |

**Campos Opcionales (requeridos solo si el paciente NO existe)**:
| Campo | Tipo | Descripción |
|-------|------|-------------|
| pacienteNombre | string | Nombre (requerido si paciente no existe) |
| pacienteApellido | string | Apellido (requerido si paciente no existe) |
| pacienteDomicilio | object | Domicilio (requerido si paciente no existe) |
| pacienteEmail | string | Email (opcional) |
| pacienteTelefono | string | Teléfono (opcional) |
| pacienteFechaNacimiento | string | Fecha nacimiento YYYY-MM-DD (opcional) |
| pacienteSexo | string | M o F (opcional) |
| pacienteObraSocial | object | Obra social (opcional) |

#### Response

**201 Created**

```json
{
  "id": "abc123-def456-ghi789",
  "paciente": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "cuil": "20-20304050-5",
    "nombre": "María",
    "apellido": "González",
    "email": "maria@example.com",
    "telefono": "+54 9 11 1234-5678",
    "fechaNacimiento": "1985-05-15",
    "edad": 40,
    "sexo": "F"
  },
  "enfermero": {
    "id": "enfermero-uuid",
    "cuil": "27-12345678-9",
    "nombre": "Ana",
    "apellido": "Martínez",
    "matricula": "ENF5678"
  },
  "descripcion": "Dolor torácico intenso, dificultad respiratoria",
  "fechaHoraIngreso": "2025-12-10T14:30:00",
  "temperatura": 37.5,
  "tensionArterial": {
    "sistolica": 140,
    "diastolica": 90
  },
  "frecuenciaCardiaca": 110,
  "frecuenciaRespiratoria": 22,
  "nivelEmergencia": "EMERGENCIA",
  "prioridad": 4,
  "estado": "PENDIENTE",
  "atencion": null
}
```

**400 Bad Request**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Temperatura debe estar entre 35°C y 42°C",
  "path": "/api/ingresos"
}
```

**404 Not Found**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Enfermero no encontrado con CUIL: 27-12345678-9",
  "path": "/api/ingresos"
}
```

---

### 🔒 GET /api/ingresos

Obtiene la lista de todos los ingresos (sin ordenar por prioridad).

**Acceso**: Requiere autenticación (MEDICO o ENFERMERO)

#### Request

```http
GET /api/ingresos
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
[
  {
    "id": "abc123-def456-ghi789",
    "paciente": {
      "id": "123e4567-e89b-12d3-a456-426614174000",
      "cuil": "20-20304050-5",
      "nombre": "María",
      "apellido": "González",
      "edad": 40
    },
    "enfermero": {
      "id": "enfermero-uuid",
      "cuil": "27-12345678-9",
      "nombre": "Ana",
      "apellido": "Martínez"
    },
    "descripcion": "Dolor torácico intenso",
    "fechaHoraIngreso": "2025-12-10T14:30:00",
    "temperatura": 37.5,
    "tensionArterial": {
      "sistolica": 140,
      "diastolica": 90
    },
    "frecuenciaCardiaca": 110,
    "frecuenciaRespiratoria": 22,
    "nivelEmergencia": "EMERGENCIA",
    "prioridad": 4,
    "estado": "PENDIENTE"
  }
]
```

---

### 🔒 GET /api/ingresos/{id}

Obtiene un ingreso específico por ID.

**Acceso**: Requiere autenticación (MEDICO o ENFERMERO)

#### Request

```http
GET /api/ingresos/abc123-def456-ghi789
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
{
  "id": "abc123-def456-ghi789",
  "paciente": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "cuil": "20-20304050-5",
    "nombre": "María",
    "apellido": "González",
    "edad": 40
  },
  "enfermero": {
    "id": "enfermero-uuid",
    "cuil": "27-12345678-9",
    "nombre": "Ana",
    "apellido": "Martínez"
  },
  "descripcion": "Dolor torácico intenso",
  "fechaHoraIngreso": "2025-12-10T14:30:00",
  "temperatura": 37.5,
  "tensionArterial": {
    "sistolica": 140,
    "diastolica": 90
  },
  "frecuenciaCardiaca": 110,
  "frecuenciaRespiratoria": 22,
  "nivelEmergencia": "EMERGENCIA",
  "prioridad": 4,
  "estado": "EN_PROCESO",
  "atencion": null
}
```

**404 Not Found**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ingreso no encontrado",
  "path": "/api/ingresos/abc123-def456-ghi789"
}
```

---

### 🔒 DELETE /api/ingresos/{id}

Elimina un ingreso del sistema (repositorio y cola de atención).

**Acceso**: Requiere autenticación y autoridad ENFERMERO

#### Request

```http
DELETE /api/ingresos/abc123-def456-ghi789
Authorization: Bearer <token>
```

#### Response

**200 OK**

```
(Sin body)
```

**404 Not Found**

```json
{
  "timestamp": "2025-12-10T14:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Ingreso no encontrado",
  "path": "/api/ingresos/abc123-def456-ghi789"
}
```

---

## Endpoints: Cola de Atención

### 🔒 GET /api/cola-atencion

Obtiene la cola de atención ordenada por prioridad y orden de llegada.

**Acceso**: Requiere autenticación (MEDICO o ENFERMERO)

**Ordenamiento**:
1. Prioridad (mayor a menor): CRITICA (5) > EMERGENCIA (4) > URGENCIA (3) > URGENCIA_MENOR (2) > SIN_URGENCIA (1)
2. Fecha de ingreso (menor a mayor): Primero en llegar, primero en la cola

#### Request

```http
GET /api/cola-atencion
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
[
  {
    "id": "ingreso-1",
    "paciente": {
      "nombre": "Pedro",
      "apellido": "López",
      "cuil": "20-11223344-5",
      "edad": 55
    },
    "descripcion": "Dolor torácico severo",
    "fechaHoraIngreso": "2025-12-10T14:00:00",
    "nivelEmergencia": "CRITICA",
    "prioridad": 5,
    "estado": "PENDIENTE"
  },
  {
    "id": "ingreso-2",
    "paciente": {
      "nombre": "María",
      "apellido": "González",
      "cuil": "20-20304050-5",
      "edad": 40
    },
    "descripcion": "Fractura de brazo",
    "fechaHoraIngreso": "2025-12-10T14:15:00",
    "nivelEmergencia": "URGENCIA",
    "prioridad": 3,
    "estado": "PENDIENTE"
  }
]
```

---

### 🔒 POST /api/cola-atencion/atender

Atiende al siguiente paciente en la cola (el de mayor prioridad).

Cambia el estado del ingreso de `PENDIENTE` a `EN_PROCESO`.

**Acceso**: Requiere autenticación y autoridad ENFERMERO

#### Request

```http
POST /api/cola-atencion/atender
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
{
  "id": "ingreso-1",
  "paciente": {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "cuil": "20-11223344-5",
    "nombre": "Pedro",
    "apellido": "López",
    "edad": 55
  },
  "enfermero": {
    "id": "enfermero-uuid",
    "cuil": "27-12345678-9",
    "nombre": "Ana",
    "apellido": "Martínez"
  },
  "descripcion": "Dolor torácico severo",
  "fechaHoraIngreso": "2025-12-10T14:00:00",
  "temperatura": 38.2,
  "tensionArterial": {
    "sistolica": 160,
    "diastolica": 100
  },
  "frecuenciaCardiaca": 120,
  "frecuenciaRespiratoria": 28,
  "nivelEmergencia": "CRITICA",
  "prioridad": 5,
  "estado": "EN_PROCESO"
}
```

**204 No Content**

```
(Sin body - No hay pacientes en cola)
```

---

## Endpoints: Atenciones

### 🔒 POST /api/atenciones

Registra una atención médica para un ingreso en proceso.

Cambia el estado del ingreso de `EN_PROCESO` a `FINALIZADO`.

**Acceso**: Requiere autenticación y autoridad MEDICO

#### Request

```http
POST /api/atenciones
Authorization: Bearer <token>
Content-Type: application/json
```

```json
{
  "ingresoId": "abc123-def456-ghi789",
  "medicoId": "medico-uuid-12345",
  "informe": "Paciente presenta angina de pecho. Se administró nitroglicerina sublingual. Electrocardiograma sin cambios isquémicos. Se indica internación para observación y estudios complementarios."
}
```

**Campos**:
| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| ingresoId | string | ✅ | UUID del ingreso a atender |
| medicoId | string | ✅ | UUID del médico que realiza la atención |
| informe | string | ✅ | Informe médico detallado (no puede estar vacío) |

#### Response

**201 Created**

```json
{
  "id": "atencion-uuid-abc123",
  "ingresoId": "abc123-def456-ghi789",
  "medico": {
    "id": "medico-uuid-12345",
    "cuil": "20-30405060-7",
    "nombre": "Juan",
    "apellido": "Pérez",
    "matricula": "MN12345",
    "autoridad": "MEDICO"
  },
  "informeMedico": "Paciente presenta angina de pecho...",
  "fechaAtencion": "2025-12-10T15:45:00"
}
```

**400 Bad Request**

```json
{
  "timestamp": "2025-12-10T15:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El ingreso debe estar en estado EN_PROCESO para registrar una atención. Estado actual: PENDIENTE",
  "path": "/api/atenciones"
}
```

**404 Not Found**

```json
{
  "timestamp": "2025-12-10T15:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "No se encontró el ingreso con ID: abc123-def456-ghi789",
  "path": "/api/atenciones"
}
```

---

### 🔒 GET /api/atenciones/ingreso/{ingresoId}

Obtiene la atención médica asociada a un ingreso.

**Acceso**: Requiere autenticación (MEDICO o ENFERMERO)

#### Request

```http
GET /api/atenciones/ingreso/abc123-def456-ghi789
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
{
  "id": "atencion-uuid-abc123",
  "ingresoId": "abc123-def456-ghi789",
  "medico": {
    "id": "medico-uuid-12345",
    "cuil": "20-30405060-7",
    "nombre": "Juan",
    "apellido": "Pérez",
    "matricula": "MN12345",
    "autoridad": "MEDICO"
  },
  "informeMedico": "Paciente presenta angina de pecho...",
  "fechaAtencion": "2025-12-10T15:45:00"
}
```

**404 Not Found**

```json
{
  "timestamp": "2025-12-10T15:45:00",
  "status": 404,
  "error": "Not Found",
  "message": "Atención no encontrada para el ingreso",
  "path": "/api/atenciones/ingreso/abc123-def456-ghi789"
}
```

---

## Endpoints: Obras Sociales

### 🔒 GET /api/obras-sociales

Obtiene la lista de todas las obras sociales disponibles.

**Acceso**: Requiere autenticación (MEDICO o ENFERMERO)

**Fuente**: API externa (caché de 1 hora)

#### Request

```http
GET /api/obras-sociales
Authorization: Bearer <token>
```

#### Response

**200 OK**

```json
[
  {
    "id": 1,
    "nombre": "OSDE"
  },
  {
    "id": 2,
    "nombre": "Swiss Medical"
  },
  {
    "id": 3,
    "nombre": "Galeno"
  },
  {
    "id": 4,
    "nombre": "IOMA"
  },
  {
    "id": 5,
    "nombre": "PAMI"
  }
]
```

**503 Service Unavailable**

```json
{
  "timestamp": "2025-12-10T15:45:00",
  "status": 503,
  "error": "Service Unavailable",
  "message": "API de obras sociales no disponible",
  "path": "/api/obras-sociales"
}
```

---

## Modelos de Datos

### Usuario

```json
{
  "id": "string (UUID)",
  "email": "string",
  "cuil": "string (XX-XXXXXXXX-X)",
  "nombre": "string",
  "apellido": "string",
  "matricula": "string",
  "autoridad": "MEDICO | ENFERMERO"
}
```

### Paciente

```json
{
  "id": "string (UUID)",
  "cuil": "string (XX-XXXXXXXX-X)",
  "nombre": "string",
  "apellido": "string",
  "email": "string | null",
  "telefono": "string | null",
  "fechaNacimiento": "string (YYYY-MM-DD) | null",
  "edad": "integer | null",
  "sexo": "M | F | null",
  "domicilio": {
    "calle": "string",
    "numero": "integer",
    "localidad": "string"
  },
  "afiliado": {
    "obraSocial": {
      "id": "integer",
      "nombre": "string"
    },
    "numeroAfiliado": "string"
  } | null
}
```

### Ingreso

```json
{
  "id": "string (UUID)",
  "paciente": "Paciente",
  "enfermero": "Usuario",
  "descripcion": "string",
  "fechaHoraIngreso": "string (ISO 8601)",
  "temperatura": "number (35.0-42.0)",
  "tensionArterial": {
    "sistolica": "integer (60-250)",
    "diastolica": "integer (40-150)"
  },
  "frecuenciaCardiaca": "integer (40-200)",
  "frecuenciaRespiratoria": "integer (8-60)",
  "nivelEmergencia": "CRITICA | EMERGENCIA | URGENCIA | URGENCIA_MENOR | SIN_URGENCIA",
  "prioridad": "integer (1-5)",
  "estado": "PENDIENTE | EN_PROCESO | FINALIZADO",
  "atencion": "Atencion | null"
}
```

### Atención

```json
{
  "id": "string (UUID)",
  "ingresoId": "string (UUID)",
  "medico": "Usuario",
  "informeMedico": "string",
  "fechaAtencion": "string (ISO 8601)"
}
```

### Nivel de Emergencia

| Valor | Prioridad | Descripción |
|-------|-----------|-------------|
| CRITICA | 5 | Riesgo vital inmediato |
| EMERGENCIA | 4 | Situación grave, atención urgente |
| URGENCIA | 3 | Requiere atención pronta |
| URGENCIA_MENOR | 2 | Puede esperar moderadamente |
| SIN_URGENCIA | 1 | No urgente, atención diferida |

### Estado del Ingreso

| Estado | Descripción |
|--------|-------------|
| PENDIENTE | Ingreso registrado, en cola de atención |
| EN_PROCESO | Paciente siendo atendido |
| FINALIZADO | Atención médica completada |

---

## Códigos de Error

### Códigos HTTP

| Código | Descripción | Cuándo ocurre |
|--------|-------------|---------------|
| 200 | OK | Solicitud exitosa |
| 201 | Created | Recurso creado exitosamente |
| 204 | No Content | Solicitud exitosa sin contenido de respuesta |
| 400 | Bad Request | Datos de entrada inválidos o faltantes |
| 401 | Unauthorized | Token JWT inválido, expirado o faltante |
| 403 | Forbidden | Usuario no tiene permisos para la operación |
| 404 | Not Found | Recurso no encontrado |
| 500 | Internal Server Error | Error interno del servidor |
| 503 | Service Unavailable | Servicio externo no disponible |

### Formato de Error

Todos los errores retornan el siguiente formato:

```json
{
  "timestamp": "2025-12-10T15:45:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Descripción detallada del error",
  "path": "/api/endpoint"
}
```

### Mensajes de Error Comunes

#### Autenticación

```json
// Token faltante o inválido
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token JWT inválido o expirado"
}

// Usuario no tiene permisos
{
  "status": 403,
  "error": "Forbidden",
  "message": "Acceso denegado. Se requiere autoridad: ENFERMERO"
}
```

#### Validación

```json
// Campo obligatorio faltante
{
  "status": 400,
  "error": "Bad Request",
  "message": "CUIL del paciente es obligatorio"
}

// Valor fuera de rango
{
  "status": 400,
  "error": "Bad Request",
  "message": "Temperatura debe estar entre 35°C y 42°C"
}

// Formato inválido
{
  "status": 400,
  "error": "Bad Request",
  "message": "Email inválido"
}
```

#### Recursos No Encontrados

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Paciente no encontrado con CUIL: 20-20304050-5"
}
```

#### Estado Inválido

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "El ingreso debe estar en estado EN_PROCESO para registrar una atención. Estado actual: PENDIENTE"
}
```

---

## Ejemplos de Uso

### Flujo Completo: Ingreso → Atención → Finalización

#### 1. Login como Enfermero

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "enfermero@hospital.com",
    "password": "password123"
  }'
```

**Guardar el token de la respuesta**

#### 2. Registrar Ingreso

```bash
curl -X POST http://localhost:8080/api/ingresos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_ENFERMERO>" \
  -d '{
    "pacienteCuil": "20-20304050-5",
    "enfermeroCuil": "27-12345678-9",
    "descripcion": "Dolor torácico intenso",
    "temperatura": 37.5,
    "tensionSistolica": 140,
    "tensionDiastolica": 90,
    "frecuenciaCardiaca": 110,
    "frecuenciaRespiratoria": 22,
    "nivelEmergencia": "EMERGENCIA",
    "pacienteNombre": "María",
    "pacienteApellido": "González",
    "pacienteDomicilio": {
      "calle": "Av. Corrientes",
      "numero": 1234,
      "localidad": "Buenos Aires"
    }
  }'
```

**Guardar el ID del ingreso de la respuesta**

#### 3. Ver Cola de Atención

```bash
curl -X GET http://localhost:8080/api/cola-atencion \
  -H "Authorization: Bearer <TOKEN_ENFERMERO>"
```

#### 4. Atender al Siguiente Paciente

```bash
curl -X POST http://localhost:8080/api/cola-atencion/atender \
  -H "Authorization: Bearer <TOKEN_ENFERMERO>"
```

#### 5. Login como Médico

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "medico@hospital.com",
    "password": "password123"
  }'
```

**Guardar el token de la respuesta**

#### 6. Registrar Atención Médica

```bash
curl -X POST http://localhost:8080/api/atenciones \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TOKEN_MEDICO>" \
  -d '{
    "ingresoId": "<INGRESO_ID>",
    "medicoId": "<MEDICO_ID>",
    "informe": "Paciente con angina de pecho. Se administró nitroglicerina. ECG sin cambios isquémicos. Se indica internación para observación."
  }'
```

#### 7. Verificar Ingreso Finalizado

```bash
curl -X GET http://localhost:8080/api/ingresos/<INGRESO_ID> \
  -H "Authorization: Bearer <TOKEN>"
```

**El estado debe ser "FINALIZADO"**

---

## Rate Limiting

Actualmente el sistema **no implementa rate limiting**.

Para producción se recomienda:
- Límite de 100 requests/minuto por usuario
- Límite de 1000 requests/minuto por IP

---

## Versionado de API

Actualmente: **v1** (implícito en `/api/`)

Futuras versiones usarán: `/api/v2/`, `/api/v3/`, etc.

---

## Soporte

Para más información, consultar:
- [README.md](./README.md) - Guía de inicio rápido
- [ARQUITECTURA.md](./ARQUITECTURA.md) - Documentación de arquitectura
- [QUICK_START_CURL.md](./QUICK_START_CURL.md) - Ejemplos prácticos con curl

