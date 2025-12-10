# 🔍 Análisis de Inconsistencias - Backend DDD y Arquitectura Limpia

## 📋 Resumen Ejecutivo

Este documento identifica inconsistencias en el backend que violan principios de DDD (Domain-Driven Design) y Arquitectura Limpia, así como buenas prácticas de ingeniería de software.

---

## 🚨 Problemas Críticos de Arquitectura

### 1. **Violación de Separación de Capas en Controladores**

**Problema**: Los controladores acceden directamente a repositorios y crean entidades del dominio, violando la separación de responsabilidades.

**Ubicación**: `tfi/controller/IngresoController.java`

**Ejemplo**:
```java
// ❌ MAL - El controlador accede directamente a repositorios
@PutMapping("/{id}")
public ResponseEntity<IngresoResponse> actualizarIngreso(...) {
    Paciente paciente = pacientesRepository.findByCuil(...);  // ❌ Acceso directo
    Usuario enfermero = usuarioRepository.findByCuil(...);    // ❌ Acceso directo
    
    Ingreso ingresoActualizado = new Ingreso(...);  // ❌ Creación de entidad en controlador
    ingresoActualizado.setId(id);
    
    Ingreso ingresoGuardado = ingresoService.actualizarIngreso(ingresoActualizado);
    // ...
}
```

**Problemas**:
- El controlador contiene lógica de negocio (búsqueda de entidades, creación de entidades)
- Viola el principio de que los controladores solo deben delegar a servicios
- Dificulta el testing y mantenimiento
- Expone detalles de implementación del dominio

**Solución**: Mover toda la lógica al servicio de aplicación:
```java
// ✅ BIEN - El controlador solo delega
@PutMapping("/{id}")
public ResponseEntity<IngresoResponse> actualizarIngreso(
        @PathVariable String id,
        @Valid @RequestBody RegistroIngresoRequest request,
        HttpServletRequest httpRequest) {
    
    IngresoResponse response = ingresoService.actualizarIngreso(id, request);
    return ResponseEntity.ok(response);
}
```

---

### 2. **ColaAtencionService en Capa Incorrecta**

**Problema**: `ColaAtencionService` está en la capa de aplicación pero contiene lógica de dominio (gestión de cola de prioridad).

**Ubicación**: `tfi/application/service/ColaAtencionService.java`

**Problemas**:
- La lógica de priorización es parte del dominio, no de la aplicación
- Viola la separación entre lógica de dominio y casos de uso
- Dificulta la reutilización de la lógica de cola

**Solución**: Mover a `tfi/domain/service/ColaAtencionService.java` o crear un agregado `ColaAtencion` en el dominio.

---

### 3. **Uso de Singleton en Servicio de Aplicación**

**Problema**: `ColaAtencionService` implementa patrón Singleton manual, lo cual es problemático en aplicaciones Spring.

**Ubicación**: `tfi/application/service/ColaAtencionService.java:19-44`

**Problemas**:
- Spring ya maneja el ciclo de vida de beans (singleton por defecto)
- El singleton manual puede causar problemas en testing
- Mezcla de responsabilidades (gestión de instancia + lógica de negocio)

**Solución**: Usar `@Service` de Spring y eliminar el patrón Singleton manual.

---

## 🏗️ Problemas en Entidades del Dominio

### 4. **Entidad Ingreso con Demasiados Setters Públicos**

**Problema**: La entidad `Ingreso` tiene muchos setters públicos que permiten modificar el estado sin validación.

**Ubicación**: `tfi/domain/entity/Ingreso.java`

**Ejemplo**:
```java
// ❌ MAL - Setters públicos sin validación
public void setPaciente(Paciente paciente) { ... }
public void setEnfermero(Usuario enfermero) { ... }
public void setDescripcion(String descripcion) { ... }
public void setTemperatura(Temperatura temperatura) { ... }
// ... muchos más
```

**Problemas**:
- Permite modificar el estado sin validar invariantes
- Viola el principio de encapsulación
- Aunque algunos están marcados como `@Deprecated`, aún están disponibles

**Solución**: 
- Eliminar setters públicos (excepto `setId` para repositorios)
- Usar métodos de negocio para cambios de estado
- Usar reflection o métodos package-private para repositorios si es necesario

---

### 5. **Entidad Atencion con IDs en lugar de Referencias**

**Problema**: `Atencion` usa `String ingresoId` y `String medicoId` en lugar de referencias a entidades.

**Ubicación**: `tfi/domain/entity/Atencion.java:14-15`

**Ejemplo**:
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

**Solución**: Usar referencias a entidades:
```java
// ✅ BIEN - Referencias a entidades
private Ingreso ingreso;
private Usuario medico;
```

**Nota**: Si hay problemas de persistencia (lazy loading, etc.), considerar usar IDs solo en la capa de infraestructura.

