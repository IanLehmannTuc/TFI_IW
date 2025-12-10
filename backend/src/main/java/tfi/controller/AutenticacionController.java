package tfi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tfi.application.dto.AuthResponse;
import tfi.application.dto.LoginRequest;
import tfi.application.dto.PerfilUsuarioResponse;
import tfi.application.dto.RegistroRequest;
import tfi.application.dto.RegistroResponse;
import tfi.application.dto.UsuarioAutenticado;
import tfi.application.service.AutenticacionService;
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
     * @return 201 Created con datos del usuario creado (id, nombre, apellido, autoridad)
     *         400 Bad Request si los datos son inválidos o el email ya existe
     */
    @PostMapping("/registro")
    public ResponseEntity<RegistroResponse> registrar(@Valid @RequestBody RegistroRequest request) {
        RegistroResponse response = autenticacionService.registrar(request);
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
     * Endpoint para obtener el perfil completo del usuario actual.
     * Endpoint protegido - requiere JWT válido en header Authorization.
     * 
     * GET /api/auth/perfil
     * Header: Authorization: Bearer <token>
     * 
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con información completa del usuario (id, email, nombre, apellido, cuil, matricula, autoridad)
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/perfil")
    public ResponseEntity<PerfilUsuarioResponse> obtenerPerfil(HttpServletRequest httpRequest) {
        UsuarioAutenticado usuarioAutenticado = SecurityContext.getUsuarioAutenticado(httpRequest);
        PerfilUsuarioResponse perfil = autenticacionService.obtenerPerfil(usuarioAutenticado.getEmail());
        return ResponseEntity.ok(perfil);
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

    /**
     * Endpoint para obtener un usuario por su ID.
     * Endpoint protegido - requiere JWT válido en header Authorization.
     * 
     * GET /api/auth/usuario/{id}
     * Header: Authorization: Bearer <token>
     * 
     * @param id ID del usuario a buscar
     * @param httpRequest Request HTTP con información del usuario autenticado
     * @return 200 OK con información completa del usuario (id, email, nombre, apellido, cuil, matricula, autoridad)
     *         404 Not Found si el usuario no existe
     *         401 Unauthorized si no hay token o es inválido
     */
    @GetMapping("/usuario/{id}")
    public ResponseEntity<PerfilUsuarioResponse> obtenerUsuarioPorId(
            @PathVariable String id,
            HttpServletRequest httpRequest) {
        SecurityContext.getUsuarioAutenticado(httpRequest);

        PerfilUsuarioResponse usuario = autenticacionService.obtenerUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }
}

