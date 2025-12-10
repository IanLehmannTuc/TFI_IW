# Guía de Docker para TFI - Sistema de Gestión de Urgencias

Esta guía explica cómo ejecutar todos los servicios del sistema usando Docker Compose.

## 📋 Requisitos Previos

- Docker Engine 20.10+
- Docker Compose 2.0+

## 🚀 Inicio Rápido

### 1. Construir y levantar todos los servicios

```bash
docker-compose up --build
```

Este comando:
- Construye las imágenes de Docker para backend, frontend y api-obras-sociales
- Crea y configura la base de datos PostgreSQL
- Ejecuta el script de inicialización de la base de datos
- Levanta todos los servicios en la red Docker

### 2. Ejecutar en segundo plano

```bash
docker-compose up -d --build
```

### 3. Ver logs

```bash
# Ver logs de todos los servicios
docker-compose logs -f

# Ver logs de un servicio específico
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
docker-compose logs -f api-obras-sociales
```

### 4. Detener los servicios

```bash
docker-compose down
```

### 5. Detener y eliminar volúmenes (⚠️ elimina los datos de la BD)

```bash
docker-compose down -v
```

## 🌐 Servicios y Puertos

| Servicio | Puerto | URL | Descripción |
|----------|--------|-----|-------------|
| Frontend | 3000 | http://localhost:3000 | Interfaz web React |
| Backend | 8080 | http://localhost:8080 | API Spring Boot |
| API Obras Sociales | 8001 | http://localhost:8001 | API FastAPI para obras sociales |
| PostgreSQL | 5432 | localhost:5432 | Base de datos |

## 📊 Estructura de Servicios

```
┌─────────────┐
│  Frontend   │ (React/Vite) → Puerto 3000
│  (Nginx)    │
└──────┬──────┘
       │
       ↓ HTTP
┌─────────────┐
│  Backend    │ (Spring Boot) → Puerto 8080
│  (Java 22)  │
└──────┬──────┘
       │
       ├──→ HTTP ──┐
       │           │
       ↓ JDBC      ↓ HTTP
┌─────────────┐  ┌──────────────────┐
│ PostgreSQL  │  │ API Obras Sociales│
│  (Puerto    │  │  (FastAPI/Python)│
│   5432)     │  │  (Puerto 8001)    │
└─────────────┘  └──────────────────┘
```

## 🗄️ Base de Datos

### Inicialización Automática

La base de datos se inicializa automáticamente al crear el contenedor por primera vez. El script `docker/init-db.sql` se ejecuta automáticamente y crea:

- Todas las tablas del sistema (usuarios, pacientes, ingresos, atenciones)
- Tablas de obras sociales (obras_sociales, afiliados)
- Índices para optimización
- Datos iniciales de obras sociales y afiliados de ejemplo

### Credenciales de Base de Datos

- **Base de datos**: `tfi`
- **Usuario**: `tfi_user`
- **Contraseña**: `tfi_password`
- **Host**: `postgres` (dentro de Docker) o `localhost` (desde fuera)

### Conectarse a la Base de Datos

```bash
# Desde fuera de Docker
psql -h localhost -U tfi_user -d tfi

# Desde dentro de Docker
docker-compose exec postgres psql -U tfi_user -d tfi
```

## 🔧 Configuración

### Variables de Entorno del Backend

Las variables de entorno del backend se configuran en `docker-compose.yml`:

- `SPRING_PROFILES_ACTIVE`: `postgres`
- `SPRING_DATASOURCE_URL`: `jdbc:postgresql://postgres:5432/tfi`
- `JWT_SECRET`: Clave secreta para JWT
- `JWT_EXPIRATION`: Tiempo de expiración del token (86400000 ms = 24 horas)
- `OBRAS_SOCIALES_API_URL`: `http://api-obras-sociales:8001`

### Variables de Entorno del Frontend

El frontend usa variables de entorno de Vite. Se configura en `docker-compose.yml`:

- `VITE_API_BASE_URL`: `http://localhost:8080/api`

**Nota**: El frontend se conecta al backend usando `localhost:8080` porque el navegador del usuario ejecuta las peticiones, no el contenedor Docker.

### Variables de Entorno de API Obras Sociales

- `DB_HOST`: `postgres`
- `DB_PORT`: `5432`
- `DB_NAME`: `tfi`
- `DB_USER`: `tfi_user`
- `DB_PASSWORD`: `tfi_password`

## 🛠️ Comandos Útiles

### Reconstruir un servicio específico

```bash
docker-compose build backend
docker-compose up -d backend
```

### Reiniciar un servicio

```bash
docker-compose restart backend
```

### Ejecutar comandos dentro de un contenedor

```bash
# Backend
docker-compose exec backend sh

# PostgreSQL
docker-compose exec postgres psql -U tfi_user -d tfi

# API Obras Sociales
docker-compose exec api-obras-sociales sh
```

### Ver el estado de los servicios

```bash
docker-compose ps
```

### Ver el uso de recursos

```bash
docker stats
```

## 🐛 Solución de Problemas

### El backend no se conecta a la base de datos

1. Verifica que PostgreSQL esté corriendo: `docker-compose ps`
2. Verifica los logs: `docker-compose logs postgres`
3. Asegúrate de que el backend espere a que PostgreSQL esté listo (usa `depends_on` con `condition: service_healthy`)

### El frontend no se conecta al backend

1. Verifica que el backend esté corriendo: `docker-compose ps`
2. Verifica que la URL en `constants.ts` sea correcta
3. Verifica los logs del backend: `docker-compose logs backend`
4. Abre las herramientas de desarrollador del navegador y revisa la consola y la pestaña Network

### La base de datos no se inicializa

1. Elimina el volumen y vuelve a crear: `docker-compose down -v && docker-compose up -d`
2. Verifica que el script `docker/init-db.sql` exista y sea válido
3. Revisa los logs de PostgreSQL: `docker-compose logs postgres`

### Los cambios en el código no se reflejan

1. Reconstruye las imágenes: `docker-compose build`
2. Reinicia los servicios: `docker-compose restart`

### Puerto ya en uso

Si un puerto ya está en uso, puedes cambiarlo en `docker-compose.yml`:

```yaml
ports:
  - "8081:8080"  # Cambiar el puerto externo
```

## 📝 Desarrollo

### Modo Desarrollo con Hot Reload

Para desarrollo con hot reload, es recomendable ejecutar los servicios localmente:

- **Backend**: `cd backend && ./mvnw spring-boot:run`
- **Frontend**: `cd frontend && npm run dev`
- **PostgreSQL**: Usar Docker Compose solo para la base de datos: `docker-compose up postgres`

### Ejecutar Tests

```bash
# Backend
docker-compose exec backend ./mvnw test

# O desde fuera del contenedor
cd backend && ./mvnw test
```

## 🔒 Seguridad

⚠️ **Importante para Producción**:

1. Cambiar todas las contraseñas por defecto
2. Usar variables de entorno para secretos sensibles
3. Configurar HTTPS/SSL
4. Restringir los orígenes CORS
5. Usar un secreto JWT más seguro
6. Configurar límites de recursos (CPU, memoria)
7. Usar secrets de Docker para credenciales

## 📚 Documentación Adicional

- [README.md](./README.md) - Documentación general del proyecto
- [backend/README.md](./backend/README.md) - Documentación del backend
- [frontend/README.md](./frontend/README.md) - Documentación del frontend
- [api-obras-sociales/README.md](./api-obras-sociales/README.md) - Documentación de la API de obras sociales

