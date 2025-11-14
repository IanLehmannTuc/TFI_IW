package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.model.dto.AuthResponse;
import tfi.model.dto.LoginRequest;
import tfi.model.dto.RegistroRequest;
import tfi.model.dto.UsuarioAutenticado;
import tfi.service.AutenticacionService;
import tfi.util.SecurityContext;

/**
 * Controlador REST para endpoints de autenticación.
 * Maneja registro, login y operaciones relacionadas con usuarios.
 */
@RestController
@RequestMapping("/api/auth")
public class AutenticacionController {
    
    private final AutenticacionService autenticacionService;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param autenticacionService Servicio de autenticación
     */
    public AutenticacionController(AutenticacionService autenticacionService) {
        this.autenticacionService = autenticacionService;
    }

    /**
     * Endpoint para registrar un nuevo usuario.
     * Endpoint público - no requiere autenticación.
     * 
     * POST /api/auth/registro
     * Body: { "email": "usuario@example.com", "password": "password123", "autoridad": "MEDICO" }
     * 
     * @param request Datos de registro
     * @return 200 OK con token JWT si el registro es exitoso
     *         400 Bad Request si los datos son inválidos o el email ya existe
     */
    @PostMapping("/registro")
    public ResponseEntity<AuthResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        AuthResponse response = autenticacionService.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Endpoint para iniciar sesión.
     * Endpoint público - no requiere autenticación.
     * 
     * POST /api/auth/login
     * Body: { "email": "usuario@example.com", "password": "password123" }
     * 
     * @param request Credenciales de login
     * @return 200 OK con token JWT si las credenciales son válidas
     *         401 Unauthorized si las credenciales son inválidas
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = autenticacionService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener el perfil del usuario actual.
     * Endpoint protegido - requiere JWT válido en header Authorization.
     * 
     * GET /api/auth/perfil
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con información del usuario
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/perfil")
    public ResponseEntity<UsuarioAutenticado> obtenerPerfil(HttpServletRequest httpRequest) {
        UsuarioAutenticado usuario = SecurityContext.getUsuarioAutenticado(httpRequest);
        return ResponseEntity.ok(usuario);
    }

    /**
     * Endpoint de prueba para verificar autenticación.
     * Endpoint protegido - requiere JWT válido.
     * 
     * GET /api/auth/verificar
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con mensaje de confirmación
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/verificar")
    public ResponseEntity<String> verificarAutenticacion(HttpServletRequest httpRequest) {
        UsuarioAutenticado usuario = SecurityContext.getUsuarioAutenticado(httpRequest);
        return ResponseEntity.ok(
            "Token válido. Usuario autenticado: " + usuario.getEmail() + 
            " con autoridad: " + usuario.getAutoridad()
        );
    }
}

