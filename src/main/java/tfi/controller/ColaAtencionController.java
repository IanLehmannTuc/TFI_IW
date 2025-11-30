package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.mapper.IngresoMapper;
import tfi.application.dto.IngresoResponse;
import tfi.domain.entity.Ingreso;
import tfi.domain.enums.Autoridad;
import tfi.application.service.ColaAtencionService;
import tfi.application.service.UrgenciaService;
import tfi.util.SecurityContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para endpoints de gestión de la cola de atención.
 * Maneja consulta, atención y operaciones relacionadas con la cola de urgencias.
 */
@RestController
@RequestMapping("/api/cola-atencion")
public class ColaAtencionController {
    
    private final ColaAtencionService colaAtencionService;
    private final UrgenciaService urgenciaService;
    private final IngresoMapper ingresoMapper;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param colaAtencionService Servicio de cola de atención
     * @param urgenciaService Servicio de urgencias
     * @param ingresoMapper Mapper para convertir Ingreso a IngresoResponse
     */
    public ColaAtencionController(ColaAtencionService colaAtencionService,
                                 UrgenciaService urgenciaService,
                                 IngresoMapper ingresoMapper) {
        this.colaAtencionService = colaAtencionService;
        this.urgenciaService = urgenciaService;
        this.ingresoMapper = ingresoMapper;
    }

    /**
     * Endpoint para obtener la cola de atención ordenada por prioridad.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/cola-atencion
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con lista de ingresos ordenados por prioridad
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping
    public ResponseEntity<List<IngresoResponse>> obtenerColaDeAtencion(
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        List<Ingreso> cola = urgenciaService.obtenerColaDeAtencion();
        List<IngresoResponse> responses = cola.stream()
            .map(ingresoMapper::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint para ver el siguiente paciente a atender sin removerlo de la cola.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/cola-atencion/siguiente
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con el siguiente ingreso a atender
     *         204 No Content si no hay pacientes en espera
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/siguiente")
    public ResponseEntity<IngresoResponse> verSiguientePaciente(
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        Ingreso siguiente = colaAtencionService.verSiguiente();
        
        if (siguiente == null) {
            return ResponseEntity.noContent().build();
        }
        
        IngresoResponse response = ingresoMapper.toResponse(siguiente);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para atender al siguiente paciente en la cola.
     * Remueve el ingreso de la cola pero NO lo elimina del repositorio.
     * Endpoint protegido - requiere JWT válido y autoridad MEDICO.
     * 
     * POST /api/cola-atencion/atender
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con el ingreso atendido
     *         204 No Content si no hay pacientes en espera
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad MEDICO
     */
    @PostMapping("/atender")
    public ResponseEntity<IngresoResponse> atenderSiguientePaciente(
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.MEDICO);
        
        Ingreso ingresoAtendido = urgenciaService.atenderSiguientePaciente();
        
        if (ingresoAtendido == null) {
            return ResponseEntity.noContent().build();
        }
        
        IngresoResponse response = ingresoMapper.toResponse(ingresoAtendido);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener la cantidad de pacientes en espera.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/cola-atencion/cantidad
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con la cantidad de pacientes en espera
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/cantidad")
    public ResponseEntity<Integer> cantidadPacientesEnEspera(
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        int cantidad = urgenciaService.cantidadPacientesEnEspera();
        return ResponseEntity.ok(cantidad);
    }
}

