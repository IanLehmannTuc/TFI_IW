# Actualización de Documentación - Backend

## Fecha: Diciembre 2025

### Resumen de Cambios

Se ha actualizado la documentación completa del backend para reflejar el estado actual del sistema, incluyendo el nuevo módulo de **Atenciones** y el flujo de trabajo completo del sistema de urgencias.

---

## Archivos Actualizados

### 1. README.md

**Cambios principales:**
- ✅ Actualizado requisito de Java 17 → **Java 22**
- ✅ Corregido enlace duplicado en documentación de API
- ✅ Agregada sección de **Atenciones** en endpoints disponibles
- ✅ Agregada sección de **Obras Sociales** en endpoints disponibles
- ✅ Actualizada lista de dependencias principales con versiones específicas:
  - Spring Boot 3.5.5
  - JWT (jjwt 0.12.3)
  - Spring JDBC (sin JPA/ORM)
  - BCrypt para hasheo de contraseñas
  - Cucumber y JUnit 5 para tests
- ✅ **Nueva sección**: "Flujo de Trabajo" con el ciclo de vida completo de un paciente
- ✅ **Nueva sección**: "Estados de un Ingreso" (PENDIENTE, EN_PROCESO, FINALIZADO)
- ✅ Agregada nota sobre verificación de obras sociales con API externa

### 2. API.md

**Cambios principales:**

#### Estados actualizados
- ✅ Actualizado enum `Estado`: EN_ESPERA/EN_ATENCION/ATENDIDO → **PENDIENTE/EN_PROCESO/FINALIZADO**
- ✅ Todos los ejemplos de respuesta actualizados con los nuevos estados

#### Nueva sección: Atenciones (Sección 6)
- ✅ `POST /api/atenciones` - Registrar atención médica
  - Requiere autoridad MEDICO
  - Valida que el ingreso esté en estado EN_PROCESO
  - Cambia automáticamente el estado a FINALIZADO
  - Incluye validaciones y efectos secundarios
- ✅ `GET /api/atenciones/ingreso/{ingresoId}` - Obtener atención por ID de ingreso
- ✅ `GET /api/atenciones/{id}` - Obtener atención por ID

#### Nueva sección: Obras Sociales (Sección 7)
- ✅ `GET /api/obras-sociales` - Listar obras sociales disponibles
- ✅ Documentación de integración con API externa

#### Actualización: Cola de Atención
- ✅ Actualizado endpoint `/api/cola-atencion/atender`:
  - Ahora indica que cambia el estado a **EN_PROCESO** (antes decía EN_ATENCION)
  - Aclarado que remueve de la cola y cambia el estado

#### Nueva sección: Flujo de Trabajo del Sistema
- ✅ Diagrama de estados: PENDIENTE → EN_PROCESO → FINALIZADO
- ✅ Descripción detallada de cada estado
- ✅ **Diagrama de flujo ASCII** completo mostrando:
  - Rol del enfermero (registro de paciente e ingreso)
  - Cola de atención automática con ordenamiento
  - Rol del médico (reclamar paciente y registrar atención)
  - Transiciones de estado en cada paso

#### Actualización: Ejemplos de Uso
- ✅ Reescrito completamente el flujo de ejemplo con 9 pasos:
  1. Login como enfermero
  2. Listar obras sociales
  3. Registrar paciente con obra social
  4. Registrar ingreso (estado PENDIENTE)
  5. Login como médico
  6. Ver cola de atención
  7. Atender siguiente paciente (estado → EN_PROCESO)
  8. **Registrar atención médica (estado → FINALIZADO)**
  9. **Consultar atención registrada**
- ✅ Agregada sección "Otros ejemplos útiles" con operaciones adicionales

#### Nueva sección: Notas ampliadas
- ✅ Subsección "General" con formatos y convenciones
- ✅ Subsección "Seguridad" con información sobre JWT y BCrypt
- ✅ Subsección "Autoridades y Permisos" con roles clarificados
- ✅ **Subsección "Validaciones Importantes"** con reglas de negocio:
  - Informe médico obligatorio
  - Solo se atienden ingresos EN_PROCESO
  - Una atención por ingreso
  - Transiciones automáticas de estado

---

## Información Clave Añadida

### Ciclo de Vida Completo de un Ingreso

```
┌─────────────────┐
│   PENDIENTE     │  ← Enfermero registra ingreso
│ (En cola)       │
└────────┬────────┘
         │
         │ Médico reclama paciente
         ↓
┌─────────────────┐
│   EN_PROCESO    │  ← Médico evaluando paciente
│                 │
└────────┬────────┘
         │
         │ Médico registra atención
         ↓
┌─────────────────┐
│   FINALIZADO    │  ← Atención completada con informe
│                 │
└─────────────────┘
```

### Roles y Permisos Clarificados

| Rol       | Puede...                                                    |
|-----------|-------------------------------------------------------------|
| ENFERMERO | Registrar pacientes, registrar ingresos, modificar datos   |
| MEDICO    | Atender pacientes (reclamar de cola), registrar atenciones |
| Ambos     | Consultar cola, consultar pacientes, consultar ingresos    |

### Validaciones de Negocio Documentadas

1. **Atención solo en estado EN_PROCESO**: No se puede registrar atención si el ingreso no fue reclamado
2. **Una atención por ingreso**: No se permiten múltiples atenciones para el mismo ingreso
3. **Informe obligatorio**: El informe médico no puede estar vacío
4. **Transiciones automáticas**: Los cambios de estado ocurren automáticamente al ejecutar las acciones

---

## Mejoras en la Documentación

### Estructura
- ✅ Organización más clara con secciones numeradas
- ✅ Índice implícito con numeración consistente
- ✅ Separación clara entre endpoints, flujo de trabajo y ejemplos

### Ejemplos
- ✅ Flujo completo de 9 pasos que muestra todo el ciclo de vida
- ✅ Ejemplos de curl actualizados y funcionales
- ✅ Respuestas de ejemplo con estados correctos

### Claridad
- ✅ Diagrama ASCII visual del flujo de trabajo
- ✅ Explicación de efectos secundarios de cada endpoint
- ✅ Validaciones claramente especificadas
- ✅ Códigos de error detallados

---

## Próximos Pasos Recomendados

1. ✅ **Documentación completada** - Los archivos README.md y API.md están actualizados
2. 📝 Considerar agregar diagramas de arquitectura (opcionales)
3. 📝 Considerar agregar colección de Postman con ejemplos (opcional)
4. 📝 Considerar documentación de tests (opcional)

---

## Verificación

Para verificar que la documentación está actualizada:

1. **Revisar README.md**: Debe mencionar Java 22, Atenciones y Obras Sociales
2. **Revisar API.md sección 6**: Debe contener documentación de Atenciones
3. **Revisar API.md sección 7**: Debe contener documentación de Obras Sociales
4. **Buscar "PENDIENTE"**: Debe aparecer en lugar de "EN_ESPERA"
5. **Buscar "EN_PROCESO"**: Debe aparecer en lugar de "EN_ATENCION"
6. **Buscar "FINALIZADO"**: Debe aparecer en lugar de "ATENDIDO"

---

**Nota**: Esta actualización sincroniza la documentación con el código actual del sistema. Todos los endpoints documentados corresponden a controladores implementados en el código fuente.

