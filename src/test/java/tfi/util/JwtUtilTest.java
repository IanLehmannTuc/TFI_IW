package tfi.util;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tfi.config.JwtConfig;
import tfi.domain.entity.Usuario;
import tfi.domain.enums.Autoridad;
import tfi.domain.valueObject.Email;

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
        jwtConfig.setExpirationTime(3600000L);
        
        jwtUtil = new JwtUtil(jwtConfig);
    }

    @Test
    void debeGenerarTokenValido() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        String token = jwtUtil.generateToken(usuario);
        
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void debeExtraerEmailCorrectamenteDelToken() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        String email = jwtUtil.getEmailFromToken(token);
        
        assertEquals("medico@hospital.com", email);
    }

    @Test
    void debeExtraerAutoridadCorrectamenteDelToken() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);
        
        assertEquals(Autoridad.MEDICO, autoridad);
    }

    @Test
    void debeGenerarTokenConAutoridadEnfermera() {
        Usuario usuario = new Usuario(
            Email.from("enfermera@hospital.com"),
            "hash",
            Autoridad.ENFERMERO
        );
        String token = jwtUtil.generateToken(usuario);
        
        Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);
        
        assertEquals(Autoridad.ENFERMERO, autoridad);
    }

    @Test
    void debeValidarTokenCorrectamente() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        boolean valido = jwtUtil.validateToken(token);
        
        assertTrue(valido);
    }

    @Test
    void debeRechazarTokenInvalido() {
        String tokenInvalido = "token.invalido.aqui";
        
        boolean valido = jwtUtil.validateToken(tokenInvalido);
        
        assertFalse(valido);
    }

    @Test
    void debeRechazarTokenManipulado() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        String tokenManipulado = token.substring(0, token.length() - 5) + "XXXXX";
        
        boolean valido = jwtUtil.validateToken(tokenManipulado);
        
        assertFalse(valido);
    }

    @Test
    void debeRechazarTokenVacio() {
        boolean valido = jwtUtil.validateToken("");
        
        assertFalse(valido);
    }

    @Test
    void debeRechazarTokenNull() {
        boolean valido = jwtUtil.validateToken(null);
        
        assertFalse(valido);
    }

    @Test
    void debeObtenerFechaDeExpiracion() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        Date expiration = jwtUtil.getExpirationFromToken(token);
        
        assertNotNull(expiration);
        assertTrue(expiration.after(new Date()));
    }

    @Test
    void tokenNoDebeEstarExpiradoRecienGenerado() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtil.generateToken(usuario);
        
        boolean expirado = jwtUtil.isTokenExpired(token);
        
        assertFalse(expirado);
    }

    @Test
    void debeDetectarTokenExpirado() {
        jwtConfig.setExpirationTime(1L);
        JwtUtil jwtUtilCorto = new JwtUtil(jwtConfig);
        
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        String token = jwtUtilCorto.generateToken(usuario);
        
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            fail("Test interrumpido");
        }
        
        boolean expirado = jwtUtilCorto.isTokenExpired(token);
        
        assertTrue(expirado);
    }

    @Test
    void dosTokensDelMismoUsuarioDebenSerDiferentes() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        String token1 = jwtUtil.generateToken(usuario);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail("Test interrumpido");
        }
        String token2 = jwtUtil.generateToken(usuario);
        
        assertNotEquals(token1, token2);
    }

    @Test
    void getEmailFromTokenDebeLanzarExcepcionParaTokenInvalido() {
        String tokenInvalido = "token.invalido.aqui";
        
        assertThrows(
            JwtException.class,
            () -> jwtUtil.getEmailFromToken(tokenInvalido)
        );
    }

    @Test
    void getAutoridadFromTokenDebeLanzarExcepcionParaTokenInvalido() {
        String tokenInvalido = "token.invalido.aqui";
        
        assertThrows(
            JwtException.class,
            () -> jwtUtil.getAutoridadFromToken(tokenInvalido)
        );
    }

    @Test
    void tokenDebeContenerTodaLaInformacionDelUsuario() {
        Usuario usuario = new Usuario(
            Email.from("enfermera@hospital.com"),
            "hash",
            Autoridad.ENFERMERO
        );
        
        String token = jwtUtil.generateToken(usuario);
        String email = jwtUtil.getEmailFromToken(token);
        Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);
        
        assertEquals("enfermera@hospital.com", email);
        assertEquals(Autoridad.ENFERMERO, autoridad);
    }
}

