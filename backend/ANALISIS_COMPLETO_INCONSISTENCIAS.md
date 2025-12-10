# 🔍 Análisis Completo de Inconsistencias y Malas Prácticas

## 📋 Resumen Ejecutivo

Este documento identifica todas las inconsistencias, malas prácticas y violaciones de principios DDD y Arquitectura Limpia encontradas en el código del backend. El análisis cubre entidades, DTOs, repositorios, servicios y controladores.

---

## 🚨 PROBLEMAS CRÍTICOS DE ARQUITECTURA

### 1. **ColaAtencionService: Patrón Singleton Manual + @Service**

**Ubicación**: `tfi/application/service/ColaAtencionService.java`

**Problemas**:
- Implementa patrón Singleton manual (`getInstance()`) mientras también tiene `@Service` de Spring
- Spring ya maneja el ciclo de vida como singleton por defecto
- Mezcla dos patrones de gestión de instancias
- Dificulta el testing (no se puede mockear fácilmente)
- Viola el principio de inversión de dependencias
- El método `getInstance()` se usa en `IngresoService` en lugar de inyección por constructor

**Código problemático**:
```java
@Service
public class ColaAtencionService {
    private static ColaAtencionService instancia;
    
    private ColaAtencionService() { ... }
    
    public static synchronized ColaAtencionService getInstance() {
        if (instancia == null) {
            instancia = new ColaAtencionService();
        }
        return instancia;
    }
}
```

**Solución**: 
- Eliminar el patrón Singleton manual
- Eliminar `getInstance()` y `resetInstance()`
- Inyectar `ColaAtencionService` por constructor en `IngresoService`
- Usar solo `@Service` de Spring

---

### 2. **ColaAtencionService en Capa Incorrecta**

**Ubicación**: `tfi/application/service/ColaAtencionService.java`

**Problemas**:
- La gestión de cola de prioridad es lógica de dominio, no de aplicación
- Debería estar en `tfi/domain/service/` o como parte de un agregado
- Viola la separación entre lógica de dominio y casos de uso

**Solución**: 
- Mover a `tfi/domain/service/ColaAtencionService.java`
- O crear un agregado `ColaAtencion` en el dominio

---

### 3. **IngresoService: Inyección Inconsistente de Dependencias**

**Ubicación**: `tfi/application/service/IngresoService.java:38-46`

**Problemas**:
- Mezcla inyección por constructor con Singleton manual
- `ColaAtencionService` se obtiene mediante `getInstance()` en lugar de inyección

**Código problemático**:
```java
public IngresoService(PacientesRepository pacientesRepository, 
                      UsuarioRepository usuarioRepository, 
                      IngresoRepository ingresoRepository,
                      IngresoMapper ingresoMapper) {
    // ...
    this.colaAtencionService = ColaAtencionService.getInstance(); // ❌ Singleton manual
}
```

**Solución**: 
```java
public IngresoService(PacientesRepository pacientesRepository, 
                      UsuarioRepository usuarioRepository, 
                      IngresoRepository ingresoRepository,
                      ColaAtencionService colaAtencionService, // ✅ Inyección
                      IngresoMapper ingresoMapper) {
    // ...
    this.colaAtencionService = colaAtencionService;
}
```

---

## 🏗️ PROBLEMAS EN ENTIDADES DEL DOMINIO

### 4. **Ingreso: Demasiados Setters Públicos**

**Ubicación**: `tfi/domain/entity/Ingreso.java`

**Problemas**:
- Múltiples setters públicos permiten modificar estado sin validación
- Setters como `setPaciente()`, `setEnfermero()`, `setDescripcion()`, etc. no validan invariantes
- Aunque algunos están `@Deprecated`, siguen siendo públicos y accesibles
- Viola el principio de encapsulación y Rich Domain Model

**Setters problemáticos**:
```java
public void setPaciente(Paciente paciente) { ... } // ❌ Sin validación
public void setEnfermero(Usuario enfermero) { ... } // ❌ Sin validación
public void setDescripcion(String descripcion) { ... } // ❌ Sin validación
public void setTemperatura(Temperatura temperatura) { ... } // ❌ Sin validación
// ... muchos más
```

**Solución**: 
- Eliminar setters públicos (excepto `setId` para repositorios)
- Usar métodos de negocio para cambios de estado
- Hacer setters package-private si son necesarios para repositorios
- O mejor: usar reflection o mappers específicos para repositorios

---

### 5. **Ingreso: Falta de Validación en Constructor**

**Ubicación**: `tfi/domain/entity/Ingreso.java:32-69`

