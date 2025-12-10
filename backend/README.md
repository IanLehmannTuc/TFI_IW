# 🏥 Sistema de Gestión de Urgencias Médicas

Sistema backend para la gestión de ingresos y atención de pacientes en el área de urgencias de un hospital.

---

## 📋 Descripción

Sistema desarrollado en **Spring Boot** que permite:

- 👥 **Gestión de Pacientes**: Registro, actualización y consulta de datos de pacientes
- 🚑 **Registro de Ingresos**: Ingreso de pacientes a urgencias con signos vitales
- 📋 **Cola de Atención**: Cola automatizada por prioridad de emergencia
- 🩺 **Atenciones Médicas**: Registro de diagnósticos e informes médicos
- 🔐 **Autenticación JWT**: Sistema seguro de autenticación y autorización
- 🏢 **Obras Sociales**: Integración con API externa para verificación

---

## 🏗️ Arquitectura

El sistema está desarrollado siguiendo principios de:

- **Clean Architecture** (Arquitectura Limpia)
- **Domain-Driven Design (DDD)** (Diseño Orientado al Dominio)
- **SOLID Principles** (Principios SOLID)

### Capas

```
┌─────────────────────────────────────────┐
│         Controllers (API REST)          │ ← Endpoints HTTP
├─────────────────────────────────────────┤
│      Application Services & DTOs        │ ← Orquestación
├─────────────────────────────────────────┤
│    Domain (Entities & Value Objects)    │ ← Lógica de Negocio
├─────────────────────────────────────────┤
│   Infrastructure (Repositories & DB)    │ ← Persistencia
└─────────────────────────────────────────┘
```

---

## 🛠️ Tecnologías

- **Java 22** - Lenguaje de programación
- **Spring Boot 3.x** - Framework backend
- **PostgreSQL** - Base de datos
- **JWT (JSON Web Tokens)** - Autenticación
- **Maven** - Gestor de dependencias
- **JDBC** - Acceso a base de datos (sin ORM)
- **Cucumber** - Tests BDD
- **JUnit 5** - Tests unitarios

---

## 🚀 Quick Start

### Requisitos

- Java 22+
- PostgreSQL 16+ (o usar perfil `memory`)
- Maven 3.6+ (incluido como `./mvnw`)

### Opción 1: Modo Memoria (Sin BD) - RECOMENDADO PARA INICIO RÁPIDO

```bash
# 1. Compilar
./mvnw clean compile

# 2. Levantar el servidor
./mvnw spring-boot:run -Dspring-boot.run.profiles=memory

# 3. La API estará disponible en http://localhost:8080
```

### Opción 2: Modo PostgreSQL

```bash
# 1. Crear base de datos
sudo -u postgres psql
CREATE DATABASE tfi_urgencias;
CREATE USER tfi_user WITH PASSWORD 'tfi_password';
GRANT ALL PRIVILEGES ON DATABASE tfi_urgencias TO tfi_user;
\q

# 2. Ejecutar script de BD
psql -U tfi_user -d tfi_urgencias -f src/main/resources/schema.sql

# 3. Levantar servidor
./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres
```

---

## 📚 Documentación

| Documento | Descripción |
|-----------|-------------|
| [GUIA_PRUEBAS_MANUAL.md](./GUIA_PRUEBAS_MANUAL.md) | Guía completa para probar el sistema manualmente |
| [FLUJOS_PRUEBA.md](./FLUJOS_PRUEBA.md) | Ejemplos de requests y responses para cada endpoint |
| [API_REFERENCE.md](./API_REFERENCE.md) | Referencia técnica completa de la API |
| [ARQUITECTURA.md](./ARQUITECTURA.md) | Documentación de la arquitectura del sistema |

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests (unitarios + integración + BDD)
./mvnw test

# Solo tests unitarios
./mvnw test -Dtest="*Test"

# Solo tests BDD (Cucumber)
./mvnw test -Dtest="RunCucumberTest"
```

### Cobertura de Tests

```
✅ Tests Unitarios: 111 tests
✅ Tests BDD (Cucumber): 11 escenarios
✅ Cobertura: Domain entities, services, repositories
```

---

## 🔐 Seguridad

### Autenticación

- Sistema basado en **JWT (JSON Web Tokens)**
- Token válido por **1 hora**
- Endpoints públicos: `/auth/registro` y `/auth/login`
- Todos los demás endpoints requieren token

### Autorización

| Rol | Permisos |
|-----|----------|
| **ENFERMERO** | Registrar pacientes, ingresos, gestionar cola |
| **MEDICO** | Registrar atenciones médicas, consultar información |

---

## 📊 Modelo de Datos

### Entidades Principales

- **Usuario**: Médicos y Enfermeros
- **Paciente**: Datos personales y médicos
- **Ingreso**: Registro de ingreso a urgencias
- **Atencion**: Diagnóstico y tratamiento médico

### Value Objects

- **Email**: Validación de formato de email
- **Cuil**: Validación de CUIL argentino
- **Temperatura**: Rangos válidos de temperatura
- **TensionArterial**: Validación de presión arterial
- **Telefono**: Validación de formato telefónico

---

## 🌟 Características Destacadas

### ✅ Implementadas

- 🔐 **Autenticación JWT** completa
- 👥 **Gestión de pacientes** con datos completos (teléfono, edad, sexo)
- 📊 **Edad calculada automáticamente** desde fecha de nacimiento
- 🚑 **Cola automática** por prioridad de emergencia
- 📋 **Signos vitales** con validaciones médicas
- 🏢 **Integración API externa** (obras sociales)
- 🎯 **Domain-Driven Design** con entidades ricas
- ✅ **Tests completos** (111 tests pasando)

### 🔄 Flujo de Trabajo

```
1. Enfermero registra ingreso
   ↓
