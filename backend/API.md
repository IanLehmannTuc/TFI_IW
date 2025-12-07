# Documentación de la API - Sistema de Gestión de Urgencias

## Base URL
```
http://localhost:8080
```

## Autenticación

La mayoría de los endpoints requieren autenticación mediante JWT (JSON Web Token). Para obtener un token, utiliza el endpoint de login.

**Header requerido para endpoints protegidos:**
```
Authorization: Bearer <token>
```

## Autoridades

El sistema maneja dos tipos de autoridades:
- **MEDICO**: Puede atender pacientes de la cola de urgencias
- **ENFERMERO**: Puede registrar pacientes e ingresos a urgencias

---

## 1. Autenticación (`/api/auth`)

### 1.1. Registrar Usuario
Registra un nuevo usuario en el sistema (médico o enfermero).

**Endpoint:** `POST /api/auth/registro`  
**Autenticación:** No requerida

**Request Body:**
```json
{
  "email": "medico@hospital.com",
  "password": "Medico123!",
  "autoridad": "MEDICO",
  "cuil": "20-12345678-6",
  "nombre": "Juan",
  "apellido": "García",
  "matricula": "MP123456"
}
```

**Campos:**
- `email` (string, requerido): Email del usuario
- `password` (string, requerido): Contraseña
- `autoridad` (enum, requerido): `MEDICO` o `ENFERMERO`
- `cuil` (string, requerido): CUIL del profesional
- `nombre` (string, requerido): Nombre del profesional
- `apellido` (string, requerido): Apellido del profesional
- `matricula` (string, requerido): Matrícula profesional

**Response:** `201 Created`
```json
{
  "id": "uuid-del-usuario",
  "email": "medico@hospital.com",
  "nombre": "Juan",
  "apellido": "García",
  "autoridad": "MEDICO"
}
```

**Errores:**
- `400 Bad Request`: Datos inválidos o email ya existe

---

### 1.2. Login
Inicia sesión y obtiene un token JWT.

**Endpoint:** `POST /api/auth/login`  
**Autenticación:** No requerida

**Request Body:**
```json
{
  "email": "medico@hospital.com",
  "password": "Medico123!"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "medico@hospital.com",
  "autoridad": "MEDICO"
}
```

**Errores:**
- `401 Unauthorized`: Credenciales inválidas

---

### 1.3. Obtener Perfil
Obtiene el perfil completo del usuario autenticado.

**Endpoint:** `GET /api/auth/perfil`  
**Autenticación:** Requerida (cualquier usuario)

**Response:** `200 OK`
```json
{
  "id": "uuid-del-usuario",
  "email": "medico@hospital.com",
  "nombre": "Juan",
  "apellido": "García",
  "cuil": "20-12345678-6",
  "matricula": "MP123456",
  "autoridad": "MEDICO"
}
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente

---

### 1.4. Verificar Autenticación
Endpoint de prueba para verificar que el token es válido.

**Endpoint:** `GET /api/auth/verificar`  
**Autenticación:** Requerida (cualquier usuario)

**Response:** `200 OK`
```
Token válido. Usuario autenticado: medico@hospital.com con autoridad: MEDICO
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente

---

## 2. Pacientes (`/api/pacientes`)

### 2.1. Crear Paciente
Registra un nuevo paciente en el sistema.

