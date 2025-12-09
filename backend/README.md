# Backend - Sistema de Gestión de Urgencias

Backend desarrollado en Spring Boot para el sistema de gestión de urgencias hospitalarias.

## 📚 Documentación de la API

**La documentación completa de todos los endpoints está disponible en:**
- **[API.md](./API.md)**

## 🚀 Inicio Rápido

### Requisitos
- Java 22
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

### Ingresos (`/api/ingresos`)
- `POST /api/ingresos` - Registrar ingreso (requiere ENFERMERO)
- `GET /api/ingresos` - Obtener todos los ingresos
- `GET /api/ingresos/{id}` - Obtener ingreso por ID
- `PUT /api/ingresos/{id}` - Actualizar ingreso (requiere ENFERMERO)
- `DELETE /api/ingresos/{id}` - Eliminar ingreso (requiere ENFERMERO)

### Cola de Atención (`/api/cola-atencion`)
- `GET /api/cola-atencion` - Obtener cola ordenada por prioridad
- `GET /api/cola-atencion/siguiente` - Ver siguiente paciente sin removerlo
- `POST /api/cola-atencion/atender` - Atender siguiente paciente (requiere MEDICO)
- `GET /api/cola-atencion/cantidad` - Cantidad de pacientes en espera

### Atenciones (`/api/atenciones`)
- `POST /api/atenciones` - Registrar atención médica (requiere MEDICO)
- `GET /api/atenciones/{id}` - Obtener atención por ID
- `GET /api/atenciones/ingreso/{ingresoId}` - Obtener atención por ID de ingreso

### Obras Sociales (`/api/obras-sociales`)
- `GET /api/obras-sociales` - Listar obras sociales disponibles

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

- Spring Boot 3.5.5
- Spring JDBC (sin JPA/ORM)
- JWT (JSON Web Tokens) - jjwt 0.12.3
- PostgreSQL Driver
- BCrypt para hasheo de contraseñas
- HikariCP para pool de conexiones
- Cucumber para tests BDD
- JUnit 5 para tests unitarios
- Maven

## 🔧 Configuración

La configuración se encuentra en `src/main/resources/application.properties`. Se pueden configurar:

- Puerto de la aplicación
- Configuración de base de datos
- Configuración de JWT (secret, expiración)
- Perfiles de Spring (postgres, memory)

## 📝 Flujo de Trabajo

### Ciclo de vida de un paciente en urgencias

1. **Registro de Paciente** (ENFERMERO)
   - Se registra un nuevo paciente o se busca uno existente
   - Si tiene obra social, se verifica automáticamente la afiliación

2. **Registro de Ingreso** (ENFERMERO)
   - Se registra el ingreso del paciente con signos vitales
   - Se asigna un nivel de emergencia (BAJA, MEDIA, ALTA, CRITICA)
   - El ingreso se crea en estado `PENDIENTE`
   - Se agrega automáticamente a la cola de atención ordenada por prioridad

3. **Cola de Atención**
   - Los pacientes se ordenan por nivel de emergencia y fecha de ingreso
   - Los médicos pueden consultar la cola y ver el siguiente paciente

4. **Atender Paciente** (MEDICO)
   - El médico reclama al siguiente paciente de la cola
   - El ingreso cambia a estado `EN_PROCESO`
   - Se remueve de la cola de espera

5. **Registrar Atención** (MEDICO)
   - El médico registra un informe de la atención realizada
   - El ingreso cambia a estado `FINALIZADO`
   - La atención queda asociada al ingreso

### Estados de un Ingreso

- `PENDIENTE`: Ingreso recién registrado, esperando en cola
- `EN_PROCESO`: Paciente siendo atendido por un médico
- `FINALIZADO`: Atención completada con informe médico

## 📝 Notas

- Por defecto, la aplicación usa el perfil "memory" (repositorio en memoria)
- Para usar PostgreSQL, activa el perfil "postgres" y configura la conexión
- Los tokens JWT expiran después de 24 horas por defecto
- La verificación de obras sociales se realiza contra una API externa configurada en `application.properties`