**Problemas**:
- No valida que `paciente` no sea null
- No valida que `enfermero` no sea null
- No valida que `enfermero` tenga autoridad ENFERMERO
- Permite crear ingresos en estados inválidos

**Solución**: 
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

### 6. **Atencion: Usa IDs en lugar de Referencias**

**Ubicación**: `tfi/domain/entity/Atencion.java:14-15`

**Problemas**:
- Usa `String ingresoId` y `String medicoId` en lugar de referencias a entidades
- No hay validación de que el ingreso existe
- No hay validación de que el médico es válido
- Dificulta el razonamiento sobre el dominio
- Viola el principio de Rich Domain Model

**Código problemático**:
```java
private String ingresoId;  // ❌ ID en lugar de referencia
private String medicoId;   // ❌ ID en lugar de referencia
```

**Solución**: 
```java
private Ingreso ingreso;   // ✅ Referencia a entidad
private Usuario medico;    // ✅ Referencia a entidad
```

**Nota**: Si hay problemas de persistencia (lazy loading, etc.), considerar usar IDs solo en la capa de infraestructura mediante un mapper.

---

### 7. **Paciente: Setters Deprecated Aún Públicos**

**Ubicación**: `tfi/domain/entity/Paciente.java:217-270`

**Problemas**:
- Aunque están `@Deprecated`, los setters siguen siendo públicos
- `@Deprecated` solo advierte, no previene el uso
- Los repositorios pueden seguir usando setters en lugar de métodos de negocio
- No hay garantía de que se respeten las invariantes

**Solución**: 
- Hacer los setters package-private o protected
- Usar reflection en repositorios si es necesario
- O mejor: usar mappers específicos para repositorios

---

### 8. **Usuario: Dos Constructores Inconsistentes**

**Ubicación**: `tfi/domain/entity/Usuario.java:39-88`

**Problemas**:
- Constructor completo requiere todos los campos
- Constructor básico permite crear usuarios sin datos personales (cuil, nombre, apellido, matricula pueden ser null)
- Se pueden crear usuarios en estados inconsistentes
- No está claro cuándo usar cada constructor

**Solución**: 
- Unificar en un solo constructor con parámetros opcionales
- O usar factory methods con nombres descriptivos
- Validar que si se proporcionan datos personales, todos sean completos

---

### 9. **Ingreso.actualizarIngreso(): Crea Nueva Entidad en lugar de Actualizar**

**Ubicación**: `tfi/application/service/IngresoService.java:270-320`

**Problemas**:
- Crea un nuevo `Ingreso` en lugar de usar métodos de negocio de la entidad existente
- No usa métodos como `actualizarVitales()`, `actualizarDescripcion()`, etc.
- Pierde el historial y estado interno de la entidad
- Puede perder validaciones y reglas de negocio

**Código problemático**:
```java
Ingreso ingresoActualizado = new Ingreso(
    ingresoExistente.getAtencion(),
    paciente,
    enfermero,
    request.getDescripcion(),
    // ...
);
ingresoActualizado.setId(id);
```

**Solución**: 
- Agregar métodos de negocio a `Ingreso`:
  - `actualizarVitales(Temperatura, TensionArterial, ...)`
  - `actualizarDescripcion(String)`
  - `actualizarNivelEmergencia(NivelEmergencia)`
- Usar estos métodos en lugar de crear nueva entidad

---

### 10. **PacienteService.actualizar(): Crea Nueva Entidad**

**Ubicación**: `tfi/application/service/PacienteService.java:265-290`

**Problemas**:
- Crea un nuevo `Paciente` en lugar de usar métodos de negocio
- No usa `actualizarDatosPersonales()`, `actualizarDomicilio()`, `actualizarObraSocial()`
- Pierde el historial y estado interno

