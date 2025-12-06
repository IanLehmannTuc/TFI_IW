package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.mapper.PacienteMapper;
import tfi.application.dto.PacienteResponse;
import tfi.application.dto.RegistroPacienteRequest;
import tfi.domain.enums.Autoridad;
import tfi.application.service.PacienteService;
import tfi.util.SecurityContext;

/**
 * Controlador REST para endpoints de gestión de pacientes.
 * Maneja registro y consulta de pacientes.
 */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {
    
    private final PacienteService pacienteService;
    private final PacienteMapper pacienteMapper;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param pacienteService Servicio de pacientes
     * @param pacienteMapper Mapper para convertir Paciente a PacienteResponse
     */
    public PacienteController(PacienteService pacienteService, PacienteMapper pacienteMapper) {
        this.pacienteService = pacienteService;
        this.pacienteMapper = pacienteMapper;
    }

    /**
     * Endpoint para crear un nuevo paciente.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * POST /api/pacientes
     * Header: Authorization: Bearer <token>
     * Body: { "cuil": "20-20304050-5", "nombre": "Juan", "apellido": "Pérez", ... }
     * 
     * @param request Datos del paciente a crear
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 201 Created con datos del paciente creado
     *         400 Bad Request si los datos son inválidos o el CUIL ya existe
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad ENFERMERO
     */
    @PostMapping
    public ResponseEntity<PacienteResponse> crear(
            @Valid @RequestBody RegistroPacienteRequest request,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERO);
        
        PacienteResponse response = pacienteService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para listar todos los pacientes con paginación.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/pacientes?page=0&size=10&sort=cuil&direction=ASC
     * Header: Authorization: Bearer <token>
     * 
     * @param page número de página (0-indexed, por defecto 0)
     * @param size tamaño de la página (por defecto 10)
     * @param sortBy campo por el cual ordenar (por defecto "cuil")
     * @param direction dirección del ordenamiento: ASC o DESC (por defecto ASC)
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con página de pacientes paginada
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping
    public ResponseEntity<Page<PacienteResponse>> listarTodos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "cuil") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction,
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        Page<PacienteResponse> pacientesPage = pacienteService.findAll(pageable)
            .map(pacienteMapper::toResponse);
        
        return ResponseEntity.ok(pacientesPage);
    }

    /**
     * Endpoint para buscar un paciente por CUIL.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/pacientes/{cuil}
     * Header: Authorization: Bearer <token>
     * 
     * @param cuil El CUIL del paciente a buscar
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con los datos del paciente si existe
     *         404 Not Found si el paciente no existe
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/{cuil}")
    public ResponseEntity<PacienteResponse> buscarPorCuil(
            @PathVariable String cuil,
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        return pacienteService.findByCuil(cuil)
            .map(paciente -> ResponseEntity.ok(pacienteMapper.toResponse(paciente)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para actualizar un paciente existente por CUIL.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * PUT /api/pacientes/{cuil}
     * Header: Authorization: Bearer <token>
     * Body: { "cuil": "20-20304050-5", "nombre": "Juan", "apellido": "Pérez", ... }
     * 
     * @param cuil El CUIL del paciente a actualizar
     * @param request Datos actualizados del paciente
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con los datos del paciente actualizado
     *         400 Bad Request si los datos son inválidos o el CUIL no coincide
     *         404 Not Found si el paciente no existe
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad ENFERMERO
     */
    @PutMapping("/{cuil}")
    public ResponseEntity<PacienteResponse> actualizar(
            @PathVariable String cuil,
            @Valid @RequestBody RegistroPacienteRequest request,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERO);
        
        PacienteResponse response = pacienteService.actualizar(cuil, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para eliminar un paciente por CUIL.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * DELETE /api/pacientes/{cuil}
     * Header: Authorization: Bearer <token>
     * 
     * @param cuil El CUIL del paciente a eliminar
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con los datos del paciente eliminado
     *         400 Bad Request si el CUIL es inválido
     *         404 Not Found si el paciente no existe
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad ENFERMERO
     */
    @DeleteMapping("/{cuil}")
    public ResponseEntity<PacienteResponse> eliminar(
            @PathVariable String cuil,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERO);
        
        PacienteResponse response = pacienteService.eliminar(cuil);
        return ResponseEntity.ok(response);
    }
}

