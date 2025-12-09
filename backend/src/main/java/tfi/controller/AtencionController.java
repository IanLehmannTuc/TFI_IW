package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.dto.AtencionResponse;
import tfi.application.dto.RegistroAtencionRequest;
import tfi.application.dto.UsuarioAutenticado;
import tfi.application.mapper.AtencionMapper;
import tfi.application.service.AtencionService;
import tfi.domain.entity.Atencion;
import tfi.domain.enums.Autoridad;
import tfi.exception.AtencionException;
import tfi.util.SecurityContext;

import java.util.Optional;

/**
 * Controlador REST para endpoints de gestión de atenciones médicas.
 * Maneja el registro y consulta de atenciones de ingresos en urgencias.
 */
@RestController
@RequestMapping("/api/atenciones")
public class AtencionController {

    private final AtencionService atencionService;
    private final AtencionMapper atencionMapper;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param atencionService Servicio de atenciones
     * @param atencionMapper Mapper para convertir Atencion a AtencionResponse
     */
    public AtencionController(AtencionService atencionService, AtencionMapper atencionMapper) {
        this.atencionService = atencionService;
        this.atencionMapper = atencionMapper;
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
        
        // Verificar que el usuario sea médico
        SecurityContext.requireAutoridad(httpRequest, Autoridad.MEDICO);
        
        // Obtener el usuario autenticado
        UsuarioAutenticado usuario = SecurityContext.getUsuarioAutenticado(httpRequest);
        
        // Registrar la atención
        Atencion atencion = atencionService.registrarAtencion(
            request.getIngresoId(),
            usuario.getId(),
            request.getInforme()
        );
        
        // Convertir a DTO de respuesta
        AtencionResponse response = atencionMapper.toResponse(atencion);
        
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
        
        // Verificar autenticación
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        // Buscar la atención
        Optional<Atencion> atencionOpt = atencionService.obtenerAtencionPorIngresoId(ingresoId);
        
        if (atencionOpt.isEmpty()) {
            throw new AtencionException("No se encontró una atención para el ingreso con ID: " + ingresoId);
        }
        
        AtencionResponse response = atencionMapper.toResponse(atencionOpt.get());
        return ResponseEntity.ok(response);
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
        
        // Verificar autenticación
        SecurityContext.getUsuarioAutenticado(httpRequest);
        
        // Buscar la atención
        Optional<Atencion> atencionOpt = atencionService.obtenerAtencionPorId(id);
        
        if (atencionOpt.isEmpty()) {
            throw new AtencionException("No se encontró la atención con ID: " + id);
        }
        
        AtencionResponse response = atencionMapper.toResponse(atencionOpt.get());
        return ResponseEntity.ok(response);
    }
}
