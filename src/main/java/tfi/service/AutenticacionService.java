package tfi.service;

import org.springframework.lang.NonNull;
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
import tfi.util.MensajesError;
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
    public AutenticacionService(@NonNull UsuarioRepository usuarioRepository, 
                                @NonNull JwtUtil jwtUtil,
                                @NonNull JwtConfig jwtConfig) {
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
     * @throws IllegalArgumentException Si el request es null
     */
    public AuthResponse registrar(@NonNull RegistroRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request de registro no puede ser nulo");
        }
        if (request.getAutoridad() == null) {
            throw new RegistroException(MensajesError.AUTORIDAD_REQUERIDA);
        }
        
        Email email;
        Password password;
        
        try {
            email = Email.from(request.getEmail());
            password = Password.from(request.getPassword());
        } catch (IllegalArgumentException e) {
            throw new RegistroException(e.getMessage());
        }
        
        if (usuarioRepository.existsByEmail(email.getValue())) {
            throw new RegistroException(MensajesError.EMAIL_YA_REGISTRADO);
        }
        
        String passwordHash = PasswordHasher.hashPassword(password.getValue());
        
        Usuario usuario = new Usuario(email, passwordHash, request.getAutoridad());
        
        usuarioRepository.add(usuario);
        
        String token = jwtUtil.generateToken(usuario);
        
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
     * @throws IllegalArgumentException Si el request es null
     */
    public AuthResponse login(@NonNull LoginRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request de login no puede ser nulo");
        }
        Email email;
        try {
            email = Email.from(request.getEmail());
        } catch (IllegalArgumentException e) {
            throw new AutenticacionException(MensajesError.USUARIO_CONTRASENA_INVALIDOS);
        }
        
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new AutenticacionException(MensajesError.USUARIO_CONTRASENA_INVALIDOS);
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email.getValue());
        
        if (usuarioOpt.isEmpty()) {
            throw new AutenticacionException(MensajesError.USUARIO_CONTRASENA_INVALIDOS);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        boolean passwordValida = PasswordHasher.checkPassword(
            request.getPassword(), 
            usuario.getPasswordHash()
        );
        
        if (!passwordValida) {
            throw new AutenticacionException(MensajesError.USUARIO_CONTRASENA_INVALIDOS);
        }
        
        String token = jwtUtil.generateToken(usuario);
        
        return new AuthResponse(
            token,
            usuario.getEmail().getValue(),
            usuario.getAutoridad(),
            jwtConfig.getExpirationTime()
        );
    }
}

