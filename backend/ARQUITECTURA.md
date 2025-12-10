# 🏗️ Arquitectura del Sistema - Backend

## Tabla de Contenidos

- [Visión General](#visión-general)
- [Principios Arquitectónicos](#principios-arquitectónicos)
- [Estructura de Capas](#estructura-de-capas)
- [Patrones de Diseño](#patrones-de-diseño)
- [Modelo de Dominio](#modelo-de-dominio)
- [Flujo de Datos](#flujo-de-datos)
- [Decisiones Arquitectónicas](#decisiones-arquitectónicas)

---

## Visión General

El sistema está construido siguiendo los principios de **Clean Architecture** y **Domain-Driven Design (DDD)**, organizando el código en capas bien definidas con responsabilidades claras y dependencias que fluyen hacia el dominio.

### Diagrama de Arquitectura

```
┌────────────────────────────────────────────────────────┐
│                   API REST Layer                        │
│              (Controllers - @RestController)            │
│   - IngresoController                                   │
│   - AtencionController                                  │
│   - PacienteController                                  │
│   - ColaAtencionController                              │
│   - AutenticacionController                             │
└──────────────────┬─────────────────────────────────────┘
                   │ HTTP Requests/Responses
                   ▼
┌────────────────────────────────────────────────────────┐
│              Application Services Layer                 │
│           (Services + DTOs + Mappers)                   │
│   - IngresoService         - DTOs (Request/Response)    │
│   - AtencionService        - IngresoMapper              │
│   - PacienteService        - Validaciones               │
│   - ColaAtencionService    - Orquestación               │
│   - AutenticacionService                                │
└──────────────────┬─────────────────────────────────────┘
                   │ Coordina lógica de aplicación
                   ▼
┌────────────────────────────────────────────────────────┐
│                    Domain Layer                         │
│              (Entidades + Value Objects)                │
│   Entities:                 Value Objects:              │
│   - Ingreso                 - Email                     │
│   - Paciente                - Cuil                      │
│   - Atencion                - Temperatura               │
│   - Usuario                 - TensionArterial           │
│   - Afiliado                - Telefono                  │
│                             - Domicilio                 │
│   Enums:                    - FrecuenciaCardiaca        │
│   - Estado                  - FrecuenciaRespiratoria    │
│   - NivelEmergencia                                     │
│   - Autoridad                                           │
│   - Sexo                                                │
└──────────────────┬─────────────────────────────────────┘
                   │ Interfaces (Ports)
                   ▼
┌────────────────────────────────────────────────────────┐
│              Infrastructure Layer                       │
│         (Repositories + External Services)              │
│   Persistence:              External:                   │
│   - JdbcPacientesRepo       - ObraSocialApiClient       │
│   - JdbcIngresoRepo         - ObraSocialCacheService    │
│   - JdbcAtencionRepo                                    │
│   - JdbcUsuarioRepo                                     │
│                                                          │
│   Configuration:                                        │
│   - DataSourceConfig (PostgreSQL/Memory)                │
│   - SecurityConfig (JWT Filter)                         │
│   - JwtAuthenticationFilter                             │
└────────────────────────────────────────────────────────┘
                   │
                   ▼
         ┌──────────────────┬──────────────────┐
         │                  │                  │
    PostgreSQL         Memoria RAM      API Externa
    (Producción)       (Testing)      (Obras Sociales)
```

---

## Principios Arquitectónicos

### 1. Clean Architecture (Arquitectura Limpia)

**Objetivo**: Separación de responsabilidades y dependencias claras

#### Principios Aplicados:

- **Independencia de frameworks**: El dominio no depende de Spring
- **Testabilidad**: Cada capa puede testearse independientemente
- **Independencia de UI**: La API REST puede cambiarse sin afectar el dominio
- **Independencia de BD**: Se puede cambiar PostgreSQL por otra BD
- **Regla de dependencia**: Las dependencias apuntan hacia adentro (hacia el dominio)

### 2. Domain-Driven Design (DDD)

**Objetivo**: Modelar el negocio con entidades ricas y expresivas

#### Elementos DDD Implementados:

**Entities (Entidades)**:
- `Ingreso`: Agrega el flujo completo de un ingreso
- `Paciente`: Agrega datos del paciente y su afiliación
- `Atencion`: Representa el diagnóstico médico
- `Usuario`: Personal médico (médicos y enfermeros)

**Value Objects**:
- `Email`, `Cuil`, `Telefono`: Validaciones de formato
- `Temperatura`, `TensionArterial`: Validaciones médicas
- `Domicilio`: Datos de ubicación
- `Presion`, `FrecuenciaCardiaca`, `FrecuenciaRespiratoria`

**Repositories (Interfaces)**:
- Define contratos, no implementaciones
- Ubicados en `domain.repository`
- Implementados en `infrastructure.persistence`

**Services de Dominio**:
- `ColaAtencionService`: Maneja la lógica de la cola de prioridad

### 3. SOLID Principles

**Single Responsibility**: Cada clase tiene una única razón para cambiar
- `IngresoService`: Solo maneja lógica de ingresos
- `AtencionService`: Solo maneja lógica de atenciones
- `PacienteService`: Solo maneja lógica de pacientes

**Open/Closed**: Abierto a extensión, cerrado a modificación
- Interfaces de repositorios permiten múltiples implementaciones
- Value Objects inmutables

**Liskov Substitution**: Las implementaciones son intercambiables
- `JdbcIngresoRepository` y `InMemoryIngresoRepository` implementan la misma interfaz

**Interface Segregation**: Interfaces específicas
- Cada repositorio tiene solo los métodos que necesita

**Dependency Inversion**: Dependencias sobre abstracciones
- Services dependen de interfaces, no de implementaciones concretas

---

## Estructura de Capas

### Capa 1: Controllers (API REST)

**Ubicación**: `tfi.controller`

**Responsabilidad**: Manejar HTTP requests/responses

**Componentes**:
```
controller/
├── IngresoController.java          # POST /api/ingresos, GET /api/ingresos
├── AtencionController.java         # POST /api/atenciones
├── PacienteController.java         # CRUD pacientes
├── ColaAtencionController.java     # POST /api/cola-atencion/atender
├── AutenticacionController.java    # POST /api/auth/login, /api/auth/registro
└── ObraSocialController.java       # GET /api/obras-sociales
```

**Características**:
- Anotaciones: `@RestController`, `@RequestMapping`
- Validación: `@Valid` con Jakarta Validation
- Seguridad: Usa `SecurityContext.requireAutoridad()`
- Respuestas: `ResponseEntity<T>` con códigos HTTP apropiados

**Ejemplo**:

```java
@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {
    
    @PostMapping
    public ResponseEntity<IngresoResponse> registrarIngreso(
            @Valid @RequestBody RegistroIngresoRequest request,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERO);
        IngresoResponse response = ingresoService.registrarIngreso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
```

---

### Capa 2: Application Services

**Ubicación**: `tfi.application`

**Responsabilidad**: Orquestar casos de uso y coordinar entidades

**Estructura**:
```
application/
├── service/
│   ├── IngresoService.java          # Lógica de ingresos
│   ├── AtencionService.java         # Lógica de atenciones
│   ├── PacienteService.java         # Lógica de pacientes
│   ├── ColaAtencionService.java     # Cola de prioridad (Singleton)
│   ├── AutenticacionService.java    # JWT y registro/login
│   └── ObraSocialCacheService.java  # Cache de obras sociales
├── dto/
│   ├── RegistroIngresoRequest.java  # DTOs de entrada
│   ├── IngresoResponse.java         # DTOs de salida
│   └── ...
└── mapper/
    ├── IngresoMapper.java           # Entity → DTO
    └── ...
```

**Características**:
- Anotación: `@Service` (gestionados por Spring)
- No contienen lógica de dominio (delegan a entidades)
- Coordinan múltiples repositorios
- Transforman entidades en DTOs

**Patrón de Servicio**:

```java
@Service
public class IngresoService {
    private final IngresoRepository ingresoRepository;
    private final PacientesRepository pacientesRepository;
    private final ColaAtencionService colaAtencionService;
    
    public IngresoResponse registrarIngreso(RegistroIngresoRequest dto) {
        // 1. Buscar o crear paciente
        Paciente paciente = pacientesRepository.findByCuil(dto.getCuil())
            .orElseGet(() -> crearNuevoPaciente(dto));
        
        // 2. Crear ingreso (lógica de negocio en la entidad)
        Ingreso ingreso = new Ingreso(...);
        
        // 3. Persistir
        Ingreso guardado = ingresoRepository.add(ingreso);
        
        // 4. Agregar a cola
        colaAtencionService.agregarACola(guardado);
        
        // 5. Retornar DTO
        return ingresoMapper.toResponse(guardado);
    }
}
```

---

### Capa 3: Domain (Núcleo del Sistema)

**Ubicación**: `tfi.domain`

**Responsabilidad**: Contener toda la lógica de negocio

**Estructura**:
```
domain/
├── entity/
│   ├── Ingreso.java        # Agrega ingreso + signos vitales
│   ├── Paciente.java       # Agrega paciente + domicilio + afiliación
│   ├── Atencion.java       # Informe médico
│   ├── Usuario.java        # Personal médico
│   ├── Afiliado.java       # Afiliación a obra social
│   ├── ObraSocial.java     # Obra social
│   └── Nivel.java          # Nivel de emergencia (agregado)
├── valueObject/
│   ├── Email.java          # Email con validación
│   ├── Cuil.java           # CUIL argentino validado
│   ├── Temperatura.java    # 35.0-42.0°C
│   ├── TensionArterial.java# Sistólica/Diastólica
│   ├── Telefono.java       # Formato telefónico
│   ├── Domicilio.java      # Calle, número, localidad
│   └── ...
├── enums/
│   ├── Estado.java         # PENDIENTE, EN_PROCESO, FINALIZADO
│   ├── NivelEmergencia.java# CRITICA, EMERGENCIA, URGENCIA, etc.
│   ├── Autoridad.java      # MEDICO, ENFERMERO
│   └── Sexo.java           # MASCULINO, FEMENINO
├── repository/
│   ├── IngresoRepository.java       # Interface
│   ├── PacientesRepository.java     # Interface
│   ├── AtencionRepository.java      # Interface
│   └── UsuarioRepository.java       # Interface
└── service/
    └── (servicios de dominio si son necesarios)
```

#### Características de las Entidades

**1. Entidades Ricas (Rich Domain Model)**:

Las entidades contienen lógica de negocio, no solo getters/setters:

```java
public class Ingreso {
    private Estado estado;
    
    // ✅ Método de negocio que valida transiciones de estado
    public void iniciarAtencion() {
        if (this.estado != Estado.PENDIENTE) {
            throw new IllegalStateException(
                "Solo se pueden iniciar ingresos PENDIENTES"
            );
        }
        this.estado = Estado.EN_PROCESO;
    }
    
    // ✅ Método de consulta expresivo
    public boolean estaPendiente() {
        return this.estado == Estado.PENDIENTE;
    }
}
```

**2. Inmutabilidad y Campos Final**:

```java
public class Ingreso {
    private final Paciente paciente;           // Inmutable
    private final Temperatura temperatura;     // Inmutable
    private final NivelEmergencia nivelEmergencia;  // Inmutable
    private Estado estado;  // Mutable solo mediante métodos de negocio
}
```

**3. Value Objects Inmutables**:

```java
public class Temperatura {
    private final double valor;
    
    public Temperatura(double valor) {
        if (valor < 35.0 || valor > 42.0) {
            throw new IllegalArgumentException(
                "Temperatura debe estar entre 35°C y 42°C"
            );
        }
        this.valor = valor;
    }
    
    public double getValor() {
        return valor;
    }
    
    // Sin setters - inmutable
}
```

**4. Validaciones en el Constructor**:

```java
public class Email {
    private final String direccion;
    
    public Email(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.direccion = email;
    }
}
```

---

### Capa 4: Infrastructure

**Ubicación**: `tfi.infrastructure`

**Responsabilidad**: Implementar detalles técnicos (BD, APIs externas)

**Estructura**:
```
infrastructure/
├── persistence/
│   ├── jdbc/
│   │   ├── JdbcIngresoRepository.java    # Implementación JDBC
│   │   ├── JdbcPacientesRepository.java
│   │   ├── JdbcAtencionRepository.java
│   │   └── JdbcUsuarioRepository.java
│   └── memory/
│       ├── InMemoryIngresoRepository.java  # Implementación en memoria
│       ├── InMemoryPacientesRepository.java
│       └── ...
└── external/
    └── ObraSocialApiClient.java          # Cliente HTTP para API externa
```

**Implementación JDBC**:

```java
@Repository
@Profile("postgres")  // Solo se activa con perfil 'postgres'
public class JdbcIngresoRepository implements IngresoRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public Ingreso add(Ingreso ingreso) {
        String sql = "INSERT INTO ingresos (...) VALUES (...)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, 
                Statement.RETURN_GENERATED_KEYS);
            // Set parameters...
            return ps;
        }, keyHolder);
        
        UUID id = (UUID) keyHolder.getKeys().get("id");
        ingreso.setId(id.toString());
        return ingreso;
    }
}
```

**Implementación en Memoria**:

```java
@Repository
@Profile("memory")  // Solo se activa con perfil 'memory'
public class InMemoryIngresoRepository implements IngresoRepository {
    
    private final Map<String, Ingreso> storage = new ConcurrentHashMap<>();
    
    @Override
    public Ingreso add(Ingreso ingreso) {
        String id = UUID.randomUUID().toString();
        ingreso.setId(id);
        storage.put(id, ingreso);
        return ingreso;
    }
}
```

---

## Patrones de Diseño

### 1. Repository Pattern

**Problema**: Aislar la lógica de persistencia

**Solución**: Interfaces en dominio, implementaciones en infrastructure

```
domain/repository/IngresoRepository.java  (Interface)
        ↑                           ↑
        |                           |
infrastructure/persistence/         |
  ├── JdbcIngresoRepository         |
  └── InMemoryIngresoRepository ----+
```

### 2. DTO Pattern (Data Transfer Object)

**Problema**: No exponer entidades de dominio en la API

**Solución**: Crear DTOs específicos para entrada/salida

```java
// ❌ NO - Expone entidad directamente
@PostMapping
public Ingreso crear(@RequestBody Ingreso ingreso) { ... }

// ✅ SÍ - Usa DTOs
@PostMapping
public ResponseEntity<IngresoResponse> crear(
    @RequestBody RegistroIngresoRequest request) { ... }
```

### 3. Mapper Pattern

**Problema**: Conversión repetitiva entre Entity ↔ DTO

**Solución**: Clases Mapper dedicadas

```java
@Component
public class IngresoMapper {
    
    public IngresoResponse toResponse(Ingreso ingreso) {
        IngresoResponse response = new IngresoResponse();
        response.setId(ingreso.getId());
        response.setPaciente(mapPaciente(ingreso.getPaciente()));
        // ...
        return response;
    }
}
```

### 4. Singleton Pattern

**Problema**: Cola de atención debe ser única en memoria

**Solución**: Spring gestiona `ColaAtencionService` como singleton

```java
@Service  // Spring crea una única instancia
public class ColaAtencionService {
    
    private final PriorityQueue<Ingreso> cola = new PriorityQueue<>(...);
    
    public void agregarACola(Ingreso ingreso) {
        cola.offer(ingreso);
    }
}
```

### 5. Strategy Pattern

**Problema**: Diferentes implementaciones de repositorios

**Solución**: Spring Profiles cambian estrategia en runtime

```java
// Estrategia 1
@Repository
@Profile("postgres")
public class JdbcIngresoRepository implements IngresoRepository { ... }

// Estrategia 2
@Repository
@Profile("memory")
public class InMemoryIngresoRepository implements IngresoRepository { ... }
```

### 6. Dependency Injection

**Problema**: Acoplamiento alto entre componentes

**Solución**: Spring inyecta dependencias automáticamente

```java
@Service
public class IngresoService {
    
    // Spring inyecta automáticamente
    private final IngresoRepository ingresoRepository;
    private final PacientesRepository pacientesRepository;
    
    public IngresoService(IngresoRepository ingresoRepository,
                         PacientesRepository pacientesRepository) {
        this.ingresoRepository = ingresoRepository;
        this.pacientesRepository = pacientesRepository;
    }
}
```

---

## Modelo de Dominio

### Diagrama de Entidades

```
┌──────────────┐
│   Usuario    │
│──────────────│
│ id           │
│ email        │◄────┐
│ cuil         │     │
│ nombre       │     │ enfermero
│ apellido     │     │
│ matricula    │     │
│ autoridad    │     │
└──────────────┘     │
       ▲             │
       │ medico      │
       │             │
┌──────────────┐     │      ┌──────────────┐
│  Atencion    │     │      │   Ingreso    │
│──────────────│     │      │──────────────│
│ id           │     │      │ id           │
│ ingresoId    │◄────┼──────┤ atencion     │
│ medico       │─────┘      │ paciente     │────┐
│ informeMedico│            │ enfermero    │────┤
│ fechaAtencion│            │ descripcion  │    │
└──────────────┘            │ fechaIngreso │    │
                            │ temperatura  │    │
                            │ tensionArt.  │    │
                            │ frecCardiaca │    │
                            │ frecResp.    │    │
                            │ nivelEmerg.  │    │
                            │ estado       │    │
                            └──────────────┘    │
                                                 │
                                                 ▼
                            ┌──────────────┐
                            │  Paciente    │
                            │──────────────│
                            │ id           │
                            │ cuil         │
                            │ nombre       │
                            │ apellido     │
                            │ email        │
                            │ telefono     │
                            │ fechaNac.    │
                            │ sexo         │
                            │ domicilio    │
                            │ afiliado     │
                            └──────────────┘
                                   │
                                   │ afiliado
                                   ▼
                            ┌──────────────┐
                            │  Afiliado    │
                            │──────────────│
                            │ obraSocial   │────┐
                            │ numeroAfil.  │    │
                            └──────────────┘    │
                                                 ▼
                                          ┌──────────────┐
                                          │ ObraSocial   │
                                          │──────────────│
                                          │ id           │
                                          │ nombre       │
                                          └──────────────┘
```

### Estados del Ingreso

```
┌─────────────┐      iniciarAtencion()      ┌─────────────┐
│  PENDIENTE  │──────────────────────────────►│ EN_PROCESO  │
└─────────────┘                              └─────────────┘
                                                     │
                                                     │ finalizar()
                                                     ▼
                                              ┌─────────────┐
                                              │ FINALIZADO  │
                                              └─────────────┘
```

### Niveles de Emergencia (Prioridad)

```
┌──────────────────────────┬──────────┬───────────────┐
│ Nivel                    │ Prioridad│ Tiempo Espera │
├──────────────────────────┼──────────┼───────────────┤
│ CRITICA                  │    5     │   Inmediato   │
│ EMERGENCIA               │    4     │   < 15 min    │
│ URGENCIA                 │    3     │   < 30 min    │
│ URGENCIA_MENOR           │    2     │   < 60 min    │
│ SIN_URGENCIA             │    1     │   < 120 min   │
└──────────────────────────┴──────────┴───────────────┘
```

---

## Flujo de Datos

### Flujo 1: Registro de Ingreso

```
1. POST /api/ingresos
   │
   ▼
2. IngresoController.registrarIngreso()
   │ - Valida JWT y autoridad ENFERMERO
   │ - Valida @Valid RegistroIngresoRequest
   ▼
3. IngresoService.registrarIngreso()
   │ - Busca o crea Paciente
   │ - Busca Usuario (enfermero)
   │ - Crea Value Objects (Temperatura, etc.)
   │ - Crea entidad Ingreso (estado PENDIENTE)
   ▼
4. IngresoRepository.add()
   │ - Persiste en BD o memoria
   │ - Genera ID
   ▼
5. ColaAtencionService.agregarACola()
   │ - Agrega a PriorityQueue
   │ - Ordena por prioridad y fecha
   ▼
6. IngresoMapper.toResponse()
   │ - Convierte Ingreso → IngresoResponse
   ▼
7. ResponseEntity<IngresoResponse>
   │ - 201 Created
   └─► Cliente recibe JSON
```

### Flujo 2: Atender Paciente

```
1. POST /api/cola-atencion/atender
   │
   ▼
2. ColaAtencionController.atenderSiguiente()
   │ - Valida JWT y autoridad ENFERMERO
   ▼
3. IngresoService.atenderSiguientePaciente()
   │ - Llama a ColaAtencionService
   ▼
4. ColaAtencionService.atenderSiguiente()
   │ - Extrae ingreso de PriorityQueue (mayor prioridad)
   ▼
5. Ingreso.iniciarAtencion()
   │ - Valida estado == PENDIENTE
   │ - Cambia estado a EN_PROCESO
   ▼
6. IngresoRepository.update()
   │ - Persiste cambio de estado
   ▼
7. IngresoMapper.toResponse()
   │
   ▼
8. ResponseEntity<IngresoResponse>
   │ - 200 OK
   └─► Cliente recibe JSON con ingreso EN_PROCESO
```

### Flujo 3: Registrar Atención Médica

```
1. POST /api/atenciones
   │
   ▼
2. AtencionController.registrarAtencion()
   │ - Valida JWT y autoridad MEDICO
   │ - Valida @Valid RegistroAtencionRequest
   ▼
3. AtencionService.registrarAtencion()
   │ - Busca Ingreso por ID
   │ - Valida estado == EN_PROCESO
   │ - Busca Usuario (medico)
   ▼
4. new Atencion(ingresoId, medico, informe)
   │ - Valida informe no vacío
   │ - Valida medico no nulo
   ▼
5. AtencionRepository.add()
   │ - Persiste atención
   ▼
6. Ingreso.asignarAtencion()
   │ - Asigna referencia a atención
   ▼
7. Ingreso.finalizar()
   │ - Valida estado == EN_PROCESO
   │ - Valida tiene atención
   │ - Cambia estado a FINALIZADO
   ▼
8. IngresoRepository.update()
   │ - Persiste cambio de estado
   ▼
9. ResponseEntity<AtencionResponse>
   │ - 201 Created
   └─► Cliente recibe JSON con atención
```

---

## Decisiones Arquitectónicas

### ADR-001: Uso de JDBC en lugar de JPA/Hibernate

**Contexto**: Necesidad de acceso a base de datos

**Decisión**: Usar Spring JDBC en lugar de JPA/Hibernate

**Justificación**:
- ✅ Control total sobre las queries SQL
- ✅ Sin magia de ORM (más explícito)
- ✅ Mejor rendimiento (sin lazy loading ni proxies)
- ✅ Facilita implementación de repositorios in-memory
- ✅ Menor curva de aprendizaje

**Consecuencias**:
- ➕ Código más explícito y predecible
- ➕ Mapeo manual Entity ↔ BD
- ➖ Más código boilerplate
- ➖ Sin generación automática de esquema

---

### ADR-002: Arquitectura de Múltiples Capas

**Contexto**: Organización del código

**Decisión**: Separar en capas: Controller → Service → Domain → Infrastructure

**Justificación**:
- ✅ Separación clara de responsabilidades
- ✅ Testabilidad independiente por capa
- ✅ Facilita mantenimiento y evolución
- ✅ Regla de dependencia clara (hacia el dominio)

**Consecuencias**:
- ➕ Código más organizado y mantenible
- ➕ Facilita trabajo en equipo
- ➖ Más archivos y estructura compleja
- ➖ Curva de aprendizaje inicial

---

### ADR-003: Entidades Ricas (Rich Domain Model)

**Contexto**: Dónde ubicar la lógica de negocio

**Decisión**: Lógica de negocio en las entidades del dominio

**Justificación**:
- ✅ Cohesión alta (datos + comportamiento juntos)
- ✅ Expresividad del modelo (`ingreso.iniciarAtencion()`)
- ✅ Encapsulación (validaciones en el dominio)
- ✅ Facilita testing unitario

**Consecuencias**:
- ➕ Modelo de dominio expresivo
- ➕ Validaciones centralizadas
- ➖ Entidades más grandes
- ➖ Requiere más diseño upfront

---

### ADR-004: Value Objects Inmutables

**Contexto**: Representar conceptos del dominio

**Decisión**: Usar Value Objects inmutables con validaciones

**Justificación**:
- ✅ Validaciones en el constructor
- ✅ Inmutabilidad garantiza consistencia
- ✅ Expresividad (`Temperatura` vs `double`)
- ✅ Reutilizables en múltiples entidades

**Consecuencias**:
- ➕ Validaciones centralizadas
- ➕ Código más seguro (sin mutaciones)
- ➖ Más clases pequeñas
- ➖ No se pueden cambiar después de creados

---

### ADR-005: Cola de Atención en Memoria (Singleton)

**Contexto**: Gestionar orden de atención de pacientes

**Decisión**: Cola en memoria con `PriorityQueue`, gestionada por Spring como Singleton

**Justificación**:
- ✅ Performance óptima (O(log n) para inserción/extracción)
- ✅ Ordenamiento automático por prioridad
- ✅ No requiere persistencia (se reconstruye al iniciar)
- ✅ Simplicidad de implementación

**Consecuencias**:
- ➕ Rendimiento excelente
- ➕ Código simple y claro
- ➖ Se pierde al reiniciar el servidor
- ➖ No funciona en clusters (solo monolito)

**Solución futura**: Si se requiere clustering, migrar a Redis o BD

---

### ADR-006: Autenticación JWT sin Spring Security

**Contexto**: Sistema de autenticación

**Decisión**: Implementar JWT con filtro custom, sin Spring Security

**Justificación**:
- ✅ Control total sobre el flujo de autenticación
- ✅ Simplicidad (sin configuración compleja)
- ✅ Aprendizaje de conceptos sin abstracciones
- ✅ Suficiente para el alcance del proyecto

**Consecuencias**:
- ➕ Código más simple y entendible
- ➕ Sin dependencias extra
- ➖ Menos features de seguridad avanzados
- ➖ Implementación manual de funcionalidades

---

### ADR-007: Perfiles Spring (PostgreSQL vs Memoria)

**Contexto**: Testing y desarrollo sin BD

**Decisión**: Implementar dos perfiles: `postgres` y `memory`

**Justificación**:
- ✅ Testing rápido sin BD
- ✅ Desarrollo sin configurar PostgreSQL
- ✅ Mismo código, diferentes implementaciones
- ✅ Facilita CI/CD

**Consecuencias**:
- ➕ Testing más rápido
- ➕ Onboarding más fácil
- ➖ Mantener dos implementaciones
- ➖ Posibles diferencias de comportamiento

---

## Consideraciones de Seguridad

### 1. Autenticación JWT

```java
// 1. Login genera token
POST /api/auth/login
{ "email": "medico@hospital.com", "password": "..." }

// 2. Server valida y retorna JWT
{ "token": "eyJhbGciOiJIUzI1Ni...", "usuario": {...} }

// 3. Cliente envía token en cada request
GET /api/ingresos
Authorization: Bearer eyJhbGciOiJIUzI1Ni...
```

### 2. Autorización por Rol

```java
@PostMapping
public ResponseEntity<?> registrarIngreso(HttpServletRequest request) {
    // ❌ Falla si no es ENFERMERO
    SecurityContext.requireAutoridad(request, Autoridad.ENFERMERO);
    // ...
}
```

### 3. Validación de Entrada

```java
// Jakarta Validation en DTOs
public class RegistroIngresoRequest {
    @NotBlank(message = "CUIL del paciente es obligatorio")
    private String pacienteCuil;
    
    @NotNull(message = "Temperatura es obligatoria")
    @Min(value = 35, message = "Temperatura mínima: 35°C")
    @Max(value = 42, message = "Temperatura máxima: 42°C")
    private Double temperatura;
}
```

### 4. Contraseñas Hasheadas

```java
// Nunca se almacena password en texto plano
String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
usuario.setPasswordHash(passwordHash);
```

---

## Escalabilidad y Performance

### 1. Pool de Conexiones (HikariCP)

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 2. Índices en Base de Datos

```sql
CREATE INDEX idx_ingresos_estado ON ingresos(estado);
CREATE INDEX idx_ingresos_nivel ON ingresos(nivel_emergencia);
CREATE INDEX idx_ingresos_fecha ON ingresos(fecha_hora_ingreso);
```

### 3. Cola de Prioridad Eficiente

- Inserción: O(log n)
- Extracción del máximo: O(log n)
- Consulta sin modificar: O(n)

### 4. Cache de Obras Sociales

```java
@Service
public class ObraSocialCacheService {
    private final Map<Integer, ObraSocial> cache = new ConcurrentHashMap<>();
    private LocalDateTime ultimaActualizacion;
    
    // Cache con TTL de 1 hora
    public ObraSocial obtenerObraSocial(Integer id) {
        if (cacheExpirado()) {
            actualizarCache();
        }
        return cache.get(id);
    }
}
```

---

## Testing

### Estructura de Tests

```
src/test/
├── java/
│   ├── tfi/domain/entity/
│   │   ├── IngresoTest.java           # Tests unitarios de entidad
│   │   ├── PacienteTest.java
│   │   └── AtencionTest.java
│   ├── tfi/domain/valueObject/
│   │   ├── TemperaturaTest.java       # Tests de validaciones
│   │   ├── TensionArterialTest.java
│   │   └── CuilTest.java
│   ├── tfi/application/service/
│   │   ├── IngresoServiceTest.java    # Tests de servicios
│   │   └── AtencionServiceTest.java
│   └── RunCucumberTest.java           # Tests BDD
└── resources/
    └── moduloUrgencias.feature        # Escenarios Cucumber
```

### Estrategia de Testing

**Tests Unitarios**: Entidades y Value Objects
- Sin dependencias externas
- Validaciones de lógica de negocio
- Transiciones de estado

**Tests de Integración**: Services
- Con repositorios in-memory
- Flujos completos de casos de uso

**Tests BDD (Cucumber)**: End-to-end
- Escenarios de usuario reales
- Múltiples componentes integrados

---

## Mejoras Futuras

### 1. Event Sourcing
- Registrar eventos de dominio (`IngresoCreado`, `AtencionIniciada`)
- Historia completa de cambios

### 2. CQRS (Command Query Responsibility Segregation)
- Separar modelos de lectura y escritura
- Optimizar queries complejas

### 3. Notification Service
- Notificar a médicos cuando hay paciente crítico
- WebSockets para updates en tiempo real

### 4. Audit Log
- Registrar quién hizo qué y cuándo
- Trazabilidad completa

### 5. API Gateway
- Centralizar autenticación
- Rate limiting
- Logging centralizado

---

## Referencias

### Libros
- **Clean Architecture** - Robert C. Martin
- **Domain-Driven Design** - Eric Evans
- **Implementing Domain-Driven Design** - Vaughn Vernon

### Artículos
- [The Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [DDD, Hexagonal, Onion, Clean, CQRS](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/)

### Herramientas
- Spring Boot Documentation
- PostgreSQL Documentation
- JWT.io

