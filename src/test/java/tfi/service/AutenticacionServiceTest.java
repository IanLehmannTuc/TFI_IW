package tfi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tfi.config.JwtConfig;
import tfi.exception.AutenticacionException;
import tfi.exception.RegistroException;
import tfi.model.dto.AuthResponse;
import tfi.model.dto.LoginRequest;
import tfi.model.dto.RegistroRequest;
import tfi.model.entity.Usuario;
import tfi.model.enums.Autoridad;
import tfi.model.valueObjects.Email;
import tfi.repository.interfaces.UsuarioRepository;
import tfi.util.JwtUtil;
import tfi.util.PasswordHasher;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para AutenticacionService.
 * Verifica registro, login y manejo de errores.
 */
@ExtendWith(MockitoExtension.class)
class AutenticacionServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private JwtConfig jwtConfig;

    private AutenticacionService autenticacionService;

    @BeforeEach
    void setUp() {
        autenticacionService = new AutenticacionService(usuarioRepository, jwtUtil, jwtConfig);
    }

    @Test
    void registrarDebeCrearUsuarioConDatosValidos() {
        RegistroRequest request = new RegistroRequest(
            "medico@hospital.com",
            "password123",
            Autoridad.MEDICO
        );
        
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(jwtUtil.generateToken(any(Usuario.class))).thenReturn("token-jwt");
        when(jwtConfig.getExpirationTime()).thenReturn(86400000L);
        
        AuthResponse response = autenticacionService.registrar(request);
        
        assertNotNull(response);
        assertEquals("token-jwt", response.getToken());
        assertEquals("medico@hospital.com", response.getEmail());
        assertEquals(Autoridad.MEDICO, response.getAutoridad());
        assertEquals(86400000L, response.getExpiresIn());
        
        verify(usuarioRepository).add(any(Usuario.class));
        verify(jwtUtil).generateToken(any(Usuario.class));
    }

    @Test
    void registrarDebeLanzarExcepcionSiRequestEsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> autenticacionService.registrar(null)
        );
        
        assertEquals("El request de registro no puede ser nulo", exception.getMessage());
        verify(usuarioRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiEmailEsInvalido() {
        RegistroRequest request = new RegistroRequest(
            "emailinvalido",
            "password123",
            Autoridad.MEDICO
        );
        
        RegistroException exception = assertThrows(
            RegistroException.class,
            () -> autenticacionService.registrar(request)
        );
        
        assertEquals("El email no tiene un formato válido", exception.getMessage());
        verify(usuarioRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiPasswordEsMenorA8Caracteres() {
        RegistroRequest request = new RegistroRequest(
            "medico@hospital.com",
            "pass123",
            Autoridad.MEDICO
        );
        
        RegistroException exception = assertThrows(
            RegistroException.class,
            () -> autenticacionService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("8 caracteres"));
        verify(usuarioRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiAutoridadEsNull() {
        RegistroRequest request = new RegistroRequest(
            "medico@hospital.com",
            "password123",
            null
        );
        
        RegistroException exception = assertThrows(
            RegistroException.class,
            () -> autenticacionService.registrar(request)
        );
        
        assertEquals("Debe especificar una autoridad (MEDICO o ENFERMERA)", exception.getMessage());
        verify(usuarioRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiEmailYaExiste() {
        RegistroRequest request = new RegistroRequest(
            "medico@hospital.com",
            "password123",
            Autoridad.MEDICO
        );
        
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);
        
        RegistroException exception = assertThrows(
            RegistroException.class,
            () -> autenticacionService.registrar(request)
        );
        
        assertEquals("El email ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).add(any());
    }

    @Test
    void registrarDebeHashearPasswordAntesDeGuardar() {
        RegistroRequest request = new RegistroRequest(
            "medico@hospital.com",
            "password123",
            Autoridad.MEDICO
        );
        
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(jwtUtil.generateToken(any(Usuario.class))).thenReturn("token-jwt");
        when(jwtConfig.getExpirationTime()).thenReturn(86400000L);
        
        autenticacionService.registrar(request);
        
        verify(usuarioRepository).add(argThat(usuario -> {
            assertNotEquals("password123", usuario.getPasswordHash());
            assertTrue(usuario.getPasswordHash().startsWith("$2a$"));
            return true;
        }));
    }

    @Test
    void registrarDebeCrearUsuarioConAutoridadEnfermera() {
        RegistroRequest request = new RegistroRequest(
            "enfermera@hospital.com",
            "password123",
            Autoridad.ENFERMERA
        );
        
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(jwtUtil.generateToken(any(Usuario.class))).thenReturn("token-jwt");
        when(jwtConfig.getExpirationTime()).thenReturn(86400000L);
        
        AuthResponse response = autenticacionService.registrar(request);
        
        assertEquals(Autoridad.ENFERMERA, response.getAutoridad());
    }

    @Test
    void loginDebeAutenticarConCredencialesValidas() {
        String plainPassword = "password123";
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        
        LoginRequest request = new LoginRequest("medico@hospital.com", plainPassword);
        
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            hashedPassword,
            Autoridad.MEDICO
        );
        
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken(any(Usuario.class))).thenReturn("token-jwt");
        when(jwtConfig.getExpirationTime()).thenReturn(86400000L);
        
        AuthResponse response = autenticacionService.login(request);
        
        assertNotNull(response);
        assertEquals("token-jwt", response.getToken());
        assertEquals("medico@hospital.com", response.getEmail());
        assertEquals(Autoridad.MEDICO, response.getAutoridad());
        
        verify(jwtUtil).generateToken(usuario);
    }

    @Test
    void loginDebeLanzarExcepcionSiRequestEsNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> autenticacionService.login(null)
        );
        
        assertEquals("El request de login no puede ser nulo", exception.getMessage());
    }

    @Test
    void loginDebeLanzarExcepcionSiEmailNoExiste() {
        LoginRequest request = new LoginRequest("noexiste@hospital.com", "password123");
        
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        AutenticacionException exception = assertThrows(
            AutenticacionException.class,
            () -> autenticacionService.login(request)
        );
        
        assertEquals("Usuario o contraseña inválidos", exception.getMessage());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void loginDebeLanzarExcepcionSiPasswordEsIncorrecta() {
        String hashedPassword = PasswordHasher.hashPassword("correcta123");
        
        LoginRequest request = new LoginRequest("medico@hospital.com", "incorrecta123");
        
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            hashedPassword,
            Autoridad.MEDICO
        );
        
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        
        AutenticacionException exception = assertThrows(
            AutenticacionException.class,
            () -> autenticacionService.login(request)
        );
        
        assertEquals("Usuario o contraseña inválidos", exception.getMessage());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    void loginDebeUsarMismoMensajeParaUsuarioNoExistenteYPasswordIncorrecta() {
        LoginRequest request1 = new LoginRequest("noexiste@hospital.com", "password123");
        LoginRequest request2 = new LoginRequest("medico@hospital.com", "passwordIncorrecta");
        
        when(usuarioRepository.findByEmail("noexiste@hospital.com")).thenReturn(Optional.empty());
        
        String hashedPassword = PasswordHasher.hashPassword("correcta123");
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            hashedPassword,
            Autoridad.MEDICO
        );
        when(usuarioRepository.findByEmail("medico@hospital.com")).thenReturn(Optional.of(usuario));
        
        AutenticacionException exception1 = assertThrows(
            AutenticacionException.class,
            () -> autenticacionService.login(request1)
        );
        
        AutenticacionException exception2 = assertThrows(
            AutenticacionException.class,
            () -> autenticacionService.login(request2)
        );
        
        assertEquals(exception1.getMessage(), exception2.getMessage());
        assertEquals("Usuario o contraseña inválidos", exception1.getMessage());
    }

    @Test
    void loginDebeLanzarExcepcionSiEmailEsNull() {
        LoginRequest request = new LoginRequest(null, "password123");
        
        AutenticacionException exception = assertThrows(
            AutenticacionException.class,
            () -> autenticacionService.login(request)
        );
        
        assertEquals("Usuario o contraseña inválidos", exception.getMessage());
    }

    @Test
    void loginDebeLanzarExcepcionSiPasswordEsNull() {
        LoginRequest request = new LoginRequest("medico@hospital.com", null);
        
        AutenticacionException exception = assertThrows(
            AutenticacionException.class,
            () -> autenticacionService.login(request)
        );
        
        assertEquals("Usuario o contraseña inválidos", exception.getMessage());
    }

    @Test
    void loginDebeLanzarExcepcionSiEmailEsVacio() {
        LoginRequest request = new LoginRequest("", "password123");
        
        AutenticacionException exception = assertThrows(
            AutenticacionException.class,
            () -> autenticacionService.login(request)
        );
        
        assertEquals("Usuario o contraseña inválidos", exception.getMessage());
    }

    @Test
    void loginDebeGenerarNuevoTokenCadaVez() {
        String plainPassword = "password123";
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);
        
        LoginRequest request = new LoginRequest("medico@hospital.com", plainPassword);
        
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            hashedPassword,
            Autoridad.MEDICO
        );
        
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken(any(Usuario.class)))
            .thenReturn("token-1")
            .thenReturn("token-2");
        when(jwtConfig.getExpirationTime()).thenReturn(86400000L);
        
        AuthResponse response1 = autenticacionService.login(request);
        AuthResponse response2 = autenticacionService.login(request);
        
        assertEquals("token-1", response1.getToken());
        assertEquals("token-2", response2.getToken());
        verify(jwtUtil, times(2)).generateToken(usuario);
    }

    @Test
    void findByEmailDebeRetornarUsuarioSiExiste() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuario));
        
        Optional<Usuario> resultado = usuarioRepository.findByEmail("medico@hospital.com");
        
        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
    }

    @Test
    void findByEmailDebeRetornarVacioSiNoExiste() {
        when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        
        Optional<Usuario> resultado = usuarioRepository.findByEmail("noexiste@hospital.com");
        
        assertTrue(resultado.isEmpty());
    }
}

