# 🔍 Análisis de Inconsistencias: Modelo de Datos PostgreSQL vs Entidades del Dominio

## 📋 Resumen Ejecutivo

Este documento identifica inconsistencias entre el modelo de datos de PostgreSQL y cómo se manejan las entidades del dominio en los repositorios de infraestructura.

---

## 🚨 PROBLEMAS CRÍTICOS

### 1. **IngresoRepositoryPostgres: No Carga la Atención Asociada**

**Ubicación**: `tfi/infrastructure/persistence/repository/postgres/IngresoRepositoryPostgres.java:93-112`

**Problema**:
- En el método `mapRow()`, se crea un `Ingreso` con `atencion = null` (línea 93)
- Nunca se carga la `Atencion` asociada desde la base de datos
- La tabla `atenciones` tiene una relación `UNIQUE` con `ingresos` (un ingreso puede tener una atención)
- Esto significa que si un ingreso tiene una atención registrada, no se está cargando

**Código problemático**:
```java
// Línea 93-96
Atencion atencion = null;  // ❌ Siempre null, nunca se carga

Ingreso ingreso = new Ingreso(
    atencion,  // ❌ Siempre null
    paciente,
    enfermero,
    // ...
);
```

**Impacto**:
- Si se consulta un ingreso que tiene atención, `ingreso.getAtencion()` siempre retorna `null`
- Esto puede causar errores en la lógica de negocio que depende de saber si un ingreso tiene atención
- Viola el principio de que el repositorio debe cargar completamente el agregado

**Solución**:
- Agregar un LEFT JOIN con la tabla `atenciones` en `buildSelectQuery()`
- Cargar la atención si existe en el `mapRow()`
- O hacer una consulta separada cuando se necesite la atención (lazy loading)

---

### 2. **Uso de Setters Deprecated en Repositorios**

**Ubicación**: Múltiples repositorios PostgreSQL

**Problemas**:

#### 2.1. IngresoRepositoryPostgres usa `setEstado()` y `setFechaHoraIngreso()`

**Ubicación**: `IngresoRepositoryPostgres.java:110, 196, 204`

**Código problemático**:
```java
// Línea 110 - Al recuperar desde BD
ingreso.setEstado(estado);  // ❌ Setter deprecated

// Línea 196 - En método add()
ingreso.setFechaHoraIngreso(LocalDateTime.now());  // ❌ Setter deprecated

// Línea 204 - En método add()
ingreso.setEstado(Estado.PENDIENTE);  // ❌ Setter deprecated
```

**Problemas**:
- Usa setters marcados como `@Deprecated`
- No usa métodos de negocio de la entidad
- Viola el principio de Rich Domain Model
- Aunque los setters están deprecated, siguen siendo públicos y se pueden usar incorrectamente

**Solución**:
- Los setters deprecated deberían ser package-private para que solo los repositorios puedan usarlos
- O mejor: usar reflection para establecer valores necesarios para persistencia
- O mejor aún: crear métodos package-private específicos para repositorios

#### 2.2. AtencionRepositoryPostgres usa `setFechaAtencion()`

**Ubicación**: `AtencionRepositoryPostgres.java:114`

**Código problemático**:
```java
atencion.setFechaAtencion(fechaAtencion);  // ❌ Setter deprecated
```

**Problema**: Similar al anterior, usa setter deprecated.

---

### 3. **Inconsistencia: Atencion usa IDs en lugar de Referencias**

**Ubicación**: `tfi/domain/entity/Atencion.java` y `AtencionRepositoryPostgres.java`

**Problema**:
- La entidad `Atencion` usa `String ingresoId` y `String medicoId` en lugar de referencias a entidades
- Esto ya fue identificado como problema en el análisis general
- El repositorio está bien alineado con esto (usa los IDs), pero el problema es de diseño del dominio

**Impacto**:
- No hay validación de que el ingreso existe
- No hay validación de que el médico es válido
- Dificulta el razonamiento sobre el dominio

**Nota**: Este problema ya está documentado en `ANALISIS_COMPLETO_INCONSISTENCIAS.md`. El repositorio está correcto según el diseño actual, pero el diseño del dominio debería cambiar.

---

### 4. **IngresoRepositoryPostgres: Lógica de Negocio en el Repositorio**

**Ubicación**: `IngresoRepositoryPostgres.java:195-205`

**Problema**:
- El método `add()` establece valores por defecto (`setFechaHoraIngreso()`, `setEstado()`)
- Esta lógica debería estar en el dominio, no en el repositorio
- El repositorio solo debería persistir, no establecer valores por defecto

**Código problemático**:
```java
if (ingreso.getFechaHoraIngreso() == null) {
    ingreso.setFechaHoraIngreso(LocalDateTime.now());  // ❌ Lógica de negocio en repositorio
}
if (ingreso.getEstado() == null) {
    ingreso.setEstado(Estado.PENDIENTE);  // ❌ Lógica de negocio en repositorio
}
```

**Solución**:
- Esta lógica debería estar en el constructor o factory method de `Ingreso`
- El repositorio solo debería validar que los valores requeridos no sean null
- O mejor: el constructor de `Ingreso` debería establecer estos valores por defecto

---

### 5. **Falta de Validación de Relaciones en Base de Datos**

**Problema General**:
- Los repositorios no validan explícitamente que las relaciones existan antes de insertar
- Se confía en las foreign keys de PostgreSQL, pero no hay validación temprana

**Ejemplo**:
- `IngresoRepositoryPostgres.add()` no valida que `paciente_id` y `enfermero_id` existan antes de insertar
- Si no existen, PostgreSQL lanzará un error de foreign key, pero sería mejor validar antes

**Recomendación**:
- Agregar validaciones explícitas antes de insertar/actualizar
- O confiar en las foreign keys y manejar las excepciones apropiadamente

