# API de Obras Sociales

API REST desarrollada con FastAPI para gestionar obras sociales y verificar afiliaciones de pacientes.

## Funcionalidades

- **Listar obras sociales**: Obtiene todas las obras sociales disponibles
- **Verificar afiliación**: Verifica si un paciente está afiliado a alguna obra social mediante su número de afiliado

## Requisitos

- Python 3.8 o superior
- Docker (para PostgreSQL)
- El contenedor `tfi-postgres` debe estar corriendo (misma instancia que el backend)

## Instalación

1. Asegúrate de que el contenedor de PostgreSQL esté corriendo:
```bash
# Desde el directorio backend
cd ../backend
docker-compose up -d
```

2. Instalar las dependencias:
```bash
pip install -r requirements.txt
```

3. Inicializar la base de datos:
```bash
cd scripts
./init-db.sh
```

Este script:
- Se conecta al contenedor Docker `tfi-postgres` existente
- Crea la base de datos `obras_sociales_db` en esa instancia
- Crea las tablas necesarias
- Pobla la base de datos con obras sociales y afiliados de ejemplo

**Nota:** Por defecto, la API usa las mismas credenciales que el backend (`tfi_user` / `tfi_password`). Si necesitas cambiar la configuración, puedes usar variables de entorno:
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=obras_sociales_db
export DB_USER=tfi_user
export DB_PASSWORD=tfi_password
```

## Ejecución

### Opción 1: Usando uvicorn directamente
```bash
uvicorn main:app --reload --host 0.0.0.0 --port 8000
```

### Opción 2: Ejecutando el script Python
```bash
python main.py
```

La API estará disponible en: `http://localhost:8000`

## Documentación

Una vez que la API esté corriendo, puedes acceder a:
- **Documentación interactiva (Swagger)**: `http://localhost:8000/docs`
- **Documentación alternativa (ReDoc)**: `http://localhost:8000/redoc`

## Endpoints

### 1. Listar Obras Sociales

**GET** `/api/obras-sociales`

Obtiene todas las obras sociales disponibles.

**Respuesta exitosa (200):**
```json
[
  {
    "id": 1,
    "nombre": "OSDE"
  },
  {
    "id": 2,
    "nombre": "Swiss Medical"
  }
]
```

### 2. Verificar Afiliación

**GET** `/api/obras-sociales/verificar`

Verifica si un paciente está afiliado a una obra social específica.

**Parámetros de query:**
- `obra_social_id` (int, requerido): ID de la obra social a verificar
- `numero_afiliado` (string, requerido): Número de afiliado del paciente

**Ejemplo de uso:**
```
GET /api/obras-sociales/verificar?obra_social_id=1&numero_afiliado=OSDE001234
```

**Respuesta exitosa (200) - Afiliado encontrado:**
```json
{
  "esta_afiliado": true,
  "numero_afiliado": "OSDE001234",
  "obra_social": {
    "id": 1,
    "nombre": "OSDE"
  }
}
```

**Respuesta exitosa (200) - No afiliado a esa obra social:**
```json
{
  "esta_afiliado": false,
  "numero_afiliado": "OSDE001234",
  "obra_social": {
    "id": 2,
    "nombre": "Swiss Medical"
  }
}
```

**Error (404) - Obra social no encontrada:**
```json
{
  "detail": "Obra social con ID 999 no encontrada"
}
```

## Estructura de la Base de Datos

### Tabla: `obras_sociales`
- `id` (SERIAL PRIMARY KEY): Identificador único
- `nombre` (VARCHAR): Nombre de la obra social
- `created_at` (TIMESTAMP): Fecha de creación

### Tabla: `afiliados`
- `id` (SERIAL PRIMARY KEY): Identificador único
- `numero_afiliado` (VARCHAR, UNIQUE): Número de afiliado del paciente
- `obra_social_id` (INTEGER, FOREIGN KEY): Referencia a la obra social
- `nombre_paciente` (VARCHAR): Nombre del paciente (opcional)
- `apellido_paciente` (VARCHAR): Apellido del paciente (opcional)
- `created_at` (TIMESTAMP): Fecha de creación

## Ejemplos de Uso

### Usando curl

Listar obras sociales:
```bash
curl http://localhost:8000/api/obras-sociales
```

Verificar afiliación:
```bash
curl "http://localhost:8000/api/obras-sociales/verificar?obra_social_id=1&numero_afiliado=OSDE001234"
```

### Usando Python

```python
import requests

# Listar obras sociales
response = requests.get("http://localhost:8000/api/obras-sociales")
obras_sociales = response.json()
print(obras_sociales)

# Verificar afiliación
response = requests.get(
    "http://localhost:8000/api/obras-sociales/verificar",
    params={"obra_social_id": 1, "numero_afiliado": "OSDE001234"}
)
afiliacion = response.json()
print(afiliacion)
```

## Notas

- La base de datos se inicializa con 10 obras sociales y más de 50 afiliados de ejemplo
- El número de afiliado debe ser único en la base de datos
- La API utiliza CORS habilitado para permitir peticiones desde cualquier origen

