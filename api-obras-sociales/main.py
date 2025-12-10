from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List, Optional
import psycopg2
from psycopg2.extras import RealDictCursor
import os
from contextlib import contextmanager

app = FastAPI(title="API de Obras Sociales", version="1.0.0")

# Configuración CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Configuración de la base de datos
# Por defecto usa la misma instancia de PostgreSQL que el backend (tfi-postgres)
DATABASE_CONFIG = {
    "host": os.getenv("DB_HOST", "localhost"),
    "port": os.getenv("DB_PORT", "5432"),
    "database": os.getenv("DB_NAME", "obras_sociales_db"),
    "user": os.getenv("DB_USER", "tfi_user"),
    "password": os.getenv("DB_PASSWORD", "tfi_password")
}

@contextmanager
def get_db_connection():
    """Context manager para obtener conexión a la base de datos"""
    conn = None
    try:
        conn = psycopg2.connect(**DATABASE_CONFIG)
        yield conn
        conn.commit()
    except Exception as e:
        if conn:
            conn.rollback()
        raise e
    finally:
        if conn:
            conn.close()

# Modelos Pydantic
class ObraSocial(BaseModel):
    id: int
    nombre: str

    class Config:
        from_attributes = True

class Afiliado(BaseModel):
    numero_afiliado: str
    obra_social_id: int
    obra_social_nombre: str

class VerificacionAfiliacion(BaseModel):
    esta_afiliado: bool
    numero_afiliado: str
    obra_social: Optional[ObraSocial] = None

# Endpoints
@app.get("/")
async def root():
    return {
        "message": "API de Obras Sociales",
        "version": "1.0.0",
        "endpoints": {
            "listar_obras_sociales": "/api/obras-sociales",
            "verificar_afiliacion": "/api/obras-sociales/verificar?obra_social_id={id}&numero_afiliado={numero}"
        }
    }

@app.get("/api/obras-sociales", response_model=List[ObraSocial])
async def listar_obras_sociales():
    """
    Lista todas las obras sociales disponibles.
    """
    try:
        with get_db_connection() as conn:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                cur.execute("SELECT id, nombre FROM obras_sociales ORDER BY id")
                obras_sociales = cur.fetchall()
                return [ObraSocial(**dict(row)) for row in obras_sociales]
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al obtener obras sociales: {str(e)}") from e

@app.get("/api/obras-sociales/verificar", response_model=VerificacionAfiliacion)
async def verificar_afiliacion(obra_social_id: int, numero_afiliado: str):
    """
    Verifica si un paciente está afiliado a una obra social específica mediante su número de afiliado.
    
    - **obra_social_id**: ID de la obra social a verificar
    - **numero_afiliado**: Número de afiliado del paciente
    """
    try:
        with get_db_connection() as conn:
            with conn.cursor(cursor_factory=RealDictCursor) as cur:
                # Primero verificar que la obra social existe
                cur.execute("SELECT id, nombre FROM obras_sociales WHERE id = %s", (obra_social_id,))
                obra_social_data = cur.fetchone()
                
                if not obra_social_data:
                    raise HTTPException(
                        status_code=404, 
                        detail=f"Obra social con ID {obra_social_id} no encontrada"
                    )
                
                # Verificar si el número de afiliado está afiliado a esa obra social específica
                cur.execute("""
                    SELECT a.numero_afiliado, a.obra_social_id, os.nombre as obra_social_nombre
                    FROM afiliados a
                    INNER JOIN obras_sociales os ON a.obra_social_id = os.id
                    WHERE a.numero_afiliado = %s AND a.obra_social_id = %s
                """, (numero_afiliado, obra_social_id))
                
                resultado = cur.fetchone()
                
                obra_social = ObraSocial(
                    id=obra_social_data['id'],
                    nombre=obra_social_data['nombre']
                )
                
                if resultado:
                    return VerificacionAfiliacion(
                        esta_afiliado=True,
                        numero_afiliado=numero_afiliado,
                        obra_social=obra_social
                    )
                else:
                    return VerificacionAfiliacion(
                        esta_afiliado=False,
                        numero_afiliado=numero_afiliado,
                        obra_social=obra_social
                    )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error al verificar afiliación: {str(e)}") from e

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)

