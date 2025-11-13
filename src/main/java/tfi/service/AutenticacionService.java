package tfi.service;

import org.springframework.stereotype.Service;
import tfi.config.JwtConfig;
import tfi.exception.AutenticacionException;
import tfi.exception.RegistroException;
import tfi.model.dto.AuthResponse;
import tfi.model.dto.LoginRequest;
import tfi.model.dto.RegistroRequest;
import tfi.model.entity.Usuario;
import tfi.model.valueObjects.Email;      
import tfi.model.valueObjects.Password;   
import tfi.repository.interfaces.UsuarioRepository;
import tfi.util.JwtUtil;
import tfi.util.PasswordHasher;

import java.util.Optional;

/**
 * Servicio para gestionar operaciones de autenticación y registro de usuarios.
 * Implementa la lógica de negocio siguiendo principios de arquitectura limpia.
 */
@Service
public class AutenticacionService {
    
    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param usuarioRepository Repositorio de usuarios
     * @param jwtUtil Utilidad para manejo de JWT
     * @param jwtConfig Configuración de JWT
     */
    public AutenticacionService(UsuarioRepository usuarioRepository, 
                                JwtUtil jwtUtil,
                                JwtConfig jwtConfig) {
        this.usuarioRepository = usuarioRepository;
        this.jwtUtil = jwtUtil;
        this.jwtConfig = jwtConfig;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Valida los datos usando Value Objects, hashea la contraseña y genera un token JWT.
     * 
     * @param request Datos de registro del usuario
     * @return Respuesta con token JWT y datos del usuario
     * @throws RegistroException Si los datos son inválidos o el email ya existe
     */
    public AuthResponse registrar(RegistroRequest request) {
        // 1. Validar que el request no sea nulo
        if (request == null) {
            throw new RegistroException("Los datos de registro no pueden ser nulos");
        }
        
        // 2. Validar autoridad
        if (request.getAutoridad() == null) {
            throw new RegistroException("Debe especificar una autoridad (MEDICO o ENFERMERA)");
        }
        
        // 3. Crear Value Objects (validación implícita)
        Email email;
        Password password;
        
        try {
            email = Email.from(request.getEmail());
            password = Password.from(request.getPassword());
        } catch (IllegalArgumentException e) {
            throw new RegistroException(e.getMessage());
        }
        
        // 4. Verificar que el email no exista
        if (usuarioRepository.existsByEmail(email.getValue())) {
            throw new RegistroException("El email ya está registrado");
        }
        
        // 5. Hashear contraseña con BCrypt
        String passwordHash = PasswordHasher.hashPassword(password.getValue());
        
        // 6. Crear usuario con Value Objects
        Usuario usuario = new Usuario(email, passwordHash, request.getAutoridad());
        
        // 7. Guardar en repositorio
        usuarioRepository.add(usuario);
        
        // 8. Generar JWT token
        String token = jwtUtil.generateToken(usuario);
        
        // 9. Retornar AuthResponse con token
        return new AuthResponse(
            token,
            usuario.getEmail().getValue(),
            usuario.getAutoridad(),
            jwtConfig.getExpirationTime()
        );
    }

    /**
     * Autentica un usuario en el sistema.
     * Verifica las credenciales y genera un token JWT si son válidas.
     * 
     * IMPORTANTE: Siempre retorna el mismo mensaje de error para prevenir
     * enumeración de usuarios (no revelar si el email existe o no).
     * 
     * @param request Credenciales de inicio de sesión
     * @return Respuesta con token JWT y datos del usuario
     * @throws AutenticacionException Si las credenciales son inválidas
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Validar que el request no sea nulo
        if (request == null) {
            throw new AutenticacionException("Usuario o contraseña inválidos");
        }
        
        // 2. Validar que se proporcionaron email y password
        if (request.getEmail() == null || request.getEmail().trim().isEmpty() ||
            request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new AutenticacionException("Usuario o contraseña inválidos");
        }
        
        // 3. Buscar usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(request.getEmail());
        
        // 4. Si no existe -> excepción genérica (NO revelar que el usuario no existe)
        if (usuarioOpt.isEmpty()) {
            throw new AutenticacionException("Usuario o contraseña inválidos");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        // 5. Verificar contraseña con BCrypt
        boolean passwordValida = PasswordHasher.checkPassword(
            request.getPassword(), 
            usuario.getPasswordHash()
        );
        
        // 6. Si contraseña incorrecta -> excepción genérica (mismo mensaje)
        if (!passwordValida) {
            throw new AutenticacionException("Usuario o contraseña inválidos");
        }
        
        // 7. Generar JWT token
        String token = jwtUtil.generateToken(usuario);
        
        // 8. Retornar AuthResponse con token
        return new AuthResponse(
            token,
            usuario.getEmail().getValue(),
            usuario.getAutoridad(),
            jwtConfig.getExpirationTime()
        );
    }

    /**
     * Busca un usuario por su email.
     * Útil para otros servicios que necesiten información del usuario.
     * 
     * @param email El email del usuario
     * @return Optional con el usuario si existe
     */
    public Optional<Usuario> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }
}