**Código problemático**:
```java
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

**Solución**: 
```java
pacienteExistente.actualizarDatosPersonales(dto.getNombre(), dto.getApellido());
pacienteExistente.actualizarDomicilio(domicilio);
pacienteExistente.actualizarObraSocial(afiliado);
pacientesRepository.update(pacienteExistente);
```

---

## 🔄 PROBLEMAS EN SERVICIOS DE APLICACIÓN

### 11. **IngresoService: Lógica de Creación de Paciente en Servicio**

**Ubicación**: `tfi/application/service/IngresoService.java:56-141`

**Problemas**:
- Contiene lógica compleja de creación de pacientes (líneas 59-111)
- Esta lógica debería estar en un servicio de dominio o en la entidad `Paciente`
- Viola la separación entre lógica de aplicación y lógica de dominio
- Dificulta el testing y mantenimiento

**Solución**: 
- Extraer la lógica de creación a un método privado o servicio de dominio
- O mejor: crear un factory method en `Paciente` que maneje todos los casos

---

### 12. **IngresoService: Métodos Deprecated Públicos**

**Ubicación**: `tfi/application/service/IngresoService.java:189-211`

**Problemas**:
- Métodos `eliminarIngresoInterno()` y `actualizarIngresoInterno()` están marcados como `@Deprecated` pero son privados
- Si son privados y deprecated, deberían eliminarse
- Si son necesarios, no deberían estar deprecated

**Solución**: 
- Eliminar métodos deprecated si no se usan
- O renombrarlos y documentarlos correctamente

---

### 13. **Servicios que Retornan Entidades del Dominio**

**Ubicación**: `tfi/application/service/ColaAtencionService.java:65`

**Problemas**:
- `obtenerCola()` retorna `List<Ingreso>` (entidades del dominio)
- Debería retornar DTOs para mantener el dominio encapsulado
- Viola el principio de que la capa de aplicación no debe exponer entidades

**Solución**: 
- Cambiar a retornar DTOs o mantener la lista interna y exponer solo métodos que retornen DTOs
- O mover `ColaAtencionService` al dominio y crear un servicio de aplicación que lo envuelva

---

## 📦 PROBLEMAS EN REPOSITORIOS

### 14. **IngresoRepository: Falta de Métodos de Búsqueda Específicos**

**Ubicación**: `tfi/domain/repository/IngresoRepository.java`

**Problemas**:
- Solo tiene `findAll()` y `findById()`
- Faltan métodos útiles como:
  - `findByEstado(Estado estado)`
  - `findByPaciente(Paciente paciente)`
  - `findByEnfermero(Usuario enfermero)`
  - `findByNivelEmergencia(NivelEmergencia nivel)`
- Fuerza a traer todos los ingresos y filtrar en memoria
- Ineficiente para grandes volúmenes de datos

**Solución**: 
- Agregar métodos específicos según necesidades del dominio
- Implementar en las capas de infraestructura (memory y postgres)

---

### 15. **Repositorios: Uso de Setters en lugar de Métodos de Negocio**

**Problema General**: Los repositorios pueden estar usando setters deprecated en lugar de métodos de negocio al recuperar desde BD.

**Recomendación**: 
- Revisar implementaciones de repositorios
- Usar reflection o mappers para reconstruir entidades
- O hacer setters package-private y documentar su uso

---

## 🎯 PROBLEMAS EN DTOs

### 16. **DTOs: Falta de Separación Clara Request/Response**

**Problemas**:
- Algunos DTOs podrían tener mejor separación entre request y response
- `RegistroIngresoRequest` tiene campos anidados que podrían simplificarse

**Recomendación**: 
- Revisar que todos los DTOs tengan responsabilidades claras
- Separar completamente request de response

---

### 17. **DTOs: Validaciones de Formato vs Reglas de Negocio**

**Problema**: 
- Los DTOs tienen validaciones de formato (Bean Validation)
- Pero algunas reglas de negocio se validan en servicios
- Esto está bien, pero debería estar documentado claramente

**Recomendación**: 
- Documentar qué validaciones están en DTOs (formato) y cuáles en dominio (reglas de negocio)

---

## 🔍 PROBLEMAS DE CONSISTENCIA

### 18. **Inconsistencia en Manejo de Errores**

**Problemas**:
- Algunos métodos lanzan `IllegalArgumentException`
- Otros lanzan `IllegalStateException`
- Otros lanzan excepciones personalizadas (`PacienteException`, `AtencionException`, etc.)
- No hay un estándar claro

**Recomendación**: 
- Usar excepciones de dominio para errores de negocio
- Usar `IllegalArgumentException` para parámetros inválidos
- Usar `IllegalStateException` para estados inválidos
- Documentar claramente qué excepción usar en cada caso

---

### 19. **Falta de Documentación Javadoc Completa**

**Problemas**:
- No todos los métodos públicos tienen documentación completa
- Faltan `@throws` en algunos métodos
- Faltan `@param` en algunos casos

**Recomendación**: 
- Agregar documentación Javadoc completa a todos los métodos públicos
- Especialmente: qué hace, parámetros, retorno, excepciones

---

### 20. **Inconsistencia en Validación de Nulls**

**Problemas**:
- Algunos métodos validan nulls al inicio
- Otros usan `Optional` y validan después
- No hay un estándar consistente

**Recomendación**: 
- Estandarizar el manejo de nulls
- Usar `Optional` en repositorios
- Validar nulls en servicios antes de llamar a repositorios

---

## 🎨 PROBLEMAS DE DISEÑO

### 21. **Falta de Value Objects para Conceptos del Dominio**

**Problemas**:
- `Ingreso.descripcion` es `String` - debería ser `DescripcionIngreso` (Value Object)
- `Atencion.informeMedico` es `String` - debería ser `InformeMedico` (Value Object)
- `Paciente.nombre` y `Paciente.apellido` son `String` - podrían ser `Nombre` y `Apellido`

**Recomendación**: 
- Crear Value Objects para conceptos importantes del dominio
- Esto mejora la expresividad y validación

---

### 22. **Value Objects sin Métodos de Negocio**

**Problemas**:
- Los value objects solo validan pero no tienen comportamiento
- `Temperatura` podría tener métodos como `esFiebre()`, `esHipotermia()`, `esNormal()`
- `TensionArterial` podría tener métodos como `esHipertension()`, `esHipotension()`

**Recomendación**: 
- Agregar métodos de negocio a value objects cuando tenga sentido
- Esto mejora el Rich Domain Model

---

## ✅ ASPECTOS POSITIVOS

1. **Buen uso de Value Objects**: `Cuil`, `Email`, `Domicilio`, etc. están bien implementados como inmutables
2. **Separación de capas**: La estructura general sigue DDD y Arquitectura Limpia
3. **Factory Methods**: `Paciente` tiene buenos factory methods
4. **Métodos de negocio**: Algunas entidades tienen métodos de negocio bien implementados (`Ingreso.iniciarAtencion()`, `Ingreso.finalizar()`, etc.)
5. **Abstracciones propias**: `PaginationRequest` y `PaginatedResult` son buenas abstracciones independientes de Spring
6. **Controladores limpios**: Los controladores están bien estructurados y solo delegan a servicios

---

## 📝 RECOMENDACIONES PRIORITARIAS

### Prioridad ALTA (Crítico - Afecta Arquitectura)

1. ✅ **Eliminar patrón Singleton manual de `ColaAtencionService`**
   - Usar solo `@Service` de Spring
   - Inyectar por constructor en `IngresoService`

2. ✅ **Mover `ColaAtencionService` al dominio**
   - De `tfi/application/service/` a `tfi/domain/service/`
   - O crear agregado `ColaAtencion`

3. ✅ **Corregir `IngresoService.actualizarIngreso()`**
   - Usar métodos de negocio en lugar de crear nueva entidad
   - Agregar métodos de negocio a `Ingreso` si faltan

4. ✅ **Corregir `PacienteService.actualizar()`**
   - Usar métodos de negocio existentes
   - No crear nueva entidad

### Prioridad MEDIA (Importante - Afecta Calidad)

5. ✅ **Reducir setters públicos en entidades**
   - Hacer package-private o eliminar
   - Usar métodos de negocio para cambios

6. ✅ **Cambiar `Atencion` para usar referencias**
   - `Ingreso` y `Usuario` en lugar de IDs
   - Manejar persistencia en infraestructura

7. ✅ **Agregar validaciones en constructores**
   - Especialmente `Ingreso` y `Usuario`

8. ✅ **Extraer lógica de creación de pacientes**
   - De `IngresoService` a servicio de dominio o factory

### Prioridad BAJA (Mejoras - Incrementales)

9. ✅ **Agregar métodos de búsqueda a repositorios**
   - `findByEstado()`, `findByPaciente()`, etc.

10. ✅ **Estandarizar manejo de excepciones**
    - Documentar qué excepción usar en cada caso

11. ✅ **Completar documentación Javadoc**
    - Todos los métodos públicos

12. ✅ **Crear Value Objects adicionales**
    - `DescripcionIngreso`, `InformeMedico`, etc.

13. ✅ **Agregar métodos de negocio a Value Objects**
    - `Temperatura.esFiebre()`, `TensionArterial.esHipertension()`, etc.

---

## 📚 Referencias

- **DDD**: Domain-Driven Design - Eric Evans
- **Clean Architecture**: Robert C. Martin
- **Rich Domain Model**: Martin Fowler
- **SOLID Principles**: Robert C. Martin
- **Effective Java**: Joshua Bloch

---

## 📊 Resumen de Problemas por Categoría

- **Arquitectura**: 3 problemas críticos
- **Entidades**: 7 problemas
- **Servicios**: 3 problemas
- **Repositorios**: 2 problemas
- **DTOs**: 2 problemas
- **Consistencia**: 3 problemas
- **Diseño**: 2 problemas

**Total: 22 problemas identificados**

---

*Última actualización: Análisis completo del código base*

