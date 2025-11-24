# Esquema de Arquitectura - TFI Ing Software

## Visión General

Sistema de gestión hospitalaria desarrollado con **Spring Boot** siguiendo una **Arquitectura en Capas (Layered Architecture)** con principios de **Clean Architecture**.

---

## Estructura de Capas

```
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE PRESENTACIÓN                     │
│                      (Controllers)                          │
├─────────────────────────────────────────────────────────────┤
│  • AutenticacionController                                  │
│  • PacienteController                                       │
│  • ColaAtencionController                                   │
│  • UrgenciaController                                       │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE SERVICIOS                         │
│                    (Business Logic)                         │
├─────────────────────────────────────────────────────────────┤
│  • AutenticacionService                                     │
│  • PacienteService                                          │
│  • ColaAtencionService                                      │
│  • UrgenciaService                                          │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│              CAPA DE REPOSITORIO                            │
│              (Data Access Layer)                            │
├─────────────────────────────────────────────────────────────┤
│  Interfaces:                                                │
│    • UsuarioRepository                                      │
│    • PacientesRepository                                    │
│    • IngresoRepository                                      │
│    • EnfermeroRepository                                    │
│                                                              │
│  Implementaciones:                                          │
│    • UsuarioRepositoryImpl (Memory)                         │
│    • [Otras implementaciones...]                            │
└───────────────────────┬─────────────────────────────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────┐
│                    CAPA DE MODELO                           │
│                    (Domain Layer)                           │
├─────────────────────────────────────────────────────────────┤
│  Entities:                                                  │
│    • Usuario                                                │
│    • Paciente                                               │
│    • Ingreso                                                │
│    • Doctor, Enfermero, Persona, Afiliado, Nivel, ObraSocial│
│                                                              │
│  Value Objects:                                             │
│    • Email                                                  │
│    • Password                                               │
│    • FrecuenciaRespiratoria                                 │
│    • [Otros VOs...]                                         │
│                                                              │
│  DTOs:                                                      │
│    • RegistroRequest, LoginRequest                          │
│    • AuthResponse                                           │
│    • RegistroPacienteRequest, PacienteResponse              │
│    • IngresoResponse, ErrorResponse                         │
│    • UsuarioAutenticado                                     │
│                                                              │
│  Enums:                                                     │
│    • Autoridad                                              │
│    • NivelEmergencia                                        │
│                                                              │
│  Mappers:                                                   │
│    • PacienteMapper                                         │
│    • IngresoMapper                                          │
└─────────────────────────────────────────────────────────────┘
```

---

## Capas Transversales

```
┌─────────────────────────────────────────────────────────────┐
│                    CONFIGURACIÓN                            │
├─────────────────────────────────────────────────────────────┤
│  • SecurityConfig (Spring Security + JWT)                   │
│  • JwtConfig                                                │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                    UTILIDADES                               │
├─────────────────────────────────────────────────────────────┤
│  • JwtUtil (Generación y validación de tokens)              │
│  • PasswordHasher (BCrypt)                                   │
│  • SecurityContext                                          │
│  • DateUtils                                                │
│  • MensajesError                                            │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  MANEJO DE EXCEPCIONES                      │
├─────────────────────────────────────────────────────────────┤
│  • AutenticacionException                                   │
│  • RegistroException                                        │
│  • [Otras excepciones...]                                   │
└─────────────────────────────────────────────────────────────┘
```

---

## Flujo de Datos

```
Cliente HTTP
    │
    ▼
┌─────────────────┐
│   Controller    │  ← Recibe requests HTTP
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│    Service      │  ← Lógica de negocio
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Repository    │  ← Acceso a datos
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   Model/Entity  │  ← Entidades de dominio
└─────────────────┘
```

---

## Principios Arquitectónicos

### 1. **Separación de Responsabilidades**
- Cada capa tiene una responsabilidad única y bien definida
- Los controllers solo manejan HTTP
- Los services contienen la lógica de negocio
- Los repositories manejan el acceso a datos

### 2. **Inversión de Dependencias**
- Los services dependen de interfaces de repositorio, no de implementaciones
- Facilita el cambio de implementación (Memory → Database)

### 3. **Domain-Driven Design (DDD)**
- Value Objects (Email, Password) encapsulan validaciones
- Entidades de dominio ricas con lógica de negocio
- Separación entre entidades y DTOs

### 4. **Clean Architecture**
- El dominio (model) no depende de frameworks
- Las dependencias apuntan hacia adentro (hacia el dominio)

---

## Tecnologías y Frameworks

- **Spring Boot 3.5.5**
- **Spring Security** (Autenticación y autorización)
- **JWT** (JSON Web Tokens)
- **BCrypt** (Hashing de contraseñas)
- **Java 22**
- **Maven** (Gestión de dependencias)

---

## Patrones de Diseño Utilizados

1. **Repository Pattern**: Abstracción del acceso a datos
2. **DTO Pattern**: Transferencia de datos entre capas
3. **Value Object Pattern**: Inmutabilidad y validación
4. **Service Layer Pattern**: Encapsulación de lógica de negocio
5. **Dependency Injection**: Inversión de control con Spring

---

## Estructura de Paquetes

```
tfi/
├── config/              # Configuración de Spring
├── controller/          # Controladores REST
├── exception/           # Excepciones personalizadas
├── model/
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # Entidades de dominio
│   ├── enums/          # Enumeraciones
│   ├── mapper/         # Mappers DTO ↔ Entity
│   └── valueObjects/   # Value Objects
├── repository/
│   ├── interfaces/     # Interfaces de repositorio
│   └── impl/
│       └── memory/     # Implementaciones en memoria
├── service/            # Servicios de negocio
└── util/               # Utilidades
```

---

## Notas de Implementación

- **Almacenamiento actual**: Implementación en memoria (ConcurrentHashMap)
- **Autenticación**: JWT con expiración configurable
- **Seguridad**: Spring Security con filtros JWT
- **Validación**: Validaciones en Value Objects y entidades
- **Thread-safety**: Repositorios en memoria usan ConcurrentHashMap

---

*Última actualización: Generado automáticamente*