**Endpoint:** `POST /api/pacientes`  
**Autenticación:** Requerida  
**Autoridad requerida:** `ENFERMERO`

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
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "12345678"
  }
}
```

**Campos:**
- `cuil` (string, requerido): CUIL del paciente
- `nombre` (string, requerido): Nombre del paciente
- `apellido` (string, requerido): Apellido del paciente
- `domicilio` (object, requerido): Domicilio del paciente
  - `calle` (string, requerido)
  - `numero` (integer, requerido)
  - `localidad` (string, requerido)
- `obraSocial` (object, opcional): Información de obra social
  - `obraSocial` (object, requerido si se especifica obraSocial)
    - `id` (integer, requerido)
    - `nombre` (string, opcional)
  - `numeroAfiliado` (string, requerido si se especifica obraSocial)

**Response:** `201 Created`
```json
{
  "id": "uuid-del-paciente",
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
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "12345678"
  }
}
```

**Nota importante sobre obras sociales:**
Si se especifica una obra social al registrar un paciente, el sistema **verifica automáticamente** la afiliación del paciente mediante una API externa. El registro solo se completará si:
- El número de afiliado es válido
- El paciente está efectivamente afiliado a la obra social especificada

Si la verificación falla, se retornará un error `400 Bad Request` con un mensaje descriptivo.

**Errores:**
- `400 Bad Request`: Datos inválidos, CUIL ya existe, o el paciente no está afiliado a la obra social especificada
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad ENFERMERO

---

### 2.2. Listar Pacientes (Paginado)
Obtiene una lista paginada de todos los pacientes registrados.

**Endpoint:** `GET /api/pacientes`  
**Autenticación:** Requerida (cualquier usuario)

**Query Parameters:**
- `page` (integer, opcional, default: 0): Número de página (0-indexed)
- `size` (integer, opcional, default: 10): Tamaño de la página
- `sortBy` (string, opcional, default: "cuil"): Campo de ordenamiento (`cuil`, `nombre`, `apellido`, `email`, `id`)
- `direction` (string, opcional, default: "ASC"): Dirección del ordenamiento (`ASC` o `DESC`)

**Ejemplo:**
```
GET /api/pacientes?page=0&size=10&sortBy=nombre&direction=ASC
```

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "uuid-del-paciente",
      "cuil": "20-20304050-5",
      "nombre": "Juan",
      "apellido": "Pérez",
      "domicilio": {
        "calle": "Av. Corrientes",
        "numero": 1234,
        "localidad": "Buenos Aires"
      },
      "obraSocial": null
    }
  ],
  "totalElements": 100,
  "totalPages": 10,
  "size": 10,
  "number": 0,
  "first": true,
  "last": false,
  "numberOfElements": 10
}
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente

---

### 2.3. Buscar Paciente por CUIL
Obtiene la información de un paciente específico por su CUIL.

**Endpoint:** `GET /api/pacientes/{cuil}`  
**Autenticación:** Requerida (cualquier usuario)

**Path Parameters:**
- `cuil` (string): CUIL del paciente a buscar

**Ejemplo:**
```
GET /api/pacientes/20-20304050-5
```

**Response:** `200 OK`
```json
{
  "id": "uuid-del-paciente",
  "cuil": "20-20304050-5",
  "nombre": "Juan",
  "apellido": "Pérez",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "obraSocial": null
}
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente
- `404 Not Found`: Paciente no encontrado

---

### 2.4. Actualizar Paciente
Actualiza los datos de un paciente existente por su CUIL.

**Endpoint:** `PUT /api/pacientes/{cuil}`  
**Autenticación:** Requerida  
**Autoridad requerida:** `ENFERMERO`

