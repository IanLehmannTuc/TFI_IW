---
name: Mejora Backend DDD y Arquitectura Limpia
overview: Plan para corregir inconsistencias críticas que violan principios de DDD y Arquitectura Limpia, enfocándose en separación de capas, encapsulación de entidades y servicios de dominio.
todos: []
---

# Plan de Mejora - Backend DDD y Arquitectura Limpia

## Objetivo

Corregir inconsistencias críticas que violan principios de DDD y Arquitectura Limpia, mejorando la separación de capas, encapsulación de entidades y organización de servicios.

## Fase 1: Refactorización de Controladores (Prioridad Alta)

### 1.1 Limpiar IngresoController

**Archivo**: [backend/src/main/java/tfi/controller/IngresoController.java](backend/src/main/java/tfi/controller/IngresoController.java)

**Problemas**:

- Acceso directo a `PacientesRepository` y `UsuarioRepository` (líneas 37-38, 180-190)
- Creación de entidades `Ingreso` en el controlador (líneas 201-213)
- Lógica de búsqueda y mapeo en el controlador (líneas 131-135, 170-174, 245-249)

**Acciones**:

1. Eliminar dependencias de repositorios del constructor
2. Eliminar `IngresoMapper` del controlador (el servicio debe retornar DTOs)
3. Crear métodos en `IngresoService`:

   - `obtenerIngresoPorId(String id): IngresoResponse`
   - `actualizarIngreso(String id, RegistroIngresoRequest request): IngresoResponse`
   - `eliminarIngreso(String id): void`

4. Simplificar métodos del controlador para solo delegar al servicio

**Resultado esperado**: El controlador solo maneja HTTP y delega toda la lógica al servicio.

---

## Fase 2: Refactorización de ColaAtencionService (Prioridad Alta)

### 2.1 Mover ColaAtencionService al Dominio

**Archivo actual**: [backend/src/main/java/tfi/application/service/ColaAtencionService.java](backend/src/main/java/tfi/application/service/ColaAtencionService.java)

**Archivo destino**: [backend/src/main/java/tfi/domain/service/ColaAtencionService.java](backend/src/main/java/tfi/domain/service/ColaAtencionService.java)

**Acciones**:

1. Mover el archivo a `tfi.domain.service`
2. Eliminar `@Service` de Spring (el dominio no debe depender de Spring)
3. Eliminar patrón Singleton manual (líneas 19-44)
4. Convertir a clase normal con constructor público
5. Crear interfaz `ColaAtencionPort` en `tfi.domain.port` si es necesario para inversión de dependencias

### 2.2 Actualizar IngresoService para usar ColaAtencionService del dominio

**Archivo**: [backend/src/main/java/tfi/application/service/IngresoService.java](backend/src/main/java/tfi/application/service/IngresoService.java)

**Acciones**:

1. Cambiar import de `tfi.application.service` a `tfi.domain.service`
2. Inyectar `ColaAtencionService` por constructor en lugar de `getInstance()` (línea 45)
3. Crear bean de Spring en capa de configuración para instanciar `ColaAtencionService` del dominio

**Resultado esperado**: La lógica de dominio está en el dominio, y Spring solo la instancia.

---

## Fase 3: Mejora de Servicios de Aplicación (Prioridad Alta)

### 3.1 Hacer que IngresoService retorne DTOs

**Archivo**: [backend/src/main/java/tfi/application/service/IngresoService.java](backend/src/main/java/tfi/application/service/IngresoService.java)

**Métodos a modificar**:

- `obtenerColaDeAtencion()`: Cambiar retorno de `List<Ingreso>` a `List<IngresoResponse>` (línea 151)
- `atenderSiguientePaciente()`: Cambiar retorno de `Ingreso` a `IngresoResponse` (línea 162)
- `eliminarIngreso()`: Cambiar parámetro de `Ingreso` a `String id` y retorno a `IngresoResponse` (línea 182)
- `obtenerTodosLosIngresos()`: Cambiar retorno de `List<Ingreso>` a `List<IngresoResponse>` (línea 215)

**Acciones**:

1. Usar `IngresoMapper` para convertir entidades a DTOs antes de retornar
2. Actualizar controladores que usen estos métodos

### 3.2 Corregir PacienteService.actualizar()

**Archivo**: [backend/src/main/java/tfi/application/service/PacienteService.java](backend/src/main/java/tfi/application/service/PacienteService.java)

**Problema**: Líneas 265-273 crean nueva entidad en lugar de usar métodos de negocio.

**Acciones**:

1. Reemplazar creación de nueva entidad por uso de métodos de negocio:
   ```java
   pacienteExistente.actualizarDatosPersonales(dto.getNombre(), dto.getApellido());
   pacienteExistente.actualizarDomicilio(domicilio);
   pacienteExistente.actualizarObraSocial(afiliado);
   pacientesRepository.update(pacienteExistente);
   ```


---

## Fase 4: Mejora de Entidades del Dominio (Prioridad Media)

### 4.1 Reducir Setters Públicos en Ingreso

**Archivo**: [backend/src/main/java/tfi/domain/entity/Ingreso.java](backend/src/main/java/tfi/domain/entity/Ingreso.java)

**Setters a hacer package-private o eliminar**:

- `setPaciente()` (línea 79)
- `setEnfermero()` (línea 87)
- `setDescripcion()` (línea 95)
- `setFechaHoraIngreso()` (línea 103)
- `setTemperatura()` (línea 111)
- `setTensionArterial()` (línea 119)
- `setFrecuenciaCardiaca()` (línea 127)
- `setFrecuenciaRespiratoria()` (línea 135)
- `setNivelEmergencia()` (línea 143)

**Acciones**:

1. Cambiar visibilidad a package-private (sin modificador de acceso)
2. Agregar comentarios indicando que son solo para repositorios
3. Verificar que los repositorios puedan acceder (mismo package)

### 4.2 Agregar Validaciones de Invariantes en Ingreso

**Archivo**: [backend/src/main/java/tfi/domain/entity/Ingreso.java](backend/src/main/java/tfi/domain/entity/Ingreso.java)

**Acciones**:

1. Agregar validaciones en constructores (líneas 32-69):

   - Validar que `paciente != null`
   - Validar que `enfermero != null`
   - Validar que `enfermero.esEnfermero() == true`
   - Validar que `descripcion` no sea null o vacío

### 4.3 Cambiar Atencion para usar Referencias

**Archivo**: [backend/src/main/java/tfi/domain/entity/Atencion.java](backend/src/main/java/tfi/domain/entity/Atencion.java)

**Problema**: Usa `String ingresoId` y `String medicoId` (líneas 14-15) en lugar de referencias.

**Acciones**:

1. Cambiar campos a:
   ```java
   private Ingreso ingreso;
   private Usuario medico;
   ```

2. Actualizar constructores para recibir entidades en lugar de IDs
3. Actualizar métodos `getIngresoId()` y `getMedicoId()` para retornar IDs desde las referencias
4. Actualizar `AtencionService` y repositorios que usen esta entidad
5. Considerar impacto en persistencia (puede requerir mapeo en capa de infraestructura)

**Nota**: Si hay problemas de lazy loading o persistencia, considerar mantener IDs solo en la capa de infraestructura usando un mapper.

---

## Fase 5: Configuración de Spring (Prioridad Media)

### 5.1 Crear Bean para ColaAtencionService del Dominio

**Archivo nuevo**: [backend/src/main/java/tfi/config/DomainServiceConfig.java](backend/src/main/java/tfi/config/DomainServiceConfig.java)

**Acciones**:

1. Crear clase de configuración con `@Configuration`
2. Crear método `@Bean` que instancie `ColaAtencionService` del dominio
3. Esto permite que Spring gestione el ciclo de vida sin que el dominio dependa de Spring

---

## Orden de Ejecución Recomendado

1. **Fase 2** (ColaAtencionService) - Base para otras mejoras
2. **Fase 1** (IngresoController) - Depende de mejoras en servicios
3. **Fase 3** (Servicios) - Mejora la capa de aplicación
4. **Fase 4** (Entidades) - Mejora el dominio
5. **Fase 5** (Configuración) - Soporte para servicios de dominio

---

## Consideraciones Importantes

- **Testing**: Cada cambio debe mantener o mejorar la capacidad de testing
- **Compatibilidad**: Verificar que los cambios no rompan la API REST existente
- **Persistencia**: Revisar implementaciones de repositorios al cambiar entidades
- **Mappers**: Asegurar que los mappers funcionen correctamente con los cambios

---

## Archivos a Modificar

1. `backend/src/main/java/tfi/controller/IngresoController.java`
2. `backend/src/main/java/tfi/application/service/IngresoService.java`
3. `backend/src/main/java/tfi/application/service/PacienteService.java`
4. `backend/src/main/java/tfi/domain/entity/Ingreso.java`
5. `backend/src/main/java/tfi/domain/entity/Atencion.java`
6. `backend/src/main/java/tfi/application/service/ColaAtencionService.java` (mover)
7. `backend/src/main/java/tfi/domain/service/ColaAtencionService.java` (nuevo)
8. `backend/src/main/java/tfi/config/DomainServiceConfig.java` (nuevo)