---

### 6. **Setters Deprecated Aún Disponibles**

**Problema**: Aunque los setters están marcados como `@Deprecated`, siguen siendo públicos y pueden usarse incorrectamente.

**Ubicación**: Múltiples entidades (`Paciente`, `Ingreso`, `Atencion`, `Usuario`)

**Problemas**:
- `@Deprecated` solo advierte, no previene el uso
- Los repositorios pueden seguir usando setters en lugar de métodos de negocio
- No hay garantía de que se respeten las invariantes

**Solución**: 
- Hacer los setters package-private o protected
- Usar reflection en repositorios si es necesario
- O mejor: usar mappers específicos para repositorios

---

### 7. **PacienteService.actualizar() Crea Nueva Entidad**

**Problema**: El método `actualizar()` crea un nuevo `Paciente` en lugar de usar métodos de negocio de la entidad existente.

**Ubicación**: `tfi/application/service/PacienteService.java:265-273`

**Ejemplo**:
```java
// ❌ MAL - Crea nueva entidad en lugar de actualizar existente
Paciente pacienteActualizado = Paciente.crearCompleto(
    dto.getCuil(),
    dto.getNombre(),
    dto.getApellido(),
    pacienteExistente.getEmail(), 
    domicilio,
    afiliado
);
pacienteActualizado.setId(pacienteExistente.getId());
```

**Problemas**:
- No usa los métodos de negocio de la entidad (`actualizarDatosPersonales()`, `actualizarEmail()`, etc.)
- Pierde el historial y estado interno de la entidad
- Puede perder validaciones y reglas de negocio

**Solución**: Usar métodos de negocio de la entidad:
```java
// ✅ BIEN - Usa métodos de negocio
pacienteExistente.actualizarDatosPersonales(dto.getNombre(), dto.getApellido());
pacienteExistente.actualizarDomicilio(domicilio);
pacienteExistente.actualizarObraSocial(afiliado);
pacientesRepository.update(pacienteExistente);
```

---

### 8. **Falta de Validación de Invariantes en Ingreso**

**Problema**: El constructor de `Ingreso` no valida todas las invariantes del dominio.

**Ubicación**: `tfi/domain/entity/Ingreso.java:32-69`

**Problemas**:
- No valida que el paciente no sea null
- No valida que el enfermero no sea null
- No valida que el enfermero tenga autoridad ENFERMERO
- Permite crear ingresos en estados inválidos

**Solución**: Agregar validaciones en el constructor:
```java
public Ingreso(Paciente paciente, Usuario enfermero, ...) {
    if (paciente == null) {
        throw new IllegalArgumentException("El paciente es obligatorio");
    }
    if (enfermero == null) {
        throw new IllegalArgumentException("El enfermero es obligatorio");
    }
    if (!enfermero.esEnfermero()) {
        throw new IllegalArgumentException("El usuario debe ser un enfermero");
    }
    // ... resto de validaciones
}
```

---

## 🔄 Problemas en Servicios de Aplicación

### 9. **Lógica de Negocio en Servicios de Aplicación**

**Problema**: Los servicios de aplicación contienen lógica de negocio que debería estar en el dominio.

**Ubicación**: `tfi/application/service/IngresoService.java:56-140`

**Ejemplo**: El método `registrarIngreso()` contiene lógica compleja de creación de pacientes que debería estar en el dominio o en un servicio de dominio.

**Problemas**:
- Viola la separación entre lógica de aplicación y lógica de dominio
- Dificulta el testing de la lógica de negocio
- Hace que los servicios sean difíciles de mantener

**Solución**: 
- Mover lógica de creación de pacientes a un método de dominio o servicio de dominio
- Los servicios de aplicación solo deben orquestar llamadas

---

### 10. **Servicios que Retornan Entidades del Dominio**

**Problema**: Algunos servicios retornan entidades del dominio directamente en lugar de DTOs.

**Ubicación**: `tfi/application/service/IngresoService.java:151-153`

**Ejemplo**:
```java
// ❌ MAL - Retorna entidad del dominio
public List<Ingreso> obtenerColaDeAtencion() {
    return this.colaAtencionService.obtenerCola();
}
```

**Problemas**:
- Expone el modelo de dominio fuera de la capa de aplicación
- Viola el principio de encapsulación
- Dificulta cambios en el modelo de dominio sin afectar otras capas

**Solución**: Retornar DTOs:
```java
// ✅ BIEN - Retorna DTOs
public List<IngresoResponse> obtenerColaDeAtencion() {
    return this.colaAtencionService.obtenerCola().stream()
        .map(ingresoMapper::toResponse)
        .collect(Collectors.toList());
}
```

---

### 11. **Inyección de Dependencias Inconsistente**

