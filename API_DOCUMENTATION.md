# Documentación de API - Sistema de Gestión de Urgencias

## Tabla de Contenidos
1. [Autenticación](#autenticación)
2. [Pacientes](#pacientes)
3. [Ingresos](#ingresos)
4. [Cola de Atención](#cola-de-atención)
5. [Atenciones](#atenciones)
6. [Obras Sociales](#obras-sociales)
7. [Tipos de Datos](#tipos-de-datos)

---

## Base URL
```
http://localhost:8080/api
```

## Autenticación

Todos los endpoints protegidos requieren un token JWT en el header:
```
Authorization: Bearer <token>
```

### 1. Registrar Usuario

**Endpoint:** `POST /auth/registro`

**Autenticación:** No requerida (público)

**Request Body:**
```json
{
  "email": "medico@hospital.com",
  "password": "password123",
  "autoridad": "MEDICO",
  "cuil": "20-12345678-9",
  "nombre": "Juan",
  "apellido": "Pérez",
  "matricula": "MP12345"
}
```

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "nombre": "Juan",
  "apellido": "Pérez",
  "autoridad": "MEDICO"
}
```

**Valores posibles para `autoridad`:** `MEDICO`, `ENFERMERO`

---

### 2. Iniciar Sesión

**Endpoint:** `POST /auth/login`

**Autenticación:** No requerida (público)

**Request Body:**
```json
{
  "email": "medico@hospital.com",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "medico@hospital.com",
  "autoridad": "MEDICO",
  "expiresIn": 3600000
}
```

---

### 3. Obtener Perfil del Usuario

**Endpoint:** `GET /auth/perfil`

**Autenticación:** Requerida

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "email": "medico@hospital.com",
  "nombre": "Juan",
  "apellido": "Pérez",
  "cuil": "20-12345678-9",
  "matricula": "MP12345",
  "autoridad": "MEDICO"
}
```

---

### 4. Verificar Autenticación

**Endpoint:** `GET /auth/verificar`

**Autenticación:** Requerida

**Response:** `200 OK`
```json
"Token válido. Usuario autenticado: medico@hospital.com con autoridad: MEDICO"
```

---

## Pacientes

### 5. Crear Paciente

**Endpoint:** `POST /pacientes`

**Autenticación:** Requerida (ENFERMERO)

**Request Body:**
```json
{
  "cuil": "20-20304050-5",
  "apellido": "García",
  "nombre": "María",
  "domicilio": {
    "calle": "Av. Libertador",
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

**Nota:** El campo `obraSocial` es opcional. Si el paciente no tiene obra social, puede omitirse.

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "cuil": "20-20304050-5",
  "nombre": "María",
  "apellido": "García",
  "domicilio": {
    "calle": "Av. Libertador",
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

---

### 6. Listar Pacientes (Paginado)

**Endpoint:** `GET /pacientes?page=0&size=10&sortBy=cuil&direction=ASC`

**Autenticación:** Requerida

**Query Parameters:**
- `page` (opcional, default: 0): Número de página (0-indexed)
- `size` (opcional, default: 10): Tamaño de página
- `sortBy` (opcional, default: "cuil"): Campo para ordenar
- `direction` (opcional, default: "ASC"): Dirección del ordenamiento (ASC/DESC)

**Response:** `200 OK`
```json
{
  "content": [
    {
      "id": "uuid",
      "cuil": "20-20304050-5",
      "nombre": "María",
      "apellido": "García",
      "domicilio": {
        "calle": "Av. Libertador",
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
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 1,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 1,
  "empty": false
}
```

---

### 7. Buscar Paciente por CUIL

**Endpoint:** `GET /pacientes/{cuil}`

**Autenticación:** Requerida

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "cuil": "20-20304050-5",
  "nombre": "María",
  "apellido": "García",
  "domicilio": {
    "calle": "Av. Libertador",
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

**Response:** `404 Not Found` (si no existe el paciente)

---

### 8. Actualizar Paciente

**Endpoint:** `PUT /pacientes/{cuil}`

**Autenticación:** Requerida (ENFERMERO)

**Request Body:** Mismo formato que crear paciente

**Response:** `200 OK` (mismo formato que la respuesta de creación)

---

### 9. Eliminar Paciente

**Endpoint:** `DELETE /pacientes/{cuil}`

**Autenticación:** Requerida (ENFERMERO)

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "cuil": "20-20304050-5",
  "nombre": "María",
  "apellido": "García",
  "domicilio": {
    "calle": "Av. Libertador",
    "numero": 1234,
    "localidad": "Buenos Aires"
  },
  "obraSocial": null
}
```

---

## Ingresos

### 10. Registrar Ingreso

**Endpoint:** `POST /ingresos`

**Autenticación:** Requerida (ENFERMERO)

**Request Body:**
```json
{
  "pacienteCuil": "20-20304050-5",
  "enfermeroCuil": "27-12345678-9",
  "descripcion": "Dolor abdominal agudo",
  "temperatura": 38.5,
  "tensionSistolica": 140,
  "tensionDiastolica": 90,
  "frecuenciaCardiaca": 95,
  "frecuenciaRespiratoria": 22,
  "nivelEmergencia": "URGENCIA"
}
```

**Campos opcionales adicionales:**
- `pacienteNombre`: Para crear paciente si no existe
- `pacienteApellido`: Para crear paciente si no existe
- `pacienteEmail`: Para crear paciente si no existe
- `pacienteDomicilio`: Para crear paciente si no existe
- `pacienteObraSocial`: Para crear paciente si no existe

**Valores posibles para `nivelEmergencia`:**
- `CRITICA` (prioridad 5)
- `EMERGENCIA` (prioridad 4)
- `URGENCIA` (prioridad 3)
- `URGENCIA_MENOR` (prioridad 2)
- `SIN_URGENCIA` (prioridad 1)

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "pacienteCuil": "20-20304050-5",
  "pacienteNombre": "María",
  "pacienteApellido": "García",
  "enfermeroCuil": "27-12345678-9",
  "enfermeroMatricula": "MP12345",
  "descripcion": "Dolor abdominal agudo",
  "fechaHoraIngreso": "2025-12-08T14:30:00",
  "temperatura": 38.5,
  "tensionSistolica": 140,
  "tensionDiastolica": 90,
  "frecuenciaCardiaca": 95,
  "frecuenciaRespiratoria": 22,
  "nivelEmergencia": "URGENCIA",
  "estado": "PENDIENTE"
}
```

---

### 11. Listar Todos los Ingresos

**Endpoint:** `GET /ingresos`

**Autenticación:** Requerida

**Response:** `200 OK`
```json
[
  {
    "id": "uuid",
    "pacienteCuil": "20-20304050-5",
    "pacienteNombre": "María",
    "pacienteApellido": "García",
    "enfermeroCuil": "27-12345678-9",
    "enfermeroMatricula": "MP12345",
    "descripcion": "Dolor abdominal agudo",
    "fechaHoraIngreso": "2025-12-08T14:30:00",
    "temperatura": 38.5,
    "tensionSistolica": 140,
    "tensionDiastolica": 90,
    "frecuenciaCardiaca": 95,
    "frecuenciaRespiratoria": 22,
    "nivelEmergencia": "URGENCIA",
    "estado": "PENDIENTE"
  }
]
```

---

### 12. Obtener Ingreso por ID

**Endpoint:** `GET /ingresos/{id}`

**Autenticación:** Requerida

**Response:** `200 OK` (mismo formato que un elemento de la lista)

**Response:** `404 Not Found` (si no existe el ingreso)

---

### 13. Actualizar Ingreso

**Endpoint:** `PUT /ingresos/{id}`

**Autenticación:** Requerida (ENFERMERO)

**Request Body:** Mismo formato que registrar ingreso

**Response:** `200 OK` (mismo formato que la respuesta de registro)

---

### 14. Eliminar Ingreso

**Endpoint:** `DELETE /ingresos/{id}`

**Autenticación:** Requerida (ENFERMERO)

**Response:** `200 OK` (sin contenido)

---

## Cola de Atención

### 15. Obtener Cola de Atención

**Endpoint:** `GET /cola-atencion`

**Autenticación:** Requerida

**Descripción:** Devuelve todos los ingresos en estado PENDIENTE ordenados por prioridad (nivel de emergencia) de mayor a menor.

**Response:** `200 OK`
```json
[
  {
    "id": "uuid-1",
    "pacienteCuil": "20-20304050-5",
    "pacienteNombre": "María",
    "pacienteApellido": "García",
    "enfermeroCuil": "27-12345678-9",
    "enfermeroMatricula": "MP12345",
    "descripcion": "Dolor de pecho",
    "fechaHoraIngreso": "2025-12-08T14:30:00",
    "temperatura": 37.2,
    "tensionSistolica": 160,
    "tensionDiastolica": 100,
    "frecuenciaCardiaca": 110,
    "frecuenciaRespiratoria": 28,
    "nivelEmergencia": "CRITICA",
    "estado": "PENDIENTE"
  },
  {
    "id": "uuid-2",
    "pacienteCuil": "20-30405060-7",
    "pacienteNombre": "Carlos",
    "pacienteApellido": "López",
    "enfermeroCuil": "27-12345678-9",
    "enfermeroMatricula": "MP12345",
    "descripcion": "Dolor abdominal",
    "fechaHoraIngreso": "2025-12-08T14:45:00",
    "temperatura": 38.5,
    "tensionSistolica": 140,
    "tensionDiastolica": 90,
    "frecuenciaCardiaca": 95,
    "frecuenciaRespiratoria": 22,
    "nivelEmergencia": "URGENCIA",
    "estado": "PENDIENTE"
  }
]
```

---

### 16. Ver Siguiente Paciente

**Endpoint:** `GET /cola-atencion/siguiente`

**Autenticación:** Requerida

**Descripción:** Muestra el siguiente paciente a atender sin removerlo de la cola.

**Response:** `200 OK` (formato de un ingreso)

**Response:** `204 No Content` (si no hay pacientes en espera)

---

### 17. Atender Siguiente Paciente

**Endpoint:** `POST /cola-atencion/atender`

**Autenticación:** Requerida (MEDICO)

**Descripción:** Remueve el siguiente paciente de la cola y cambia su estado a EN_PROCESO. El médico debe usar este endpoint para reclamar un paciente antes de atenderlo.

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "pacienteCuil": "20-20304050-5",
  "pacienteNombre": "María",
  "pacienteApellido": "García",
  "enfermeroCuil": "27-12345678-9",
  "enfermeroMatricula": "MP12345",
  "descripcion": "Dolor de pecho",
  "fechaHoraIngreso": "2025-12-08T14:30:00",
  "temperatura": 37.2,
  "tensionSistolica": 160,
  "tensionDiastolica": 100,
  "frecuenciaCardiaca": 110,
  "frecuenciaRespiratoria": 28,
  "nivelEmergencia": "CRITICA",
  "estado": "EN_PROCESO"
}
```

**Response:** `400 Bad Request` (si no hay pacientes en espera)

---

### 18. Cantidad de Pacientes en Espera

**Endpoint:** `GET /cola-atencion/cantidad`

**Autenticación:** Requerida

**Response:** `200 OK`
```json
5
```

---

## Atenciones

### 19. Registrar Atención

**Endpoint:** `POST /atenciones`

**Autenticación:** Requerida (MEDICO)

**Descripción:** Registra la atención médica de un ingreso. El ingreso debe estar en estado EN_PROCESO. Al registrar la atención, el estado del ingreso cambia a FINALIZADO.

**Request Body:**
```json
{
  "ingresoId": "uuid",
  "informe": "Paciente presenta cuadro de apendicitis aguda. Se recomienda intervención quirúrgica inmediata. Se administró analgesia y se preparó para cirugía."
}
```

**Response:** `201 Created`
```json
{
  "id": "uuid",
  "ingresoId": "uuid",
  "medicoId": "uuid",
  "informeMedico": "Paciente presenta cuadro de apendicitis aguda...",
  "fechaAtencion": "2025-12-08T15:00:00"
}
```

**Validaciones:**
- El informe no puede estar vacío
- El ingreso debe existir
- El ingreso debe estar en estado EN_PROCESO
- Solo usuarios con autoridad MEDICO pueden registrar atenciones

---

### 20. Obtener Atención por ID de Ingreso

**Endpoint:** `GET /atenciones/ingreso/{ingresoId}`

**Autenticación:** Requerida

**Response:** `200 OK`
```json
{
  "id": "uuid",
  "ingresoId": "uuid",
  "medicoId": "uuid",
  "informeMedico": "Paciente presenta cuadro de apendicitis aguda...",
  "fechaAtencion": "2025-12-08T15:00:00"
}
```

**Response:** `404 Not Found` (si no existe atención para ese ingreso)

---

### 21. Obtener Atención por ID

**Endpoint:** `GET /atenciones/{id}`

**Autenticación:** Requerida

**Response:** `200 OK` (mismo formato que el endpoint anterior)

**Response:** `404 Not Found` (si no existe la atención)

---

## Obras Sociales

### 22. Listar Obras Sociales

**Endpoint:** `GET /obras-sociales`

**Autenticación:** Requerida

**Descripción:** Devuelve la lista de obras sociales disponibles desde la API externa.

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

**Response:** `400 Bad Request` (si hay un error al consultar la API externa)

---

## Tipos de Datos

### Estados de Ingreso (Estado)
- `PENDIENTE`: Ingreso registrado, esperando atención
- `EN_PROCESO`: Ingreso siendo atendido por un médico
- `FINALIZADO`: Ingreso completado con atención registrada

### Niveles de Emergencia (NivelEmergencia)
- `CRITICA`: Prioridad 5 (más urgente)
- `EMERGENCIA`: Prioridad 4
- `URGENCIA`: Prioridad 3
- `URGENCIA_MENOR`: Prioridad 2
- `SIN_URGENCIA`: Prioridad 1 (menos urgente)

### Autoridades/Roles (Autoridad)
- `MEDICO`: Personal médico autorizado a atender pacientes y registrar atenciones
- `ENFERMERO`: Personal de enfermería autorizado a registrar ingresos y gestionar pacientes

---

## Códigos de Estado HTTP

### Respuestas Exitosas
- `200 OK`: Operación exitosa
- `201 Created`: Recurso creado exitosamente
- `204 No Content`: Operación exitosa sin contenido (ej: cola vacía)

### Errores del Cliente
- `400 Bad Request`: Datos inválidos o error de validación
- `401 Unauthorized`: Token faltante o inválido
- `403 Forbidden`: Usuario sin permisos suficientes
- `404 Not Found`: Recurso no encontrado

### Errores del Servidor
- `500 Internal Server Error`: Error interno del servidor

---

## Flujo de Trabajo Típico

### 1. Flujo de Enfermería (Registro de Ingreso)

1. **Autenticar** enfermero: `POST /auth/login`
2. **Buscar o crear** paciente:
   - Buscar: `GET /pacientes/{cuil}`
   - Si no existe, crear: `POST /pacientes`
3. **Listar obras sociales** (si es necesario): `GET /obras-sociales`
4. **Registrar ingreso**: `POST /ingresos`

### 2. Flujo Médico (Atención de Pacientes)

1. **Autenticar** médico: `POST /auth/login`
2. **Ver cola de atención**: `GET /cola-atencion`
3. **Ver siguiente paciente**: `GET /cola-atencion/siguiente` (opcional)
4. **Reclamar paciente**: `POST /cola-atencion/atender`
5. **Consultar detalles del ingreso**: `GET /ingresos/{id}`
6. **Consultar datos del paciente**: `GET /pacientes/{cuil}`
7. **Registrar atención**: `POST /atenciones`

### 3. Flujo de Consulta

1. **Autenticar** usuario: `POST /auth/login`
2. **Consultar todos los ingresos**: `GET /ingresos`
3. **Ver atención de un ingreso**: `GET /atenciones/ingreso/{ingresoId}`

---

## Notas Importantes

1. **Tokens JWT**: Todos los tokens tienen un tiempo de expiración. Implementar lógica de refresh o re-login cuando expire.

2. **Fechas**: Todas las fechas se devuelven en formato ISO 8601: `YYYY-MM-DDTHH:mm:ss`

3. **CUIL**: El formato del CUIL debe ser: `XX-XXXXXXXX-X` (con guiones)

4. **Signos Vitales**: 
   - Temperatura en grados Celsius (°C)
   - Tensión arterial en mmHg
   - Frecuencia cardíaca en latidos por minuto
   - Frecuencia respiratoria en respiraciones por minuto

5. **Permisos**:
   - Solo ENFERMERO puede: crear/actualizar/eliminar pacientes e ingresos
   - Solo MEDICO puede: atender pacientes y registrar atenciones
   - Ambos roles pueden: consultar datos

6. **Cola de Atención**: Se ordena automáticamente por nivel de emergencia (prioridad). En caso de igual prioridad, se ordena por fecha de ingreso (FIFO).

7. **Estado de Ingresos**: El flujo de estados es unidireccional: PENDIENTE → EN_PROCESO → FINALIZADO


