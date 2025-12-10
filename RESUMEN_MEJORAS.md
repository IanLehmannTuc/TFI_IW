# 📋 Resumen Ejecutivo - Mejoras Arquitectónicas

## 🎯 Problemas Principales Identificados

### 1. **Entidades Anémicas** 🔴 CRÍTICO
- Las entidades son solo contenedores de datos (getters/setters)
- No tienen métodos de negocio
- La lógica está en servicios de aplicación

**Ejemplo**: `Ingreso` no tiene `iniciarAtencion()`, `finalizar()`, etc.

### 2. **Violación de Clean Architecture** 🔴 CRÍTICO
- El dominio depende de Spring (`Page`, `Pageable`)
- No es independiente de frameworks
- Dificulta el testing

### 3. **Lógica de Negocio en Capa Incorrecta** 🔴 CRÍTICO
- Lógica de dominio en servicios de aplicación
- Validaciones de negocio fuera del dominio
- Viola DDD

### 4. **Falta de Agregados Definidos** 🟠 ALTO
- No hay definición clara de agregados
- No hay agregados raíz
- No hay protección de invariantes

### 5. **Value Objects Mutables** 🟠 ALTO
- Algunos VOs permiten mutación
- Viola principio de inmutabilidad
- Puede causar bugs

---

## 🚀 Plan de Acción Recomendado

### Fase 1: Refactorización del Dominio (2-3 semanas)

#### 1.1. Hacer Entidades Ricas
```java
// ANTES (Anémica)
public class Ingreso {
    private Estado estado;
    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}

// DESPUÉS (Rica)
public class Ingreso {
    private Estado estado;
    
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
}
```

#### 1.2. Eliminar Dependencias de Spring del Dominio
```java
// ANTES
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PacientesRepository {
    Page<Paciente> findAll(Pageable pageable);
}

// DESPUÉS
public class PaginationRequest {
    private final int page;
    private final int size;
    // ...
}

public class PaginatedResult<T> {
    private final List<T> content;
    private final int totalElements;
    // ...
}

public interface PacientesRepository {
    PaginatedResult<Paciente> findAll(PaginationRequest request);
}
```

#### 1.3. Mover Lógica de Negocio al Dominio
```java
// ANTES (En IngresoService)
public IngresoResponse registrarIngreso(RegistroIngresoRequest dto) {
    // 100+ líneas de lógica aquí
    Paciente paciente = pacientesRepository.findByCuil(dto.getCuil())
        .orElseGet(() -> {
            // Lógica compleja de creación
        });
    // ...
}

// DESPUÉS (En dominio)
public class Paciente {
    public static Paciente crearDesdeRegistro(
            String cuil, 
            String nombre, 
            String apellido,
            Email email,
            Domicilio domicilio,
            Afiliado afiliado) {
        
        // Validaciones de negocio aquí
        return new Paciente(cuil, nombre, apellido, email, domicilio, afiliado);
    }
}
```

### Fase 2: Definir Agregados (1 semana)

#### 2.1. Documentar Agregados
- **Agregado `Ingreso`** (Raíz)
  - Contiene: `Ingreso`, `Atencion`
  - Invariantes: Un ingreso solo puede tener una atención
  
- **Agregado `Paciente`** (Raíz)
  - Contiene: `Paciente`, `Afiliado`
  - Invariantes: CUIL único

#### 2.2. Proteger Invariantes
```java
public class Ingreso {
    private Atencion atencion;
    
    public void asignarAtencion(Atencion atencion) {
        if (this.atencion != null) {
            throw new IllegalStateException("El ingreso ya tiene una atención asignada");
        }
        if (this.estado != Estado.EN_PROCESO) {
            throw new IllegalStateException("Solo se puede asignar atención a ingresos EN_PROCESO");
        }
        this.atencion = atencion;
    }
}
```

### Fase 3: Mejorar Value Objects (1 semana)

#### 3.1. Hacer Inmutables
```java
// ANTES
public class Afiliado {
    private ObraSocial obraSocial;
    public void setObraSocial(ObraSocial obraSocial) {
        this.obraSocial = obraSocial; // ❌ Mutable
    }
}

// DESPUÉS
public class Afiliado {
    private final ObraSocial obraSocial;
    private final String numeroAfiliado;
    
    public Afiliado(ObraSocial obraSocial, String numeroAfiliado) {
        // Validaciones
        this.obraSocial = obraSocial;
        this.numeroAfiliado = numeroAfiliado;
    }
    // Solo getters, sin setters
}
```

### Fase 4: Refactorizar Servicios (1-2 semanas)

#### 4.1. Separar Responsabilidades
```java
// ANTES: Un servicio hace todo
@Service
public class IngresoService {
    // 200+ líneas
}

// DESPUÉS: Servicios especializados
@Service
public class RegistroIngresoService {
    // Solo registro
}

@Service
public class ConsultaIngresoService {
    // Solo consultas
}

@Service
public class GestionColaAtencionService {
    // Solo cola
}
```

#### 4.2. Usar Inyección de Dependencias Correctamente
```java
// ANTES
this.colaAtencionService = ColaAtencionService.getInstance();

// DESPUÉS
public IngresoService(..., ColaAtencionService colaAtencionService) {
    this.colaAtencionService = colaAtencionService;
}
```

---

## 📊 Impacto Esperado

### Antes
- ❌ Lógica de negocio dispersa
- ❌ Entidades anémicas
- ❌ Dependencias de frameworks en dominio
- ❌ Difícil de testear
- ❌ Violaciones de DDD

### Después
- ✅ Lógica de negocio encapsulada
- ✅ Entidades ricas con comportamiento
- ✅ Dominio independiente de frameworks
- ✅ Fácil de testear
- ✅ Cumple principios DDD y Clean Architecture

---

## 🎓 Recursos de Aprendizaje

1. **Libro**: "Implementing Domain-Driven Design" - Vaughn Vernon
2. **Libro**: "Clean Architecture" - Robert C. Martin
3. **Artículo**: [The Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
4. **Artículo**: [DDD, Hexagonal, Onion, Clean, CQRS](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/)

---

## ⚠️ Advertencias

1. **No hacer todo de una vez**: Refactorizar incrementalmente
2. **Mantener tests**: Asegurar que los tests pasen después de cada cambio
3. **Comunicar cambios**: Documentar cambios arquitectónicos
4. **Revisar con equipo**: Validar decisiones con el equipo

---

*Resumen generado para: TFI_IW - Sistema de Gestión de Urgencias*

