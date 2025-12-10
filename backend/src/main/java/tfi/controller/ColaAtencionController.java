package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.dto.IngresoResponse;
import tfi.domain.enums.Autoridad;
import tfi.application.service.IngresoService;
import tfi.util.SecurityContext;

import java.util.List;

/**
 * Controlador REST para endpoints de gestión de la cola de atención.
 * Maneja consulta, atención y operaciones relacionadas con la cola de urgencias.
 */
@RestController
@RequestMapping("/api/cola-atencion")
public class ColaAtencionController {
    
    private final IngresoService ingresoService;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param ingresoService Servicio de ingresos
     */
    public ColaAtencionController(IngresoService ingresoService) {
        this.ingresoService = ingresoService;
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
        
        List<IngresoResponse> responses = ingresoService.obtenerColaDeAtencion();
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
        
        IngresoResponse siguiente = ingresoService.verSiguientePaciente();
        
        if (siguiente == null) {
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.ok(siguiente);
    }

    /**
     * Endpoint para reclamar y atender al siguiente paciente en la cola.
     * Remueve el ingreso de la cola de espera y cambia su estado a EN_PROCESO.
     * Endpoint protegido - requiere JWT válido y autoridad MEDICO.
     * 
     * POST /api/cola-atencion/atender
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con el ingreso atendido
     *         400 Bad Request si no hay pacientes en espera
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad MEDICO
     */
    @PostMapping("/atender")
    public ResponseEntity<IngresoResponse> atenderSiguientePaciente(
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.MEDICO);
        
        IngresoResponse ingresoAtendido = ingresoService.atenderSiguientePaciente();
        
        if (ingresoAtendido == null) {
            throw new IllegalStateException("No hay pacientes en la lista de espera");
        }
        
        return ResponseEntity.ok(ingresoAtendido);
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
        
        int cantidad = ingresoService.cantidadPacientesEnEspera();
        return ResponseEntity.ok(cantidad);
    }
}

