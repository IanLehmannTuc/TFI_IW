package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tfi.application.dto.ObraSocialResponse;
import tfi.domain.port.ObraSocialPort;
import tfi.util.SecurityContext;

import java.util.List;

/**
 * Controlador REST para endpoints de gestión de obras sociales.
 * Maneja consulta de obras sociales disponibles.
 */
@RestController
@RequestMapping("/api/obras-sociales")
public class ObraSocialController {

    private final ObraSocialPort obraSocialPort;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param obraSocialPort Puerto para operaciones con obras sociales
     */
    public ObraSocialController(ObraSocialPort obraSocialPort) {
        this.obraSocialPort = obraSocialPort;
    }

    /**
     * Endpoint para listar todas las obras sociales disponibles.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/obras-sociales
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con lista de obras sociales disponibles
     *         400 Bad Request si hay un error al consultar la API externa
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping
    public ResponseEntity<List<ObraSocialResponse>> listarObrasSociales(
            HttpServletRequest httpRequest) {

        SecurityContext.getUsuarioAutenticado(httpRequest);

        List<ObraSocialResponse> obrasSociales = obraSocialPort.listarObrasSociales();
        return ResponseEntity.ok(obrasSociales);
    }
}
