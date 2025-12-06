# Backend - Sistema de Gestión de Urgencias

Backend desarrollado en Spring Boot para el sistema de gestión de urgencias hospitalarias.

## 📚 Documentación de la API

**La documentación completa de todos los endpoints está disponible en:**
- **[API.md](./API.md)](./API.md)

## 🚀 Inicio Rápido

### Requisitos
- Java 17 o superior
- Maven 3.6+
- PostgreSQL (opcional, también soporta repositorio en memoria)

### Configuración

1. **Clonar el repositorio y navegar al directorio backend:**
```bash
cd backend
```

2. **Configurar la base de datos (opcional):**
```bash
# Iniciar PostgreSQL con Docker Compose
docker-compose up -d

# Inicializar la base de datos
./scripts/init-db.sh

# Poblar con datos de prueba (opcional)
./scripts/populate-db.sh
```

3. **Ejecutar la aplicación:**
```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en `http://localhost:8080`

## 📋 Endpoints Disponibles

### Autenticación (`/api/auth`)
- `POST /api/auth/registro` - Registrar nuevo usuario
- `POST /api/auth/login` - Iniciar sesión
- `GET /api/auth/perfil` - Obtener perfil del usuario
- `GET /api/auth/verificar` - Verificar autenticación

### Pacientes (`/api/pacientes`)
- `POST /api/pacientes` - Crear paciente (requiere ENFERMERO)
- `GET /api/pacientes` - Listar pacientes con paginación
- `GET /api/pacientes/{cuil}` - Buscar paciente por CUIL

### Urgencias (`/api/urgencias`)
- `POST /api/urgencias` - Registrar ingreso (requiere ENFERMERO)
- `GET /api/urgencias` - Obtener todos los ingresos
- `GET /api/urgencias/{id}` - Obtener ingreso por ID
- `PUT /api/urgencias/{id}` - Actualizar ingreso (requiere ENFERMERO)
- `DELETE /api/urgencias/{id}` - Eliminar ingreso (requiere ENFERMERO)

### Cola de Atención (`/api/cola-atencion`)
- `GET /api/cola-atencion` - Obtener cola ordenada por prioridad
- `GET /api/cola-atencion/siguiente` - Ver siguiente paciente sin removerlo
- `POST /api/cola-atencion/atender` - Atender siguiente paciente (requiere MEDICO)
- `GET /api/cola-atencion/cantidad` - Cantidad de pacientes en espera

**Para detalles completos, ejemplos y validaciones, consulta [API.md](./API.md)**

## 🧪 Scripts de Prueba

El directorio `scripts/` contiene varios scripts útiles:

- `init-db.sh` - Inicializa la base de datos PostgreSQL
- `populate-db.sh` - Pobla la base de datos con 100 pacientes de prueba
- `script_setup_completo.sh` - Script completo de configuración inicial
- `test_listar_pacientes.sh` - Prueba el endpoint de listado de pacientes
- `test_registrar_ingreso.sh` - Prueba el endpoint de registro de ingresos

## 🏗️ Arquitectura

El proyecto sigue una arquitectura limpia (Clean Architecture) con las siguientes capas:

- **Domain**: Entidades, value objects, enums y repositorios (interfaces)
- **Application**: DTOs, servicios y mappers
- **Infrastructure**: Implementaciones de repositorios (PostgreSQL y memoria)
- **Controller**: Endpoints REST

## 🔐 Autenticación

La API utiliza JWT (JSON Web Tokens) para autenticación. Los tokens se obtienen mediante login y deben incluirse en el header:

```
Authorization: Bearer <token>
```

## 📦 Dependencias Principales

- Spring Boot 3.x
- Spring Security
- JWT (JSON Web Tokens)
- PostgreSQL Driver
- Maven

## 🔧 Configuración

La configuración se encuentra en `src/main/resources/application.properties`. Se pueden configurar:

- Puerto de la aplicación
- Configuración de base de datos
- Configuración de JWT (secret, expiración)
- Perfiles de Spring (postgres, memory)

## 📝 Notas

- Por defecto, la aplicación usa el perfil "memory" (repositorio en memoria)
- Para usar PostgreSQL, activa el perfil "postgres" y configura la conexión
- Los tokens JWT expiran después de 24 horas por defecto
