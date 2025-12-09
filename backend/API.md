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

## 4. Ingresos (`/api/ingresos`)

### 4.1. Registrar Ingreso
Registra un nuevo ingreso a urgencias. Si el paciente no existe, se crea automáticamente.

**Endpoint:** `POST /api/ingresos`  
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

**Endpoint:** `GET /api/ingresos`  
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
    "estado": "PENDIENTE"
  }
]
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente

---

### 4.3. Obtener Ingreso por ID
Obtiene la información de un ingreso específico por su ID.

**Endpoint:** `GET /api/ingresos/{id}`  
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
  "estado": "PENDIENTE"
}
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente
- `404 Not Found`: Ingreso no encontrado

---

### 4.4. Actualizar Ingreso
Actualiza la información de un ingreso existente.

**Endpoint:** `PUT /api/ingresos/{id}`  
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
  "estado": "PENDIENTE"
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

**Endpoint:** `DELETE /api/ingresos/{id}`  
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
    "estado": "PENDIENTE"
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
  "estado": "PENDIENTE"
}
```

**Errores:**
- `204 No Content`: No hay pacientes en espera
- `401 Unauthorized`: Token inválido o ausente

---

### 5.3. Atender Siguiente Paciente
Atiende al siguiente paciente en la cola (lo remueve de la cola y cambia su estado a EN_PROCESO).

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
  "estado": "EN_PROCESO"
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

## 6. Atenciones (`/api/atenciones`)

### 6.1. Registrar Atención Médica
Registra una nueva atención médica para un ingreso. El médico debe haber reclamado previamente el paciente (estado EN_PROCESO).

**Endpoint:** `POST /api/atenciones`  
**Autenticación:** Requerida  
**Autoridad requerida:** `MEDICO`

**Request Body:**
```json
{
  "ingresoId": "uuid-del-ingreso",
  "informe": "Paciente presenta cuadro de dolor torácico. Se realizaron estudios complementarios (ECG, análisis de sangre). Diagnóstico: angina de pecho. Se indica tratamiento farmacológico y reposo."
}
```

**Campos:**
- `ingresoId` (string, requerido): ID del ingreso que se está atendiendo
- `informe` (string, requerido): Informe médico detallado de la atención

**Validaciones:**
- El informe no puede estar vacío
- El ingreso debe existir
- El ingreso debe estar en estado `EN_PROCESO`
- No debe existir una atención previa para ese ingreso

**Response:** `201 Created`
```json
{
  "id": "uuid-de-la-atencion",
  "ingresoId": "uuid-del-ingreso",
  "medicoId": "uuid-del-medico",
  "informe": "Paciente presenta cuadro de dolor torácico...",
  "fechaHoraAtencion": "2024-01-15T11:00:00"
}
```

**Efectos:**
- Se crea el registro de atención
- El estado del ingreso cambia automáticamente a `FINALIZADO`
- El ingreso queda asociado a la atención

**Errores:**
- `400 Bad Request`: Informe vacío, ingreso no está en estado EN_PROCESO, o ya existe una atención para ese ingreso
- `401 Unauthorized`: Token inválido o ausente
- `403 Forbidden`: Usuario no tiene autoridad MEDICO
- `404 Not Found`: Ingreso no encontrado

---

### 6.2. Obtener Atención por ID de Ingreso
Obtiene la atención médica asociada a un ingreso específico.

**Endpoint:** `GET /api/atenciones/ingreso/{ingresoId}`  
**Autenticación:** Requerida (cualquier usuario)

**Path Parameters:**
- `ingresoId` (string): ID del ingreso

**Ejemplo:**
```
GET /api/atenciones/ingreso/123e4567-e89b-12d3-a456-426614174000
```

**Response:** `200 OK`
```json
{
  "id": "uuid-de-la-atencion",
  "ingresoId": "uuid-del-ingreso",
  "medicoId": "uuid-del-medico",
  "informe": "Paciente presenta cuadro de dolor torácico...",
  "fechaHoraAtencion": "2024-01-15T11:00:00"
}
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente
- `404 Not Found`: No existe atención para ese ingreso

