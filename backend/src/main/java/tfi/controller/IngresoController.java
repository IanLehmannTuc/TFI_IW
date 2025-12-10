package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.dto.IngresoResponse;
import tfi.application.dto.RegistroIngresoRequest;
import tfi.domain.enums.Autoridad;
import tfi.application.service.IngresoService;
import tfi.util.SecurityContext;

import java.util.List;

/**
 * Controlador REST para endpoints de gestión de ingresos.
 * Maneja registro, consulta, actualización y eliminación de ingresos.
 */
@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final IngresoService ingresoService;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param ingresoService Servicio de ingresos
     */
    public IngresoController(IngresoService ingresoService) {
        this.ingresoService = ingresoService;
    }

    /**
     * Endpoint para registrar un nuevo ingreso.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * POST /api/ingresos
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

        IngresoResponse ingresoResponse = ingresoService.registrarIngreso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ingresoResponse);
    }

    /**
     * Endpoint para obtener todos los ingresos registrados.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/ingresos
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

        List<IngresoResponse> responses = ingresoService.obtenerTodosLosIngresos();
        return ResponseEntity.ok(responses);
    }

    /**
     * Endpoint para obtener un ingreso por ID.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/ingresos/{id}
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

        try {
            IngresoResponse response = ingresoService.obtenerIngresoPorId(id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para actualizar un ingreso existente.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * PUT /api/ingresos/{id}
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

        try {
            IngresoResponse response = ingresoService.actualizarIngreso(id, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Endpoint para eliminar un ingreso.
     * Endpoint protegido - requiere JWT válido y autoridad ENFERMERO.
     * 
     * DELETE /api/ingresos/{id}
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

        try {
            ingresoService.eliminarIngreso(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}