---

### 6. **Inconsistencia en Manejo de Nulls**

**Ubicación**: Múltiples repositorios

**Problemas**:
- Algunos campos pueden ser null en la BD pero no se valida consistentemente
- Por ejemplo, `paciente.email` puede ser null, pero no hay validación clara

**Recomendación**:
- Documentar claramente qué campos pueden ser null
- Validar consistentemente en los repositorios

---

## 📊 RESUMEN DE PROBLEMAS POR REPOSITORIO

### IngresoRepositoryPostgres
1. ❌ No carga la atención asociada
2. ❌ Usa setters deprecated (`setEstado()`, `setFechaHoraIngreso()`)
3. ❌ Lógica de negocio en el repositorio (valores por defecto)

### AtencionRepositoryPostgres
1. ❌ Usa setter deprecated (`setFechaAtencion()`)
2. ⚠️ Diseño de dominio usa IDs en lugar de referencias (ya documentado)

### PacientesRepositoryPostgres
✅ No se encontraron problemas críticos

### UsuarioRepositoryPostgres
✅ No se encontraron problemas críticos

---

## 🔧 SOLUCIONES PROPUESTAS

### Solución 1: Cargar Atención en IngresoRepositoryPostgres

**Opción A: LEFT JOIN (Recomendada)**
```java
private String buildSelectQuery() {
    return "SELECT " +
           // ... campos de ingreso ...
           "a.id AS atencion_id, a.ingreso_id AS atencion_ingreso_id, " +
           "a.medico_id AS atencion_medico_id, a.informe_medico AS atencion_informe_medico, " +
           "a.fecha_atencion AS atencion_fecha_atencion " +
           "FROM ingresos i " +
           "INNER JOIN pacientes p ON i.paciente_id = p.id " +
           "INNER JOIN usuarios e ON i.enfermero_id = e.id " +
           "LEFT JOIN atenciones a ON i.id = a.ingreso_id";  // ✅ LEFT JOIN
}

// En mapRow():
Atencion atencion = null;
String atencionId = rs.getString("atencion_id");
if (atencionId != null) {
    String ingresoId = rs.getString("atencion_ingreso_id");
    String medicoId = rs.getString("atencion_medico_id");
    String informeMedico = rs.getString("atencion_informe_medico");
    Timestamp fechaAtencion = rs.getTimestamp("atencion_fecha_atencion");
    atencion = new Atencion(atencionId, ingresoId, medicoId, informeMedico, 
                           fechaAtencion != null ? fechaAtencion.toLocalDateTime() : null);
}
```

**Opción B: Lazy Loading**
- Cargar la atención solo cuando se necesite
- Agregar método `loadAtencion(Ingreso ingreso)` que haga una consulta separada

---

### Solución 2: Hacer Setters Deprecated Package-Private

**Cambio en entidades**:
```java
// En Ingreso.java
/**
 * Setter para estado - SOLO para uso interno del repositorio al recuperar desde BD.
 * @deprecated Usar métodos de negocio (iniciarAtencion(), finalizar()) en su lugar.
 */
@Deprecated
void setEstado(Estado estado) {  // ✅ Sin public, solo package-private
    this.estado = estado;
}
```

**Ventajas**:
- Los repositorios pueden seguir usándolos (mismo package)
- El código fuera del package no puede usarlos accidentalmente
- Mantiene la compatibilidad con los repositorios actuales

---

### Solución 3: Mover Lógica de Negocio al Dominio

**En Ingreso.java**:
```java
public Ingreso(Paciente paciente, Usuario enfermero, String descripcion, ...) {
    // ... validaciones ...
    this.fechaHoraIngreso = LocalDateTime.now();  // ✅ Valor por defecto en constructor
    this.estado = Estado.PENDIENTE;  // ✅ Valor por defecto en constructor
    // ...
}
```

**En IngresoRepositoryPostgres.add()**:
```java
// Eliminar estas líneas:
// if (ingreso.getFechaHoraIngreso() == null) { ... }
// if (ingreso.getEstado() == null) { ... }

// Solo validar:
if (ingreso.getFechaHoraIngreso() == null) {
    throw new IllegalArgumentException("La fecha de ingreso no puede ser nula");
}
```

---

## 📝 RECOMENDACIONES PRIORITARIAS

### Prioridad ALTA (Crítico)

1. ✅ **Cargar atención en IngresoRepositoryPostgres**
   - Agregar LEFT JOIN con tabla `atenciones`
   - Cargar la atención en `mapRow()` si existe

2. ✅ **Mover lógica de valores por defecto al dominio**
   - El constructor de `Ingreso` debe establecer `fechaHoraIngreso` y `estado` por defecto
   - Eliminar esta lógica del repositorio

### Prioridad MEDIA (Importante)

3. ✅ **Hacer setters deprecated package-private**
   - Cambiar visibilidad de setters deprecated a package-private
   - Esto previene su uso accidental fuera del package

4. ✅ **Eliminar uso de setters deprecated en repositorios**
   - Los repositorios pueden seguir usándolos si son package-private
   - O usar reflection si es necesario

### Prioridad BAJA (Mejoras)

5. ✅ **Agregar validaciones explícitas de relaciones**
   - Validar que `paciente_id` y `enfermero_id` existan antes de insertar
   - O manejar excepciones de foreign key apropiadamente

6. ✅ **Documentar campos nullable**
   - Documentar claramente qué campos pueden ser null en cada entidad
   - Validar consistentemente en repositorios

---

## 📚 Referencias

- **Rich Domain Model**: Martin Fowler
- **Repository Pattern**: Domain-Driven Design - Eric Evans
- **Clean Architecture**: Robert C. Martin

---

*Última actualización: Análisis del modelo de datos PostgreSQL*

