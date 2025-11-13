package tfi.util;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tfi.config.JwtConfig;
import tfi.model.entity.Usuario;
import tfi.model.enums.Autoridad;
import tfi.model.valueObjects.Email;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para JwtUtil.
 * Verifica generación, validación y extracción de información de tokens JWT.
 */
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private JwtConfig jwtConfig;

    @BeforeEach
    void setUp() {
        jwtConfig = new JwtConfig();
        jwtConfig.setSecretKey("clave-secreta-para-testing-debe-ser-larga-al-menos-256-bits");
        jwtConfig.setExpirationTime(3600000L); // 1 hora
        
        jwtUtil = new JwtUtil(jwtConfig);
    }

    @Test
    void debeGenerarTokenValido() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        // Act
        String token = jwtUtil.generateToken(usuario);
        
        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3); // JWT tiene 3 partes
    }

    @Test
    void debeExtraerEmailCorrectamenteDelToken() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        // Act
        String email = jwtUtil.getEmailFromToken(token);
        
        // Assert
        assertEquals("medico@hospital.com", email);
    }

    @Test
    void debeExtraerAutoridadCorrectamenteDelToken() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        // Act
        Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);
        
        // Assert
        assertEquals(Autoridad.MEDICO, autoridad);
    }

    @Test
    void debeGenerarTokenConAutoridadEnfermera() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("enfermera@hospital.com"),
            "hash",
            Autoridad.ENFERMERA
        );
        String token = jwtUtil.generateToken(usuario);
        
        // Act
        Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);
        
        // Assert
        assertEquals(Autoridad.ENFERMERA, autoridad);
    }

    @Test
    void debeValidarTokenCorrectamente() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        // Act
        boolean valido = jwtUtil.validateToken(token);
        
        // Assert
        assertTrue(valido);
    }

    @Test
    void debeRechazarTokenInvalido() {
        // Arrange
        String tokenInvalido = "token.invalido.aqui";
        
        // Act
        boolean valido = jwtUtil.validateToken(tokenInvalido);
        
        // Assert
        assertFalse(valido);
    }

    @Test
    void debeRechazarTokenManipulado() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        String tokenManipulado = token.substring(0, token.length() - 5) + "XXXXX";
        
        // Act
        boolean valido = jwtUtil.validateToken(tokenManipulado);
        
        // Assert
        assertFalse(valido);
    }

    @Test
    void debeRechazarTokenVacio() {
        // Act
        boolean valido = jwtUtil.validateToken("");
        
        // Assert
        assertFalse(valido);
    }

    @Test
    void debeRechazarTokenNull() {
        // Act
        boolean valido = jwtUtil.validateToken(null);
        
        // Assert
        assertFalse(valido);
    }

    @Test
    void debeObtenerFechaDeExpiracion() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        // Act
        Date expiration = jwtUtil.getExpirationFromToken(token);
        
        // Assert
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date())); // Debe ser fecha futura
    }

    @Test
    void tokenNoDebeEstarExpiradoRecienGenerado() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        // Act
        boolean expirado = jwtUtil.isTokenExpired(token);
        
        // Assert
        assertFalse(expirado);
    }

    @Test
    void debeDetectarTokenExpirado() {
        // Arrange
        jwtConfig.setExpirationTime(1L); // 1 milisegundo
        JwtUtil jwtUtilCorto = new JwtUtil(jwtConfig);
        
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtilCorto.generateToken(usuario);
        
        // Act
        try {
            Thread.sleep(10); // Esperar a que expire
        } catch (InterruptedException e) {
            fail("Test interrumpido");
        }
        
        boolean expirado = jwtUtilCorto.isTokenExpired(token);
        
        // Assert
        assertTrue(expirado);
    }

    @Test
    void dosTokensDelMismoUsuarioDebenSerDiferentes() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        // Act
        String token1 = jwtUtil.generateToken(usuario);
        try {
            Thread.sleep(1000); // Esperar 1 segundo para que el timestamp cambie
        } catch (InterruptedException e) {
            fail("Test interrumpido");
        }
        String token2 = jwtUtil.generateToken(usuario);
        
        // Assert
        assertNotEquals(token1, token2); // Diferentes por timestamp (iat)
    }

    @Test
    void getEmailFromTokenDebeLanzarExcepcionParaTokenInvalido() {
        // Arrange
        String tokenInvalido = "token.invalido.aqui";
        
        // Act & Assert
        assertThrows(
            JwtException.class,
            () -> jwtUtil.getEmailFromToken(tokenInvalido)
        );
    }

    @Test
    void getAutoridadFromTokenDebeLanzarExcepcionParaTokenInvalido() {
        // Arrange
        String tokenInvalido = "token.invalido.aqui";
        
        // Act & Assert
        assertThrows(
            JwtException.class,
            () -> jwtUtil.getAutoridadFromToken(tokenInvalido)
        );
    }

    @Test
    void tokenDebeContenerTodaLaInformacionDelUsuario() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("enfermera@hospital.com"),
            "hash",
            Autoridad.ENFERMERA
        );
        
        // Act
        String token = jwtUtil.generateToken(usuario);
        String email = jwtUtil.getEmailFromToken(token);
        Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);
        
        // Assert
        assertEquals("enfermera@hospital.com", email);
        assertEquals(Autoridad.ENFERMERA, autoridad);
    }
}