**Problema**: `IngresoService` tiene una dependencia inconsistente con `ColaAtencionService`.

**Ubicación**: `tfi/application/service/IngresoService.java:34-45`

**Ejemplo**:
```java
// ❌ MAL - Mezcla inyección por constructor con Singleton manual
public IngresoService(..., IngresoMapper ingresoMapper) {
    // ...
    this.colaAtencionService = ColaAtencionService.getInstance();  // ❌ Singleton manual
}
```

**Problemas**:
- Mezcla dos patrones de inyección de dependencias
- Dificulta el testing (no se puede mockear fácilmente)
- Viola el principio de inversión de dependencias

**Solución**: Inyectar `ColaAtencionService` por constructor:
```java
// ✅ BIEN - Inyección consistente
public IngresoService(..., ColaAtencionService colaAtencionService) {
    // ...
    this.colaAtencionService = colaAtencionService;
}
```

---

## 📦 Problemas en Repositorios

### 12. **Falta de Métodos de Búsqueda Específicos**

**Problema**: Algunos repositorios no tienen métodos de búsqueda específicos que podrían ser útiles.

**Ubicación**: `tfi/domain/repository/IngresoRepository.java`

**Ejemplos de métodos faltantes**:
- `findByEstado(Estado estado)` - Para buscar ingresos por estado
- `findByPaciente(Paciente paciente)` - Para buscar ingresos de un paciente
- `findByEnfermero(Usuario enfermero)` - Para buscar ingresos de un enfermero

**Problemas**:
- Fuerza a traer todos los ingresos y filtrar en memoria
- Ineficiente para grandes volúmenes de datos
- No aprovecha índices de base de datos

**Solución**: Agregar métodos específicos al repositorio según necesidades del dominio.

---

## 🎯 Problemas en DTOs

### 13. **DTOs con Lógica de Negocio**

**Problema**: Algunos DTOs podrían tener validaciones que deberían estar en el dominio.

**Recomendación**: Revisar que los DTOs solo contengan validaciones de formato (anotaciones de Bean Validation), no reglas de negocio.

---

## 🔍 Problemas de Consistencia

### 14. **Inconsistencia en Manejo de Errores**

**Problema**: Algunos métodos lanzan `IllegalArgumentException`, otros `IllegalStateException`, y otros excepciones personalizadas.

**Recomendación**: 
- Usar excepciones de dominio para errores de negocio
- Usar `IllegalArgumentException` para parámetros inválidos
- Usar `IllegalStateException` para estados inválidos
- Documentar claramente qué excepción usar en cada caso

---

### 15. **Falta de Documentación en Algunos Métodos**

**Problema**: No todos los métodos públicos tienen documentación Javadoc completa.

**Recomendación**: Agregar documentación Javadoc a todos los métodos públicos, especialmente:
- Qué hace el método
- Qué parámetros recibe y qué validaciones hace
- Qué retorna
- Qué excepciones puede lanzar

---

## ✅ Aspectos Positivos

1. **Buen uso de Value Objects**: `Cuil`, `Email`, `Domicilio`, etc. están bien implementados como inmutables.
2. **Separación de capas**: La estructura general sigue DDD y Arquitectura Limpia.
3. **Factory Methods**: `Paciente` tiene buenos factory methods.
4. **Métodos de negocio**: Algunas entidades tienen métodos de negocio bien implementados (`Ingreso.iniciarAtencion()`, `Ingreso.finalizar()`, etc.).
5. **Abstracciones propias**: `PaginationRequest` y `PaginatedResult` son buenas abstracciones independientes de Spring.

---

## 📝 Recomendaciones Prioritarias

### Prioridad Alta (Crítico)
1. ✅ Mover lógica de `IngresoController` al servicio de aplicación
2. ✅ Eliminar acceso directo a repositorios desde controladores
3. ✅ Corregir `PacienteService.actualizar()` para usar métodos de negocio
4. ✅ Mover `ColaAtencionService` al dominio o refactorizar

### Prioridad Media (Importante)
5. ✅ Reducir setters públicos en entidades
6. ✅ Cambiar `Atencion` para usar referencias en lugar de IDs
7. ✅ Agregar validaciones de invariantes en constructores
8. ✅ Hacer que servicios retornen DTOs en lugar de entidades

### Prioridad Baja (Mejoras)
9. ✅ Agregar métodos de búsqueda específicos a repositorios
10. ✅ Estandarizar manejo de excepciones
11. ✅ Completar documentación Javadoc
12. ✅ Eliminar patrón Singleton manual de `ColaAtencionService`

---

## 📚 Referencias

- **DDD**: Domain-Driven Design - Eric Evans
- **Clean Architecture**: Robert C. Martin
- **Rich Domain Model**: Martin Fowler
- **SOLID Principles**: Robert C. Martin

