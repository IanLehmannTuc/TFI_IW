package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.model.dto.PacienteResponse;
import tfi.model.dto.RegistroPacienteRequest;
import tfi.model.enums.Autoridad;
import tfi.service.PacienteService;
import tfi.util.SecurityContext;

/**
 * Controlador REST para endpoints de gestión de pacientes.
 * Maneja registro y consulta de pacientes.
 */
@RestController
@RequestMapping("/api/pacientes")
public class PacienteController {
    
    private final PacienteService pacienteService;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param pacienteService Servicio de pacientes
     */
    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    /**
     * Endpoint para crear un nuevo paciente.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERA.
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
     *         403 Forbidden si el usuario no tiene autoridad ENFERMERA
     */
    @PostMapping
    public ResponseEntity<PacienteResponse> crear(
            @Valid @RequestBody RegistroPacienteRequest request,
            HttpServletRequest httpRequest) {
        
        SecurityContext.requireAutoridad(httpRequest, Autoridad.ENFERMERA);
        
        PacienteResponse response = pacienteService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
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
        
        var paciente = pacienteService.findByCuil(cuil);
        
        if (paciente == null) {
            return ResponseEntity.notFound().build();
        }
        
        PacienteResponse response = convertirPacienteAResponse(paciente);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Convierte un Paciente a PacienteResponse.
     */
    private PacienteResponse convertirPacienteAResponse(tfi.model.entity.Paciente paciente) {
        PacienteResponse.DomicilioResponse domicilioResponse = new PacienteResponse.DomicilioResponse(
            paciente.getDomicilio().getCalle(),
            paciente.getDomicilio().getNumero(),
            paciente.getDomicilio().getLocalidad()
        );
        
        PacienteResponse.AfiliadoResponse afiliadoResponse = null;
        if (paciente.getObraSocial() != null) {
            PacienteResponse.ObraSocialResponse obraSocialResponse = new PacienteResponse.ObraSocialResponse(
                paciente.getObraSocial().getObraSocial().getIdObraSocial(),
                paciente.getObraSocial().getObraSocial().getNombreObraSocial()
            );
            afiliadoResponse = new PacienteResponse.AfiliadoResponse(
                obraSocialResponse,
                paciente.getObraSocial().getNumeroAfiliado()
            );
        }
        
        return new PacienteResponse(
            paciente.getCuil(),
            paciente.getNombre(),
            paciente.getApellido(),
            domicilioResponse,
            afiliadoResponse
        );
    }
}

