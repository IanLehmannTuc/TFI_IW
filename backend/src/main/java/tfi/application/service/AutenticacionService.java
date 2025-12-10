package tfi.application.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tfi.config.JwtConfig;
import tfi.exception.AutenticacionException;
import tfi.exception.RegistroException;
import tfi.application.dto.AuthResponse;
import tfi.application.dto.LoginRequest;
import tfi.application.dto.PerfilUsuarioResponse;
import tfi.application.dto.RegistroRequest;
import tfi.application.dto.RegistroResponse;
import tfi.domain.entity.Usuario;
import tfi.domain.valueObject.Cuil;
import tfi.domain.valueObject.Email;      
import tfi.domain.valueObject.Password;   
import tfi.domain.repository.UsuarioRepository;
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
     * Registra un nuevo usuario en el sistema con sus datos profesionales.
     * Valida los datos usando Value Objects y hashea la contraseña.
     * 
     * TRANSACCIONAL: Si falla cualquier parte del registro,
     * se hace rollback completo para mantener la consistencia de la base de datos.
     * 
     * @param request Datos de registro del usuario (email, contraseña, autoridad, cuil, nombre, apellido, matrícula)
     * @return Respuesta con datos del usuario creado (id, nombre, apellido, autoridad)
     * @throws RegistroException Si los datos son inválidos o el email ya existe
     * @throws IllegalArgumentException Si el request es null
     */
    @Transactional
    public RegistroResponse registrar(@NonNull RegistroRequest request) {
        
        
        
        
        Email email;
        Password password;
        Cuil cuil;
        
        try {
            email = Email.from(request.getEmail());
            password = Password.from(request.getPassword());
            cuil = new Cuil(request.getCuil());
        } catch (IllegalArgumentException e) {
            throw new RegistroException(e.getMessage());
        }
        
        if (usuarioRepository.existsByEmail(email.getValue())) {
            throw new RegistroException(MensajesError.EMAIL_YA_REGISTRADO);
        }
        if (usuarioRepository.existsByCuil(cuil.getValor())) {
            throw new RegistroException("Ya existe un usuario con el CUIL: " + cuil.getValor());
        }
        if (usuarioRepository.existsByMatricula(request.getMatricula())) {
            throw new RegistroException("Ya existe un usuario con la matrícula: " + request.getMatricula());
        }
        
        String passwordHash = PasswordHasher.hashPassword(password.getValue());
        
        Usuario usuario = new Usuario(
            email, 
            passwordHash, 
            request.getAutoridad(),
            cuil,
            request.getNombre(),
            request.getApellido(),
            request.getMatricula()
        );
        
        usuarioRepository.add(usuario);
        
        return new RegistroResponse(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getAutoridad()
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

    /**
     * Obtiene el perfil completo del usuario autenticado.
     * Consulta el repositorio para obtener todos los datos del usuario.
     * 
     * @param email Email del usuario autenticado
     * @return Respuesta con todos los datos del usuario (id, email, nombre, apellido, cuil, matricula, autoridad)
     * @throws AutenticacionException Si el usuario no existe
     * @throws IllegalArgumentException Si el email es null
     */
    public PerfilUsuarioResponse obtenerPerfil(@NonNull String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        
        if (usuarioOpt.isEmpty()) {
            throw new AutenticacionException("Usuario no encontrado");
        }
        
        Usuario usuario = usuarioOpt.get();
        
        return new PerfilUsuarioResponse(
            usuario.getId(),
            usuario.getEmail().getValue(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getCuil(),
            usuario.getMatricula(),
            usuario.getAutoridad()
        );
    }

    /**
     * Obtiene el perfil de un usuario por su CUIL.
     * Consulta el repositorio para obtener todos los datos del usuario excepto la contraseña.
     * 
     * @param cuil CUIL del usuario a buscar
     * @return Respuesta con todos los datos del usuario (id, email, nombre, apellido, cuil, matricula, autoridad)
     * @throws AutenticacionException Si el usuario no existe
     * @throws IllegalArgumentException Si el CUIL es null o vacío
     */
    public PerfilUsuarioResponse obtenerUsuarioPorCuil(@NonNull String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL no puede ser nulo o vacío");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCuil(cuil);
        
        if (usuarioOpt.isEmpty()) {
            throw new AutenticacionException("Usuario no encontrado con CUIL: " + cuil);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        return new PerfilUsuarioResponse(
            usuario.getId(),
            usuario.getEmail().getValue(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getCuil(),
            usuario.getMatricula(),
            usuario.getAutoridad()
        );
    }

    /**
     * Obtiene el perfil de un usuario por su ID.
     * Consulta el repositorio para obtener todos los datos del usuario excepto la contraseña.
     * 
     * @param id ID del usuario a buscar
     * @return Respuesta con todos los datos del usuario (id, email, nombre, apellido, cuil, matricula, autoridad)
     * @throws AutenticacionException Si el usuario no existe
     * @throws IllegalArgumentException Si el ID es null o vacío
     */
    public PerfilUsuarioResponse obtenerUsuarioPorId(@NonNull String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID no puede ser nulo o vacío");
        }
        
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        
        if (usuarioOpt.isEmpty()) {
            throw new AutenticacionException("Usuario no encontrado con ID: " + id);
        }
        
        Usuario usuario = usuarioOpt.get();
        
        return new PerfilUsuarioResponse(
            usuario.getId(),
            usuario.getEmail().getValue(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getCuil(),
            usuario.getMatricula(),
            usuario.getAutoridad()
        );
    }
}