---

### 6.3. Obtener Atención por ID
Obtiene una atención médica por su identificador único.

**Endpoint:** `GET /api/atenciones/{id}`  
**Autenticación:** Requerida (cualquier usuario)

**Path Parameters:**
- `id` (string): ID de la atención

**Ejemplo:**
```
GET /api/atenciones/123e4567-e89b-12d3-a456-426614174000
```

**Response:** `200 OK`
```json
{
  "id": "uuid-de-la-atencion",
  "ingresoId": "uuid-del-ingreso",
  "medicoId": "uuid-del-medico",
  "informe": "Paciente presenta cuadro de dolor torácico...",
  "fechaHoraAtencion": "2024-01-15T11:00:00"
}
```

**Errores:**
- `401 Unauthorized`: Token inválido o ausente
- `404 Not Found`: Atención no encontrada

---

## 7. Obras Sociales (`/api/obras-sociales`)

### 7.1. Listar Obras Sociales
Obtiene la lista de todas las obras sociales disponibles desde la API externa.

**Endpoint:** `GET /api/obras-sociales`  
**Autenticación:** Requerida (cualquier usuario)

**Response:** `200 OK`
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
  }
]
```

**Errores:**
- `400 Bad Request`: Error al comunicarse con la API externa de obras sociales
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
- `PENDIENTE` - Ingreso registrado, esperando en cola de atención
- `EN_PROCESO` - Paciente siendo atendido por un médico
- `FINALIZADO` - Atención completada con informe médico

---

## Ejemplos de Uso

### Flujo completo: De ingreso a atención finalizada

#### 1. Login como enfermero
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"enfermero@hospital.com","password":"Enfermero123!"}'
```

#### 2. Listar obras sociales disponibles
```bash
curl -X GET http://localhost:8080/api/obras-sociales \
  -H "Authorization: Bearer <token>"
```

#### 3. Registrar paciente con obra social
El sistema verifica automáticamente la afiliación:
```bash
curl -X POST http://localhost:8080/api/pacientes \
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

#### 4. Registrar ingreso a urgencias
El ingreso se crea en estado PENDIENTE y se agrega automáticamente a la cola:
```bash
curl -X POST http://localhost:8080/api/ingresos \
  -H "Authorization: Bearer <token-enfermero>" \
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

#### 5. Login como médico
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"medico@hospital.com","password":"Medico123!"}'
```

#### 6. Ver cola de atención ordenada por prioridad
```bash
curl -X GET http://localhost:8080/api/cola-atencion \
  -H "Authorization: Bearer <token-medico>"
```

#### 7. Atender siguiente paciente
El ingreso cambia a estado EN_PROCESO y se remueve de la cola:
```bash
curl -X POST http://localhost:8080/api/cola-atencion/atender \
  -H "Authorization: Bearer <token-medico>"
```

Respuesta (guardar el `id` del ingreso):
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "estado": "EN_PROCESO",
  ...
}
```

#### 8. Registrar atención médica
El ingreso cambia a estado FINALIZADO:
```bash
curl -X POST http://localhost:8080/api/atenciones \
  -H "Authorization: Bearer <token-medico>" \
  -H "Content-Type: application/json" \
  -d '{
    "ingresoId": "550e8400-e29b-41d4-a716-446655440000",
    "informe": "Paciente presenta cuadro de dolor torácico. Se realizaron ECG y análisis de sangre. Diagnóstico: angina de pecho. Se indica tratamiento farmacológico y reposo de 48 horas."
  }'
```

#### 9. Consultar atención registrada
```bash
curl -X GET http://localhost:8080/api/atenciones/ingreso/550e8400-e29b-41d4-a716-446655440000 \
  -H "Authorization: Bearer <token-medico>"
```

---

### Otros ejemplos útiles

#### Actualizar paciente (como enfermero)
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