**Path Parameters:**
- `cuil` (string): CUIL del paciente a actualizar

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
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "12345678"
  }
}
```

**Campos:**
- `cuil` (string, requerido): CUIL del paciente (debe coincidir con el CUIL en la URL)
- `nombre` (string, requerido): Nombre del paciente
- `apellido` (string, requerido): Apellido del paciente
- `domicilio` (object, requerido): Domicilio del paciente
  - `calle` (string, requerido)
  - `numero` (integer, requerido)
  - `localidad` (string, requerido)
- `obraSocial` (object, opcional): Información de obra social
  - `obraSocial` (object, requerido si se especifica obraSocial)
    - `id` (integer, requerido)
    - `nombre` (string, opcional)
  - `numeroAfiliado` (string, requerido si se especifica obraSocial)

**Ejemplo:**
```
PUT /api/pacientes/20-20304050-5
```

**Response:** `200 OK`
```json
{
  "id": "uuid-del-paciente",
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
      "id": 1,
      "nombre": "OSDE"
    },
    "numeroAfiliado": "12345678"
  }
}
```

**Nota importante sobre obras sociales:**
Al igual que en el registro, si se especifica una obra social al actualizar un paciente, el sistema **verifica automáticamente** la afiliación mediante la API externa.

**Errores:**
- `400 Bad Request`: Datos inválidos, el CUIL en el body no coincide con el de la URL, o el paciente no está afiliado a la obra social especificada
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad ENFERMERO
- `404 Not Found`: Paciente no encontrado

---

### 2.5. Eliminar Paciente
Elimina un paciente del sistema por su CUIL.

**Endpoint:** `DELETE /api/pacientes/{cuil}`  
**Autenticación:** Requerida  
**Autoridad requerida:** `ENFERMERO`

**Path Parameters:**
- `cuil` (string): CUIL del paciente a eliminar

**Ejemplo:**
```
DELETE /api/pacientes/20-20304050-5
```

**Response:** `200 OK`
```json
{
  "id": "uuid-del-paciente",
  "cuil": "20-20304050-5",
  "nombre": "Juan",
  "apellido": "Pérez",
  "domicilio": {
    "calle": "Av. Corrientes",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "obraSocial": null
}
```

**Errores:**
- `400 Bad Request`: CUIL inválido o vacío
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad ENFERMERO
- `404 Not Found`: Paciente no encontrado

---

## 4. Urgencias (`/api/urgencias`)

### 4.1. Registrar Ingreso
Registra un nuevo ingreso a urgencias. Si el paciente no existe, se crea automáticamente.

**Endpoint:** `POST /api/urgencias`  
**Autenticación:** Requerida  
**Autoridad requerida:** `ENFERMERO`

**Request Body:**
```json
{
  "pacienteCuil": "20-20304050-5",
  "pacienteNombre": "Juan",
  "pacienteApellido": "Pérez",
  "pacienteEmail": "juan@example.com",
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
    "numeroAfiliado": "12345678"
  },
  "enfermeroCuil": "27-12345678-9",
  "descripcion": "Dolor de pecho intenso",
  "temperatura": 37.5,
  "tensionSistolica": 120,
  "tensionDiastolica": 80,
  "frecuenciaCardiaca": 85,
  "frecuenciaRespiratoria": 18,
  "nivelEmergencia": "ALTA"
}
```

**Campos:**
- `pacienteCuil` (string, requerido): CUIL del paciente
- `pacienteNombre` (string, opcional): Nombre del paciente (si no existe)
- `pacienteApellido` (string, opcional): Apellido del paciente (si no existe)
- `pacienteEmail` (string, opcional): Email del paciente (si no existe)
- `pacienteDomicilio` (object, opcional): Domicilio del paciente (si no existe)
- `pacienteObraSocial` (object, opcional): Obra social del paciente (si no existe)
- `enfermeroCuil` (string, requerido): CUIL del enfermero que registra
- `descripcion` (string, requerido): Descripción del motivo de ingreso
- `temperatura` (double, requerido): Temperatura corporal en °C
- `tensionSistolica` (integer, requerido): Tensión arterial sistólica
- `tensionDiastolica` (integer, requerido): Tensión arterial diastólica
- `frecuenciaCardiaca` (integer, requerido): Frecuencia cardíaca (latidos/min)
- `frecuenciaRespiratoria` (integer, requerido): Frecuencia respiratoria (respiraciones/min)
- `nivelEmergencia` (enum, requerido): `BAJA`, `MEDIA`, `ALTA`, `CRITICA`

**Response:** `201 Created`
```json
{
  "id": "uuid-del-ingreso",
  "paciente": {
    "id": "uuid-del-paciente",
    "cuil": "20-20304050-5",
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "enfermero": {
    "cuil": "27-12345678-9",
    "nombre": "María",
    "apellido": "González"
  },
  "descripcion": "Dolor de pecho intenso",
  "fechaHoraIngreso": "2024-01-15T10:30:00",
  "temperatura": 37.5,
  "tensionArterial": {
    "sistolica": 120,
    "diastolica": 80
  },
  "frecuenciaCardiaca": 85,
  "frecuenciaRespiratoria": 18,
  "nivelEmergencia": "ALTA",
  "estado": "EN_ESPERA"
}
```

**Errores:**
- `400 Bad Request`: Datos inválidos
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad ENFERMERO
- `404 Not Found`: Enfermero no encontrado

---

### 4.2. Obtener Todos los Ingresos
Obtiene una lista de todos los ingresos registrados.

**Endpoint:** `GET /api/urgencias`  
**Autenticación:** Requerida (cualquier usuario)

**Response:** `200 OK`
```json
[
  {
    "id": "uuid-del-ingreso",
    "paciente": {
      "id": "uuid-del-paciente",
      "cuil": "20-20304050-5",
      "nombre": "Juan",
      "apellido": "Pérez"
    },
    "enfermero": {
      "cuil": "27-12345678-9",
      "nombre": "María",
      "apellido": "González"
    },
    "descripcion": "Dolor de pecho intenso",
    "fechaHoraIngreso": "2024-01-15T10:30:00",
    "temperatura": 37.5,
    "tensionArterial": {
      "sistolica": 120,
      "diastolica": 80
    },
    "frecuenciaCardiaca": 85,
    "frecuenciaRespiratoria": 18,
    "nivelEmergencia": "ALTA",
    "estado": "EN_ESPERA"
  }
]
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente

---

### 4.3. Obtener Ingreso por ID
Obtiene la información de un ingreso específico por su ID.

**Endpoint:** `GET /api/urgencias/{id}`  
**Autenticación:** Requerida (cualquier usuario)

**Path Parameters:**
- `id` (string): ID del ingreso a buscar

**Response:** `200 OK`
```json
{
  "id": "uuid-del-ingreso",
  "paciente": {
    "id": "uuid-del-paciente",
    "cuil": "20-20304050-5",
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "enfermero": {
    "cuil": "27-12345678-9",
    "nombre": "María",
    "apellido": "González"
  },
  "descripcion": "Dolor de pecho intenso",
  "fechaHoraIngreso": "2024-01-15T10:30:00",
  "temperatura": 37.5,
  "tensionArterial": {
    "sistolica": 120,
    "diastolica": 80
  },
  "frecuenciaCardiaca": 85,
  "frecuenciaRespiratoria": 18,
  "nivelEmergencia": "ALTA",
  "estado": "EN_ESPERA"
}
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente
- `404 Not Found`: Ingreso no encontrado

---

### 4.4. Actualizar Ingreso
Actualiza la información de un ingreso existente.

**Endpoint:** `PUT /api/urgencias/{id}`  
**Autenticación:** Requerida  
**Autoridad requerida:** `ENFERMERO`

**Path Parameters:**
- `id` (string): ID del ingreso a actualizar

**Request Body:** (Mismo formato que registrar ingreso)

**Response:** `200 OK`
```json
{
  "id": "uuid-del-ingreso",
  "paciente": {
    "id": "uuid-del-paciente",
    "cuil": "20-20304050-5",
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "enfermero": {
    "cuil": "27-12345678-9",
    "nombre": "María",
    "apellido": "González"
  },
  "descripcion": "Dolor de pecho intenso - Actualizado",
  "fechaHoraIngreso": "2024-01-15T10:30:00",
  "temperatura": 37.8,
  "tensionArterial": {
    "sistolica": 125,
    "diastolica": 82
  },
  "frecuenciaCardiaca": 90,
  "frecuenciaRespiratoria": 20,
  "nivelEmergencia": "ALTA",
  "estado": "EN_ESPERA"
}
```

**Errores:**
- `400 Bad Request`: Datos inválidos
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad ENFERMERO
- `404 Not Found`: Ingreso, paciente o enfermero no encontrado

---

### 4.5. Eliminar Ingreso
Elimina un ingreso del sistema.

**Endpoint:** `DELETE /api/urgencias/{id}`  
**Autenticación:** Requerida  
**Autoridad requerida:** `ENFERMERO`

**Path Parameters:**
- `id` (string): ID del ingreso a eliminar

**Response:** `200 OK` (sin body)

**Errores:**
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad ENFERMERO
- `404 Not Found`: Ingreso no encontrado

---

## 5. Cola de Atención (`/api/cola-atencion`)

### 5.1. Obtener Cola de Atención
Obtiene la cola de atención ordenada por prioridad (nivel de emergencia y fecha de ingreso).

**Endpoint:** `GET /api/cola-atencion`  
**Autenticación:** Requerida (cualquier usuario)

**Response:** `200 OK`
```json
[
  {
    "id": "uuid-del-ingreso",
    "paciente": {
      "id": "uuid-del-paciente",
      "cuil": "20-20304050-5",
      "nombre": "Juan",
      "apellido": "Pérez"
    },
    "enfermero": {
      "cuil": "27-12345678-9",
      "nombre": "María",
      "apellido": "González"
    },
    "descripcion": "Dolor de pecho intenso",
    "fechaHoraIngreso": "2024-01-15T10:30:00",
    "temperatura": 37.5,
    "tensionArterial": {
      "sistolica": 120,
      "diastolica": 80
    },
    "frecuenciaCardiaca": 85,
    "frecuenciaRespiratoria": 18,
    "nivelEmergencia": "CRITICA",
    "estado": "EN_ESPERA"
  }
]
```

**Orden de prioridad:**
1. Nivel de emergencia (CRITICA > ALTA > MEDIA > BAJA)
2. Fecha y hora de ingreso (más antiguo primero)

**Errores:**
- `401 Unauthorized`: Token inválido o ausente

---

### 5.2. Ver Siguiente Paciente
Obtiene el siguiente paciente en la cola sin removerlo.

**Endpoint:** `GET /api/cola-atencion/siguiente`  
**Autenticación:** Requerida (cualquier usuario)

**Response:** `200 OK`
```json
{
  "id": "uuid-del-ingreso",
  "paciente": {
    "id": "uuid-del-paciente",
    "cuil": "20-20304050-5",
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "enfermero": {
    "cuil": "27-12345678-9",
    "nombre": "María",
    "apellido": "González"
  },
  "descripcion": "Dolor de pecho intenso",
  "fechaHoraIngreso": "2024-01-15T10:30:00",
  "temperatura": 37.5,
  "tensionArterial": {
    "sistolica": 120,
    "diastolica": 80
  },
  "frecuenciaCardiaca": 85,
  "frecuenciaRespiratoria": 18,
  "nivelEmergencia": "CRITICA",
  "estado": "EN_ESPERA"
}
```

**Errores:**
- `204 No Content`: No hay pacientes en espera
- `401 Unauthorized`: Token inválido o ausente

---

### 5.3. Atender Siguiente Paciente
Atiende al siguiente paciente en la cola (lo remueve de la cola pero no lo elimina del repositorio).

**Endpoint:** `POST /api/cola-atencion/atender`  
**Autenticación:** Requerida  
**Autoridad requerida:** `MEDICO`

**Response:** `200 OK`
```json
{
  "id": "uuid-del-ingreso",
  "paciente": {
    "id": "uuid-del-paciente",
    "cuil": "20-20304050-5",
    "nombre": "Juan",
    "apellido": "Pérez"
  },
  "enfermero": {
    "cuil": "27-12345678-9",
    "nombre": "María",
    "apellido": "González"
  },
  "descripcion": "Dolor de pecho intenso",
  "fechaHoraIngreso": "2024-01-15T10:30:00",
  "temperatura": 37.5,
  "tensionArterial": {
    "sistolica": 120,
    "diastolica": 80
  },
  "frecuenciaCardiaca": 85,
  "frecuenciaRespiratoria": 18,
  "nivelEmergencia": "CRITICA",
  "estado": "EN_ATENCION"
}
```

**Errores:**
- `204 No Content`: No hay pacientes en espera
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad MEDICO

---

### 5.4. Cantidad de Pacientes en Espera
Obtiene la cantidad de pacientes que están en espera en la cola.

**Endpoint:** `GET /api/cola-atencion/cantidad`  
**Autenticación:** Requerida (cualquier usuario)

**Response:** `200 OK`
```json
5
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente

---

## Códigos de Estado HTTP

- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Operación exitosa sin contenido
- `400 Bad Request`: Datos inválidos en la solicitud
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene permisos suficientes
- `404 Not Found`: Recurso no encontrado
- `500 Internal Server Error`: Error interno del servidor

---

## Enums

### Autoridad
- `MEDICO`
- `ENFERMERO`

### NivelEmergencia
- `BAJA`
- `MEDIA`
- `ALTA`
- `CRITICA`

### Estado
- `EN_ESPERA`
- `EN_ATENCION`
- `ATENDIDO`

---

## Ejemplos de Uso

### Flujo completo: Registrar paciente e ingreso

1. **Login como enfermero:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"enfermero@hospital.com","password":"Enfermero123!"}'
```

2. **Listar obras sociales disponibles:**
```bash
curl -X GET http://localhost:8080/api/obras-sociales \
  -H "Authorization: Bearer <token>"
```

3. **Registrar paciente:**
```bash
curl -X POST http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer <token>" \
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
```

4. **Registrar paciente con obra social (se verifica automáticamente la afiliación):**
```bash
curl -X POST http://localhost:8080/api/pacientes \
  -H "Authorization: Bearer <token>" \
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
        "id": 1,
        "nombre": "OSDE"
      },
      "numeroAfiliado": "OSDE001234"
    }
  }'
