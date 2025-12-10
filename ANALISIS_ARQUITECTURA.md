# 🔍 Análisis Exhaustivo de Arquitectura y Diseño - Proyecto DDD

## 📋 Índice
1. [Problemas Críticos de Arquitectura](#problemas-críticos-de-arquitectura)
2. [Problemas de Diseño DDD](#problemas-de-diseño-ddd)
3. [Problemas en Entidades](#problemas-en-entidades)
4. [Problemas en Value Objects](#problemas-en-value-objects)
5. [Problemas en DTOs](#problemas-en-dtos)
6. [Problemas en Servicios](#problemas-en-servicios)
7. [Problemas en Repositorios](#problemas-en-repositorios)
8. [Problemas en Modelo de Base de Datos](#problemas-en-modelo-de-base-de-datos)
9. [Problemas de Separación de Responsabilidades](#problemas-de-separación-de-responsabilidades)
10. [Recomendaciones de Mejora](#recomendaciones-de-mejora)

---

## 🚨 Problemas Críticos de Arquitectura

### 1. **Violación de la Regla de Dependencia (Clean Architecture)**

**Problema**: El dominio está contaminado con dependencias de infraestructura.

**Ejemplos encontrados**:
- `PacientesRepository` usa `Page` y `Pageable` de Spring Data (`org.springframework.data.domain`)
- Esto viola el principio de que el dominio NO debe depender de frameworks externos

**Ubicación**: `tfi/domain/repository/PacientesRepository.java`

```java
// ❌ MAL - El dominio depende de Spring
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PacientesRepository {
    Page<Paciente> findAll(Pageable pageable);
}
```

**Impacto**: 
- El dominio no es independiente de Spring
- Dificulta el testing sin Spring
- Viola Clean Architecture

---

### 2. **Lógica de Negocio en Capa de Aplicación**

**Problema**: La lógica de negocio está en los servicios de aplicación en lugar del dominio.

**Ejemplos**:
- `IngresoService.registrarIngreso()` contiene lógica compleja de creación de pacientes
- `PacienteService.registrar()` contiene validaciones de negocio
- `AtencionService` valida estados de ingreso (debería estar en la entidad)

**Ubicación**: `tfi/application/service/IngresoService.java:56-140`

```java
// ❌ MAL - Lógica de negocio en servicio de aplicación
public IngresoResponse registrarIngreso(RegistroIngresoRequest ingresoDto) {
    // 100+ líneas de lógica de creación de paciente
    // Validaciones de negocio
    // Construcción compleja de objetos
}
```

**Impacto**:
- La lógica de negocio no está encapsulada en el dominio
- Dificulta el testing unitario del dominio
- Viola DDD (Domain-Driven Design)

---

### 3. **Entidades Anémicas (Anemic Domain Model)**

**Problema**: Las entidades son principalmente getters/setters sin comportamiento.

**Ejemplos**:
- `Ingreso` no tiene métodos de negocio como `iniciarAtencion()`, `finalizar()`
- `Paciente` no tiene métodos de negocio
- `Atencion` es completamente anémica

**Ubicación**: `tfi/domain/entity/Ingreso.java`

```java
// ❌ MAL - Entidad anémica
public class Ingreso {
    private Estado estado;
    
    // Solo getters/setters, sin comportamiento
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
    
    // ❌ Falta: iniciarAtencion(), finalizar(), etc.
}
```

**Impacto**:
- No hay encapsulación de lógica de negocio
- Las reglas de negocio están dispersas
- Viola el principio de Rich Domain Model

---

### 4. **Falta de Agregados (Aggregates) y Agregados Raíz**

**Problema**: No hay definición clara de agregados y agregados raíz.

**Análisis**:
- `Ingreso` debería ser un agregado raíz que contiene `Atencion`
- `Paciente` debería ser un agregado raíz
- No hay definición de límites de agregados
- No hay protección de invariantes

**Impacto**:
- No hay control de consistencia
- Se pueden violar invariantes del dominio
- Dificulta la gestión de transacciones

---

## 🏗️ Problemas de Diseño DDD

### 5. **Falta de Servicios de Dominio**

**Problema**: La lógica que involucra múltiples entidades está en servicios de aplicación.

**Ejemplo**: `ColaAtencionService` está en `application` pero debería estar en `domain.service` si contiene lógica de dominio.

**Ubicación**: `tfi/application/service/ColaAtencionService.java`

**Impacto**:
- Confusión sobre qué es lógica de dominio vs aplicación
- Dificulta la identificación de conceptos del dominio

---

### 6. **Value Objects Mutables**

**Problema**: Algunos value objects permiten mutación.

**Ejemplo**: `Afiliado` tiene setters que permiten cambiar el estado.

**Ubicación**: `tfi/domain/entity/Afiliado.java`

```java
// ❌ MAL - Value Object mutable
public class Afiliado {
    private ObraSocial obraSocial;
    private String numeroAfiliado;
    
    public void setObraSocial(ObraSocial obraSocial) {
        this.obraSocial = obraSocial; // ❌ Permite mutación
    }
}
```

**Impacto**:
- Viola la inmutabilidad de Value Objects
- Puede causar bugs por efectos secundarios
- Dificulta el razonamiento sobre el estado

---

### 7. **Falta de Factory Methods en Entidades**

**Problema**: Las entidades se crean directamente con constructores complejos.

**Ejemplo**: `Paciente` tiene múltiples constructores que hacen difícil entender qué es válido.

**Ubicación**: `tfi/domain/entity/Paciente.java`

```java
// ❌ MAL - Múltiples constructores confusos
public Paciente(String cuil) { ... }
public Paciente(String cuil, Domicilio domicilio, Afiliado obraSocial) { ... }
public Paciente(String cuil, String nombre, String apellido) { ... }
public Paciente(String cuil, String nombre, String apellido, String email, Domicilio domicilio, Afiliado obraSocial) { ... }
```

**Mejora sugerida**: Usar factory methods con nombres expresivos:
```java
// ✅ BIEN
public static Paciente crearConDatosBasicos(String cuil, String nombre, String apellido) { ... }
public static Paciente crearCompleto(String cuil, String nombre, String apellido, Email email, Domicilio domicilio, Afiliado afiliado) { ... }
```

---

## 📦 Problemas en Entidades

### 8. **Entidad `Ingreso` sin Métodos de Negocio**

**Problema**: `Ingreso` no tiene métodos para gestionar su ciclo de vida.

**Falta**:
- `iniciarAtencion()` - Debería validar que está PENDIENTE y cambiar a EN_PROCESO
- `finalizar()` - Debería validar que tiene atención y cambiar a FINALIZADO
- `asignarAtencion(Atencion atencion)` - Debería validar el estado

**Ubicación**: `tfi/domain/entity/Ingreso.java`

**Código actual**:
```java
// ❌ MAL - Se cambia el estado directamente desde el servicio
ingreso.setEstado(Estado.EN_PROCESO);
```

**Debería ser**:
```java
// ✅ BIEN - La entidad controla su estado
public void iniciarAtencion() {
    if (this.estado != Estado.PENDIENTE) {
        throw new IllegalStateException("Solo se pueden iniciar ingresos PENDIENTES");
    }
    this.estado = Estado.EN_PROCESO;
}
```

---

### 9. **Entidad `Atencion` Anémica y con IDs en lugar de Referencias**

**Problema**: `Atencion` usa `String ingresoId` y `String medicoId` en lugar de referencias a entidades.

**Ubicación**: `tfi/domain/entity/Atencion.java`

```java
// ❌ MAL - Usa IDs en lugar de referencias
private String ingresoId;
private String medicoId;
```

**Problemas**:
- No hay validación de que el ingreso existe
- No hay validación de que el médico es válido
- Dificulta el razonamiento sobre el dominio
- Viola el principio de Rich Domain Model

**Debería ser**:
```java
// ✅ BIEN - Referencias a entidades
private Ingreso ingreso;
private Usuario medico;
```

---

### 10. **Entidad `Paciente` con Constructores Confusos**

**Problema**: Múltiples constructores hacen difícil saber qué combinaciones son válidas.

**Ubicación**: `tfi/domain/entity/Paciente.java:16-39`

**Problemas**:
- No está claro qué campos son obligatorios
- Se pueden crear pacientes en estados inválidos
- Falta validación de invariantes

---

### 11. **Entidad `Usuario` con Dos Constructores Inconsistentes**

**Problema**: `Usuario` tiene un constructor completo y uno básico que deja campos null.

**Ubicación**: `tfi/domain/entity/Usuario.java:39-88`

**Problemas**:
- Se pueden crear usuarios en estados inconsistentes
- El constructor básico permite crear usuarios sin datos personales
- No está claro cuándo usar cada constructor

---

## 🎯 Problemas en Value Objects

### 12. **Value Objects sin Validación Suficiente**

**Problema**: Algunos value objects no validan todas las reglas de negocio.

**Ejemplo**: `Telefono` valida formato pero no valida que sea un número argentino válido.

**Ubicación**: `tfi/domain/valueObject/Telefono.java`

---

### 13. **Falta de Value Objects para Conceptos del Dominio**

**Problema**: Se usan tipos primitivos donde deberían haber Value Objects.

**Ejemplos**:
- `Ingreso.descripcion` es `String` - debería ser `DescripcionIngreso` (Value Object)
- `Atencion.informeMedico` es `String` - debería ser `InformeMedico` (Value Object)
- `Paciente.nombre` y `Paciente.apellido` son `String` - podrían ser `Nombre` y `Apellido`

---

### 14. **Value Objects sin Métodos de Negocio**

**Problema**: Los value objects solo validan pero no tienen comportamiento.

**Ejemplo**: `Temperatura` podría tener métodos como:
- `esFiebre()` - retorna true si > 37.5°C
- `esHipotermia()` - retorna true si < 36°C
- `esNormal()` - retorna true si está en rango normal

---

## 📝 Problemas en DTOs

### 15. **DTOs con Lógica de Negocio**

**Problema**: Los DTOs deberían ser solo contenedores de datos, pero algunos tienen validaciones complejas.

**Ejemplo**: `RegistroIngresoRequest` tiene validaciones de Jakarta pero también se valida lógica de negocio en el servicio.

---

### 16. **Falta de Separación entre DTOs de Request y Response**

**Problema**: Algunos DTOs se reutilizan para request y response.

**Mejora**: Separar claramente:
- `RegistroIngresoRequest` - solo para entrada
- `IngresoResponse` - solo para salida
- No mezclar responsabilidades

---

### 17. **DTOs Anidados Complejos**

**Problema**: `RegistroIngresoRequest` tiene DTOs anidados que hacen difícil la validación.

**Ubicación**: `tfi/application/dto/RegistroIngresoRequest.java`

```java
// ❌ MAL - DTOs anidados complejos
private RegistroPacienteRequest.DomicilioRequest pacienteDomicilio;
private RegistroPacienteRequest.AfiliadoRequest pacienteObraSocial;
```

**Problemas**:
- Dificulta la validación
- Hace el código más complejo
- Viola el principio de simplicidad

---

## 🔧 Problemas en Servicios

### 18. **Servicios de Aplicación con Demasiada Responsabilidad**

**Problema**: Los servicios de aplicación hacen demasiado.

**Ejemplo**: `IngresoService.registrarIngreso()` tiene 100+ líneas y:
- Busca/crea pacientes
- Valida datos
- Crea value objects
- Crea entidades
- Persiste
- Agrega a cola
- Mapea a DTO

**Ubicación**: `tfi/application/service/IngresoService.java:56-140`

**Impacto**:
- Dificulta el testing
- Viola Single Responsibility Principle
- Dificulta el mantenimiento

---

### 19. **Singleton Manual en `ColaAtencionService`**

**Problema**: `ColaAtencionService` usa patrón Singleton manual con `getInstance()`.

**Ubicación**: `tfi/application/service/IngresoService.java:45`

```java
// ❌ MAL - Singleton manual
this.colaAtencionService = ColaAtencionService.getInstance();
```

**Problemas**:
- Dificulta el testing (no se puede mockear fácilmente)
- Viola Dependency Injection
- Acoplamiento fuerte

**Debería ser**:
```java
// ✅ BIEN - Inyección de dependencias
public IngresoService(..., ColaAtencionService colaAtencionService) {
    this.colaAtencionService = colaAtencionService;
}
```

---

### 20. **Falta de Manejo de Transacciones Explícito**

**Problema**: No hay anotaciones `@Transactional` en métodos que modifican múltiples entidades.

**Ejemplo**: `IngresoService.registrarIngreso()` modifica paciente e ingreso pero no está marcado como transaccional.

**Impacto**:
- Puede haber inconsistencias si falla a mitad de operación
- No hay rollback automático

---

### 21. **Servicios que Expresan Entidades del Dominio**

**Problema**: Los servicios retornan entidades del dominio en lugar de solo DTOs.

**Ejemplo**: `IngresoService.obtenerColaDeAtencion()` retorna `List<Ingreso>` en lugar de `List<IngresoResponse>`.

**Ubicación**: `tfi/application/service/IngresoService.java:151`

**Impacto**:
- Expone el modelo de dominio fuera de la capa de aplicación
- Viola el principio de encapsulación

---

## 💾 Problemas en Repositorios

### 22. **Interfaz de Repositorio con Dependencia de Spring**

**Problema**: `PacientesRepository` usa tipos de Spring Data.

**Ubicación**: `tfi/domain/repository/PacientesRepository.java`

```java
// ❌ MAL - El dominio depende de Spring
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PacientesRepository {
    Page<Paciente> findAll(Pageable pageable);
}
```

**Solución**: Crear abstracciones propias:
```java
// ✅ BIEN - Abstracción propia
public class PaginationRequest {
    private int page;
    private int size;
    // ...
}

public class PaginatedResult<T> {
    private List<T> content;
    private int totalElements;
    // ...
}
```

---

### 23. **Falta de Especificaciones (Specifications)**

**Problema**: No hay patrón Specification para queries complejas.

**Ejemplo**: Si necesitas buscar ingresos por múltiples criterios, tendrías que agregar métodos al repositorio.

**Mejora**: Implementar patrón Specification:
```java
public interface IngresoSpecification {
    boolean isSatisfiedBy(Ingreso ingreso);
}

public class IngresosPendientesSpecification implements IngresoSpecification {
    public boolean isSatisfiedBy(Ingreso ingreso) {
        return ingreso.getEstado() == Estado.PENDIENTE;
    }
}
```

---

## 🗄️ Problemas en Modelo de Base de Datos

### 24. **Desajuste entre Modelo de Dominio y Esquema de BD**

**Problema**: El esquema de BD no refleja bien el modelo de dominio.

**Ejemplos**:
- `ingresos` no tiene columna `atencion_id` (aunque hay referencia en `atenciones.ingreso_id`)
- `pacientes` tiene campos planos para `domicilio` en lugar de una tabla separada
- `pacientes` tiene campos planos para `obra_social` en lugar de una tabla separada

**Ubicación**: `backend/src/main/resources/schema.sql`

**Problemas**:
- Dificulta el mapeo objeto-relacional
- No refleja las relaciones del dominio
- Puede causar inconsistencias

---

### 25. **Falta de Campos de Auditoría**

**Problema**: No hay campos de auditoría como `created_at`, `updated_at`, `created_by`, `updated_by`.

**Impacto**:
- No se puede rastrear quién hizo qué y cuándo
- Dificulta el debugging
- No cumple con requisitos de auditoría

---

### 26. **Falta de Soft Delete**

**Problema**: No hay soporte para soft delete (eliminación lógica).

**Impacto**:
- No se puede recuperar datos eliminados
- No se puede auditar eliminaciones
- Dificulta el cumplimiento de regulaciones

---

### 27. **Falta de Versionado (Optimistic Locking)**

**Problema**: No hay campos de versión para optimistic locking.

**Impacto**:
- Puede haber problemas de concurrencia
- No se detectan actualizaciones concurrentes
- Puede causar pérdida de datos

---

## 🔀 Problemas de Separación de Responsabilidades

### 28. **Mappers con Lógica de Negocio**

**Problema**: Los mappers tienen lógica de negocio en lugar de solo mapeo.

**Ejemplo**: `IngresoMapper.toResponse()` tiene lógica condicional compleja.

**Ubicación**: `tfi/application/mapper/IngresoMapper.java`

```java
// ❌ MAL - Lógica condicional en mapper
ingreso.getPaciente() != null ? ingreso.getPaciente().getCuil() : null
```

**Mejora**: Los mappers deberían ser simples transformaciones sin lógica.

---

### 29. **Controladores con Lógica de Negocio**

**Problema**: Los controladores tienen validaciones que deberían estar en el dominio.

**Ejemplo**: `AutenticacionController` valida autoridad pero también podría tener más lógica.

---

### 30. **Falta de Capa de Dominio para Lógica Transversal**

**Problema**: No hay una capa clara para lógica que cruza múltiples agregados.

**Ejemplo**: La lógica de "verificar afiliación antes de crear paciente" está en el servicio de aplicación.

**Mejora**: Crear un servicio de dominio `VerificacionAfiliacionService` en `domain.service`.

---

## ✅ Recomendaciones de Mejora

### 1. **Refactorizar Entidades para Rich Domain Model**

**Acción**: Agregar métodos de negocio a las entidades.

**Ejemplo para `Ingreso`**:
```java
public class Ingreso {
    // ...
    
    public void iniciarAtencion() {
        if (this.estado != Estado.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden iniciar ingresos PENDIENTES");
        }
        this.estado = Estado.EN_PROCESO;
    }
    
    public void finalizar(Atencion atencion) {
        if (this.estado != Estado.EN_PROCESO) {
            throw new IllegalStateException("Solo se pueden finalizar ingresos EN_PROCESO");
        }
        if (atencion == null) {
            throw new IllegalArgumentException("Un ingreso debe tener atención para finalizar");
        }
        this.atencion = atencion;
        this.estado = Estado.FINALIZADO;
    }
    
    public boolean estaPendiente() {
        return this.estado == Estado.PENDIENTE;
    }
    
    public boolean puedeSerAtendido() {
        return estaPendiente() && this.paciente != null && this.enfermero != null;
    }
}
```

---

### 2. **Definir Agregados y Agregados Raíz**

**Acción**: Identificar y documentar agregados.

**Agregados propuestos**:
- **Agregado `Ingreso`** (Raíz)
  - Contiene: `Ingreso`, `Atencion`
  - Invariantes: Un ingreso solo puede tener una atención, debe estar en estado válido
  
- **Agregado `Paciente`** (Raíz)
  - Contiene: `Paciente`, `Afiliado`
  - Invariantes: Un paciente debe tener CUIL único, afiliación válida si existe

- **Agregado `Usuario`** (Raíz)
  - Contiene: `Usuario`
  - Invariantes: Email único, CUIL único, matrícula única

---

### 3. **Eliminar Dependencias de Spring del Dominio**

**Acción**: Crear abstracciones propias.

**Ejemplo**:
```java
// domain/repository/PacientesRepository.java
public interface PacientesRepository {
    PaginatedResult<Paciente> findAll(PaginationRequest request);
    // ...
}

// domain/valueObject/PaginationRequest.java
public class PaginationRequest {
    private final int page;
    private final int size;
    private final SortOrder sortOrder;
    // ...
}

// domain/valueObject/PaginatedResult.java
public class PaginatedResult<T> {
    private final List<T> content;
    private final int totalElements;
    private final int totalPages;
    // ...
}
```

---

### 4. **Mover Lógica de Negocio al Dominio**

**Acción**: Extraer lógica de servicios de aplicación al dominio.

**Ejemplo**: Crear factory methods en entidades:
```java
// domain/entity/Paciente.java
public class Paciente {
    // ...
    
    public static Paciente crearDesdeRegistro(
            String cuil, 
            String nombre, 
            String apellido,
            Email email,
            Domicilio domicilio,
            Afiliado afiliado) {
        
        // Validaciones de negocio aquí
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        // ...
        
        return new Paciente(cuil, nombre, apellido, email, domicilio, afiliado);
    }
}
```

---

### 5. **Implementar Value Objects Inmutables Correctamente**

**Acción**: Hacer todos los value objects inmutables.

**Ejemplo para `Afiliado`**:
```java
public class Afiliado {
    private final ObraSocial obraSocial;
    private final String numeroAfiliado;
    
    public Afiliado(ObraSocial obraSocial, String numeroAfiliado) {
        if (obraSocial == null) {
            throw new IllegalArgumentException("La obra social no puede ser nula");
        }
        if (numeroAfiliado == null || numeroAfiliado.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de afiliado es obligatorio");
        }
        this.obraSocial = obraSocial;
        this.numeroAfiliado = numeroAfiliado.trim();
    }
    
    // Solo getters, sin setters
    public ObraSocial getObraSocial() {
        return obraSocial;
    }
    
    public String getNumeroAfiliado() {
        return numeroAfiliado;
    }
    
    @Override
    public boolean equals(Object o) {
        // Implementación de equals
    }
    
    @Override
    public int hashCode() {
        // Implementación de hashCode
    }
}
```

---

### 6. **Crear Servicios de Dominio**

**Acción**: Mover lógica que involucra múltiples entidades a servicios de dominio.

**Estructura propuesta**:
```
domain/
├── entity/
├── valueObject/
├── repository/
└── service/          # ← NUEVO
    ├── VerificacionAfiliacionService.java
    ├── CalculoNivelEmergenciaService.java
    └── ColaAtencionService.java  # ← Mover desde application
```

---

### 7. **Mejorar el Modelo de Base de Datos**

**Acción**: Alinear el esquema con el modelo de dominio.

**Mejoras**:
- Agregar tabla `domicilios` si es necesario
- Agregar campos de auditoría (`created_at`, `updated_at`, etc.)
- Agregar soporte para soft delete
- Agregar versionado para optimistic locking

---

### 8. **Implementar Patrón Specification**

**Acción**: Crear specifications para queries complejas.

**Ejemplo**:
```java
// domain/specification/IngresoSpecification.java
public interface IngresoSpecification {
    boolean isSatisfiedBy(Ingreso ingreso);
}

// domain/specification/IngresosPendientesSpecification.java
public class IngresosPendientesSpecification implements IngresoSpecification {
    @Override
    public boolean isSatisfiedBy(Ingreso ingreso) {
        return ingreso.estaPendiente();
    }
}

// domain/repository/IngresoRepository.java
public interface IngresoRepository {
    List<Ingreso> findBySpecification(IngresoSpecification spec);
}
```

---

### 9. **Separar Responsabilidades en Servicios de Aplicación**

**Acción**: Dividir servicios grandes en servicios más pequeños.

**Ejemplo**: `IngresoService` podría dividirse en:
- `RegistroIngresoService` - Solo para registrar
- `ConsultaIngresoService` - Solo para consultar
- `GestionColaAtencionService` - Solo para gestión de cola

---

### 10. **Agregar Manejo de Transacciones**

**Acción**: Marcar métodos que modifican datos como `@Transactional`.

**Ejemplo**:
```java
@Service
public class IngresoService {
    
    @Transactional
    public IngresoResponse registrarIngreso(RegistroIngresoRequest request) {
        // ...
    }
}
```

---

### 11. **Mejorar el Manejo de Errores**

**Acción**: Crear excepciones de dominio específicas.

**Estructura propuesta**:
```
domain/
└── exception/
    ├── DomainException.java (base)
    ├── IngresoException.java
    ├── PacienteException.java
    └── AtencionException.java
```

---

### 12. **Documentar Agregados y Bounded Contexts**

**Acción**: Crear documentación clara de:
- Agregados y sus raíces
- Invariantes de cada agregado
- Límites de contexto (si aplica)

---

## 📊 Resumen de Problemas por Categoría

| Categoría | Cantidad | Severidad |
|-----------|----------|-----------|
| Arquitectura | 4 | 🔴 Crítica |
| DDD | 3 | 🟠 Alta |
| Entidades | 4 | 🟠 Alta |
| Value Objects | 3 | 🟡 Media |
| DTOs | 3 | 🟡 Media |
| Servicios | 4 | 🟠 Alta |
| Repositorios | 2 | 🟠 Alta |
| Base de Datos | 4 | 🟡 Media |
| Separación de Responsabilidades | 3 | 🟠 Alta |
| **TOTAL** | **30** | |

---

## 🎯 Priorización de Mejoras

### Prioridad ALTA (Hacer primero)
1. ✅ Refactorizar entidades para Rich Domain Model
2. ✅ Eliminar dependencias de Spring del dominio
3. ✅ Mover lógica de negocio al dominio
4. ✅ Definir agregados y agregados raíz
5. ✅ Implementar métodos de negocio en entidades

### Prioridad MEDIA (Hacer después)
6. ✅ Crear servicios de dominio
7. ✅ Mejorar value objects (inmutabilidad)
8. ✅ Separar responsabilidades en servicios
9. ✅ Agregar manejo de transacciones
10. ✅ Mejorar modelo de base de datos

### Prioridad BAJA (Mejoras futuras)
11. ✅ Implementar patrón Specification
12. ✅ Agregar campos de auditoría
13. ✅ Implementar soft delete
14. ✅ Mejorar manejo de errores

---

## 📚 Referencias y Recursos

- **Clean Architecture** - Robert C. Martin
- **Domain-Driven Design** - Eric Evans
- **Implementing Domain-Driven Design** - Vaughn Vernon
- **Architecture Patterns with Python** - Harry Percival

---

*Análisis generado el: $(date)*
*Proyecto: TFI_IW - Sistema de Gestión de Urgencias*