#### Ver siguiente paciente sin atenderlo
```bash
curl -X GET http://localhost:8080/api/cola-atencion/siguiente \
  -H "Authorization: Bearer <token>"
```

#### Cantidad de pacientes en espera
```bash
curl -X GET http://localhost:8080/api/cola-atencion/cantidad \
  -H "Authorization: Bearer <token>"
```

---

## Flujo de Trabajo del Sistema

### Estados de un Ingreso

```
PENDIENTE → EN_PROCESO → FINALIZADO
```

1. **PENDIENTE**: Ingreso recién registrado por el enfermero
   - El paciente está en la cola de atención
   - Ordenado por prioridad (nivel de emergencia + fecha)

2. **EN_PROCESO**: Médico reclama el paciente de la cola
   - Se remueve de la cola de atención
   - El médico realiza la evaluación y tratamiento

3. **FINALIZADO**: Médico registra la atención con informe
   - Se crea el registro de atención médica
   - El ingreso queda cerrado con su informe

### Diagrama de Flujo

```
┌─────────────────────────────────────────────────────────────────┐
│                        ENFERMERO                                 │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. Registrar Paciente (si no existe)                           │
│     └─> POST /api/pacientes                                     │
│                                                                  │
│  2. Registrar Ingreso                                           │
│     └─> POST /api/ingresos                                      │
│         • Estado inicial: PENDIENTE                             │
│         • Se agrega automáticamente a la cola de atención       │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

                            ↓

┌─────────────────────────────────────────────────────────────────┐
│                    COLA DE ATENCIÓN                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  • Ordenamiento automático por:                                 │
│    1. Nivel de emergencia (CRITICA > ALTA > MEDIA > BAJA)       │
│    2. Fecha de ingreso (FIFO dentro del mismo nivel)            │
│                                                                  │
│  • Cualquier usuario puede consultar:                           │
│    └─> GET /api/cola-atencion                                   │
│    └─> GET /api/cola-atencion/siguiente                         │
│    └─> GET /api/cola-atencion/cantidad                          │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘

                            ↓

┌─────────────────────────────────────────────────────────────────┐
│                         MÉDICO                                   │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  3. Reclamar siguiente paciente                                 │
│     └─> POST /api/cola-atencion/atender                         │
│         • Remueve el ingreso de la cola                         │
│         • Estado cambia: PENDIENTE → EN_PROCESO                 │
│                                                                  │
│  4. Realizar evaluación y tratamiento                           │
│                                                                  │
│  5. Registrar atención con informe                              │
│     └─> POST /api/atenciones                                    │
│         • Se crea el registro de atención                       │
│         • Estado cambia: EN_PROCESO → FINALIZADO                │
│                                                                  │
│  6. Consultar atenciones (opcional)                             │
│     └─> GET /api/atenciones/{id}                                │
│     └─> GET /api/atenciones/ingreso/{ingresoId}                 │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Notas

### General
- Todos los timestamps están en formato ISO 8601 (UTC)
- Los CUIL deben seguir el formato: `XX-XXXXXXXX-X`
- Las temperaturas se manejan en grados Celsius
- Las frecuencias se expresan en unidades por minuto
- La paginación usa índices basados en 0 (primera página = 0)

### Seguridad
- Los tokens JWT expiran después de 24 horas (configurable)
- Las contraseñas se hashean con BCrypt
- Los endpoints protegidos requieren el header `Authorization: Bearer <token>`

### Autoridades y Permisos
- **MEDICO**: Puede atender pacientes y registrar atenciones
- **ENFERMERO**: Puede registrar pacientes e ingresos, y modificar datos de pacientes

### Validaciones Importantes
- El informe médico es **obligatorio** al registrar una atención
- Solo se pueden atender ingresos en estado **EN_PROCESO**
- No se puede registrar más de una atención por ingreso
- Al atender un paciente, automáticamente cambia de PENDIENTE a EN_PROCESO
- Al registrar una atención, automáticamente cambia de EN_PROCESO a FINALIZADO

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