```

5. **Registrar ingreso:**
```bash
curl -X POST http://localhost:8080/api/urgencias \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "pacienteCuil": "20-20304050-5",
    "enfermeroCuil": "27-12345678-9",
    "descripcion": "Dolor de pecho intenso",
    "temperatura": 37.5,
    "tensionSistolica": 120,
    "tensionDiastolica": 80,
    "frecuenciaCardiaca": 85,
    "frecuenciaRespiratoria": 18,
    "nivelEmergencia": "ALTA"
  }'
```

6. **Actualizar paciente (como enfermero):**
```bash
curl -X PUT http://localhost:8080/api/pacientes/20-20304050-5 \
  -H "Authorization: Bearer <token-enfermero>" \
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
```

7. **Eliminar paciente (como enfermero):**
```bash
curl -X DELETE http://localhost:8080/api/pacientes/20-20304050-5 \
  -H "Authorization: Bearer <token-enfermero>"
```

8. **Ver cola de atención (como médico):**
```bash
curl -X GET http://localhost:8080/api/cola-atencion \
  -H "Authorization: Bearer <token-medico>"
```

9. **Atender siguiente paciente:**
```bash
curl -X POST http://localhost:8080/api/cola-atencion/atender \
  -H "Authorization: Bearer <token-medico>"
```

---

## Notas

- Todos los timestamps están en formato ISO 8601 (UTC)
- Los CUIL deben seguir el formato: `XX-XXXXXXXX-X`
- Las temperaturas se manejan en grados Celsius
- Las frecuencias se expresan en unidades por minuto
- La paginación usa índices basados en 0 (primera página = 0)

## Integración con API Externa de Obras Sociales

El sistema se integra con una API externa de obras sociales para:
- **Listar obras sociales disponibles**: El endpoint `/api/obras-sociales` consulta la API externa para obtener todas las obras sociales disponibles.
- **Verificar afiliación**: Al registrar o actualizar un paciente con obra social, el sistema verifica automáticamente que el paciente esté afiliado a la obra social especificada mediante su número de afiliado.

**Configuración:**
La URL de la API externa se configura en `application.properties`:
```properties
obras-sociales.api.url=http://localhost:8001
```

**Comportamiento:**
- Si la API externa no está disponible, los endpoints que la requieren retornarán un error `400 Bad Request` con un mensaje descriptivo.
- La verificación de afiliación es **obligatoria** cuando se especifica una obra social. Si el paciente no está afiliado, el registro/actualización fallará.
- Los timeouts de conexión están configurados en 5 segundos para conexión y 10 segundos para lectura.
