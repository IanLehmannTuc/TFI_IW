package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.dto.AtencionResponse;
import tfi.application.dto.RegistroAtencionRequest;
import tfi.application.dto.UsuarioAutenticado;
import tfi.application.service.AtencionService;
import tfi.domain.enums.Autoridad;
import tfi.exception.AtencionException;
import tfi.util.SecurityContext;

/**
 * Controlador REST para endpoints de gestión de atenciones médicas.
 * Maneja el registro y consulta de atenciones de ingresos en urgencias.
 */
@RestController
@RequestMapping("/api/atenciones")
public class AtencionController {

    private final AtencionService atencionService;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param atencionService Servicio de atenciones
     */
    public AtencionController(AtencionService atencionService) {
        this.atencionService = atencionService;
    }

    /**
     * Endpoint para registrar una nueva atención médica.
     * El médico debe estar autenticado y tener autoridad MEDICO.
     * Valida que el informe sea obligatorio y que el ingreso esté en estado EN_PROCESO.
     * Al registrar la atención, cambia el estado del ingreso a FINALIZADO.
     * 
     * POST /api/atenciones
     * Header: Authorization: Bearer <token>
     * Body: { "ingresoId": "uuid", "informe": "texto del informe" }
     * 
     * @param request DTO con los datos de la atención
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 201 Created con los datos de la atención registrada
     *         400 Bad Request si el informe está vacío o hay errores de validación
     *         401 Unauthorized si no hay token o es inválido
     *         403 Forbidden si el usuario no tiene autoridad MEDICO
     */
    @PostMapping
    public ResponseEntity<AtencionResponse> registrarAtencion(
            @RequestBody RegistroAtencionRequest request,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.MEDICO);
        
        UsuarioAutenticado usuario = SecurityContext.getUsuarioAutenticado(httpRequest);
        
        AtencionResponse response = atencionService.registrarAtencion(
            request.getIngresoId(),
            usuario.getId(),
            request.getInforme()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para obtener la atención de un ingreso específico.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/atenciones/ingreso/{ingresoId}
     * Header: Authorization: Bearer <token>
     * 
     * @param ingresoId ID del ingreso
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con los datos de la atención
     *         404 Not Found si no existe atención para ese ingreso
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/ingreso/{ingresoId}")
    public ResponseEntity<AtencionResponse> obtenerAtencionPorIngresoId(
            @PathVariable String ingresoId,
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        try {
            AtencionResponse response = atencionService.obtenerAtencionPorIngresoId(ingresoId);
            return ResponseEntity.ok(response);
        } catch (AtencionException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para obtener una atención por su ID.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/atenciones/{id}
     * Header: Authorization: Bearer <token>
     * 
     * @param id ID de la atención
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con los datos de la atención
     *         404 Not Found si no existe la atención
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/{id}")
    public ResponseEntity<AtencionResponse> obtenerAtencionPorId(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        try {
            AtencionResponse response = atencionService.obtenerAtencionPorId(id);
            return ResponseEntity.ok(response);
        } catch (AtencionException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
