package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.dto.IngresoResponse;
import tfi.application.dto.RegistroIngresoRequest;
import tfi.domain.entity.Usuario;
import tfi.domain.entity.Ingreso;
import tfi.domain.entity.Paciente;
import tfi.domain.enums.Autoridad;
import tfi.domain.valueObject.FrecuenciaCardiaca;
import tfi.domain.valueObject.FrecuenciaRespiratoria;
import tfi.domain.valueObject.Presion;
import tfi.domain.valueObject.Temperatura;
import tfi.domain.valueObject.TensionArterial;
import tfi.application.mapper.IngresoMapper;
import tfi.domain.repository.UsuarioRepository;
import tfi.domain.repository.PacientesRepository;
import tfi.application.service.UrgenciaService;
import tfi.util.SecurityContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para endpoints de gestión de urgencias.
 * Maneja registro, consulta, actualización y eliminación de ingresos.
 */
@RestController
@RequestMapping("/api/urgencias")
public class UrgenciaController {
    
    private final UrgenciaService urgenciaService;
    private final PacientesRepository pacientesRepository;
    private final UsuarioRepository usuarioRepository;
    private final IngresoMapper ingresoMapper;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param urgenciaService Servicio de urgencias
     * @param pacientesRepository Repositorio de pacientes
     * @param usuarioRepository Repositorio de usuarios (personal médico)
     * @param ingresoMapper Mapper para convertir Ingreso a IngresoResponse
     */
    public UrgenciaController(UrgenciaService urgenciaService,
                             PacientesRepository pacientesRepository,
                             UsuarioRepository usuarioRepository,
                             IngresoMapper ingresoMapper) {
        this.urgenciaService = urgenciaService;
        this.pacientesRepository = pacientesRepository;
        this.usuarioRepository = usuarioRepository;
        this.ingresoMapper = ingresoMapper;
    }

    /**
     * Endpoint para registrar un nuevo ingreso a urgencias.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * POST /api/urgencias
     * Header: Authorization: Bearer <token>
     * Body: { "pacienteCuil": "20-20304050-5", "enfermeroCuil": "27-12345678-9", ... }
     * 
     * @param request Datos del ingreso a crear
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 201 Created con datos del ingreso creado
     *         400 Bad Request si los datos son inválidos
     *         404 Not Found si el paciente o enfermero no existen
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad ENFERMERO
     */
    @PostMapping
    public ResponseEntity<IngresoResponse> registrarIngreso(
            @Valid @RequestBody RegistroIngresoRequest request,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERO);
        
        IngresoResponse ingresoResponse = urgenciaService.registrarIngreso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingresoResponse);
    }

    /**
     * Endpoint para obtener todos los ingresos registrados.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/urgencias
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con lista de ingresos
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping
    public ResponseEntity<List<IngresoResponse>> obtenerTodosLosIngresos(
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        List<Ingreso> ingresos = urgenciaService.obtenerTodosLosIngresos();
        List<IngresoResponse> responses = ingresos.stream()
            .map(ingresoMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint para obtener un ingreso por ID.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/urgencias/{id}
     * Header: Authorization: Bearer <token>
     * 
     * @param id El ID del ingreso a buscar
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con los datos del ingreso si existe
     *         404 Not Found si el ingreso no existe
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/{id}")
    public ResponseEntity<IngresoResponse> obtenerIngresoPorId(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        List<Ingreso> ingresos = urgenciaService.obtenerTodosLosIngresos();
        Ingreso ingreso = ingresos.stream()
            .filter(i -> i.getId() != null && i.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (ingreso == null) {
            return ResponseEntity.notFound().build();
        }
        
        IngresoResponse response = ingresoMapper.toResponse(ingreso);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para actualizar un ingreso existente.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * PUT /api/urgencias/{id}
     * Header: Authorization: Bearer <token>
     * Body: { "descripcion": "...", "temperatura": 37.5, ... }
     * 
     * @param id El ID del ingreso a actualizar
     * @param request Datos actualizados del ingreso
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con datos del ingreso actualizado
     *         400 Bad Request si los datos son inválidos
     *         404 Not Found si el ingreso no existe
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad ENFERMERO
     */
    @PutMapping("/{id}")
    public ResponseEntity<IngresoResponse> actualizarIngreso(
            @PathVariable String id,
            @Valid @RequestBody RegistroIngresoRequest request,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERO);
        
        List<Ingreso> ingresos = urgenciaService.obtenerTodosLosIngresos();
        Ingreso ingresoExistente = ingresos.stream()
            .filter(i -> i.getId() != null && i.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (ingresoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        
        Paciente paciente = pacientesRepository.findByCuil(request.getPacienteCuil())
            .orElse(null);
        if (paciente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        Usuario enfermero = usuarioRepository.findByCuil(request.getEnfermeroCuil())
            .orElse(null);
        if (enfermero == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        
        try {
            Temperatura temperatura = new Temperatura(request.getTemperatura());
            TensionArterial tensionArterial = new TensionArterial(
                new Presion(request.getTensionSistolica()),
                new Presion(request.getTensionDiastolica())
            );
            FrecuenciaCardiaca frecuenciaCardiaca = new FrecuenciaCardiaca(request.getFrecuenciaCardiaca());
            FrecuenciaRespiratoria frecuenciaRespiratoria = new FrecuenciaRespiratoria(request.getFrecuenciaRespiratoria());
            
            Ingreso ingresoActualizado = new Ingreso(
                ingresoExistente.getAtencion(),
                paciente,
                enfermero,
                request.getDescripcion(),
                ingresoExistente.getFechaHoraIngreso(),
                temperatura,
                tensionArterial,
                frecuenciaCardiaca,
                frecuenciaRespiratoria,
                request.getNivelEmergencia()
            );
            ingresoActualizado.setId(id);
            
            Ingreso ingresoGuardado = urgenciaService.actualizarIngreso(ingresoActualizado);
            IngresoResponse response = ingresoMapper.toResponse(ingresoGuardado);
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Endpoint para eliminar un ingreso.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * DELETE /api/urgencias/{id}
     * Header: Authorization: Bearer <token>
     * 
     * @param id El ID del ingreso a eliminar
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK si se eliminó correctamente
     *         404 Not Found si el ingreso no existe
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad ENFERMERO
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarIngreso(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERO);
        
        List<Ingreso> ingresos = urgenciaService.obtenerTodosLosIngresos();
        Ingreso ingreso = ingresos.stream()
            .filter(i -> i.getId() != null && i.getId().equals(id))
            .findFirst()
            .orElse(null);
        
        if (ingreso == null) {
            return ResponseEntity.notFound().build();
        }
        
        urgenciaService.eliminarIngreso(ingreso);
        return ResponseEntity.ok().build();
    }
}