2. Paciente entra a cola (ordenado por prioridad)
   ↓
3. Enfermero llama al siguiente paciente
   ↓
4. Estado cambia a EN_PROCESO
   ↓
5. Médico registra atención
   ↓
6. Estado cambia a FINALIZADO
```

---

## 📂 Estructura del Proyecto

```
backend/
├── src/
│   ├── main/
│   │   ├── java/tfi/
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── application/         # Services & DTOs
│   │   │   │   ├── service/         # Application Services
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   └── mapper/          # Entity ↔ DTO Mappers
│   │   │   ├── domain/              # Domain Layer
│   │   │   │   ├── entity/          # Domain Entities
│   │   │   │   ├── valueObject/     # Value Objects
│   │   │   │   ├── enums/           # Enumerations
│   │   │   │   ├── repository/      # Repository Interfaces
│   │   │   │   └── port/            # Ports (Hexagonal)
│   │   │   ├── infrastructure/      # Infrastructure Layer
│   │   │   │   ├── persistence/     # DB Implementations
│   │   │   │   ├── external/        # External APIs
│   │   │   │   └── security/        # Security Config
│   │   │   ├── exception/           # Custom Exceptions
│   │   │   └── util/                # Utilities
│   │   └── resources/
│   │       ├── application.properties
│   │       └── schema.sql           # Database Schema
│   └── test/
│       ├── java/                    # Unit Tests
│       └── resources/
│           └── moduloUrgencias.feature  # BDD Tests
├── GUIA_PRUEBAS_MANUAL.md          # Testing Guide
├── FLUJOS_PRUEBA.md                # API Examples
├── API_REFERENCE.md                # API Reference
└── README.md                       # This file
```

---

## 🐛 Troubleshooting

### Puerto 8080 ocupado

```bash
# Cambiar puerto en application.properties
server.port=8081
```

### Error al conectar a PostgreSQL

```bash
# Verificar que PostgreSQL está corriendo
sudo systemctl status postgresql

# Verificar credenciales en application-postgres.properties
```

### Tests fallan

```bash
# Limpiar y recompilar
./mvnw clean install -DskipTests
```

---

## 👥 Roles del Sistema

### ENFERMERO
- Registrar pacientes
- Registrar ingresos a urgencias
- Ver cola de atención
- Llamar al siguiente paciente
- Consultar información

### MEDICO
- Registrar atenciones médicas
- Crear informes médicos
- Consultar información

---

## 📈 Estado del Proyecto

```
✅ Compilación: SUCCESS
✅ Tests: 111/111 PASSED
✅ Cobertura: Alta
✅ Arquitectura: Clean Architecture + DDD
✅ Seguridad: JWT implementado
✅ API: RESTful completa
```

---

## 🔜 Próximas Mejoras

- [ ] Swagger/OpenAPI para documentación interactiva
- [ ] Colección de Postman lista para importar
- [ ] Docker Compose para levantar todo el stack
- [ ] Logs estructurados (ELK Stack)
- [ ] Metrics y monitoring (Prometheus + Grafana)
- [ ] Tests de performance (JMeter)

---

## 📝 Convenciones de Código

- **Nombres**: camelCase para métodos, PascalCase para clases
- **Idioma**: Español para dominio, inglés para técnico
- **Comentarios**: Javadoc en métodos públicos
- **Tests**: Nombre descriptivo de lo que prueban
- **Commits**: Mensajes claros y descriptivos

---

## 📞 Soporte

Para iniciar con las pruebas manuales, sigue estos pasos:

1. Lee [GUIA_PRUEBAS_MANUAL.md](./GUIA_PRUEBAS_MANUAL.md)
2. Levanta el servidor en modo memoria
3. Sigue los ejemplos en [FLUJOS_PRUEBA.md](./FLUJOS_PRUEBA.md)
4. Consulta [API_REFERENCE.md](./API_REFERENCE.md) para detalles técnicos

---

## 📜 Licencia

Este proyecto es parte del Trabajo Final Integrador de Ingeniería de Software.

---

## 🎓 Créditos

Desarrollado como Trabajo Final Integrador (TFI) - Ingeniería de Software

**Tecnologías principales:**
- Spring Boot
- PostgreSQL
- JWT
- Clean Architecture
- Domain-Driven Design

**Año:** 2025
