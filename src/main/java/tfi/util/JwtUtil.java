package tfi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import tfi.config.JwtConfig;
import tfi.model.entity.Usuario;
import tfi.model.enums.Autoridad;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Utilidad para generar, validar y extraer información de tokens JWT.
 * Utiliza la librería JJWT para manejar tokens de forma segura.
 */
@Component
public class JwtUtil {
    
    private final JwtConfig jwtConfig;
    private final SecretKey key;

    /**
     * Constructor que inicializa la clave de firma desde la configuración.
     * 
     * @param jwtConfig Configuración de JWT
     */
    public JwtUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        // Crear una clave segura HMAC-SHA desde el string de configuración
        this.key = Keys.hmacShaKeyFor(
            jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * Genera un JWT token para un usuario autenticado.
     * El token contiene el email como subject y la autoridad como claim.
     * 
     * @param usuario El usuario para el cual generar el token
     * @return El token JWT firmado
     */
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpirationTime());
        
        return Jwts.builder()
                .subject(usuario.getEmail().getValue())             // Subject: email del usuario (extraer String del VO)
                .claim("autoridad", usuario.getAutoridad().name())  // Claim custom: rol
                .issuedAt(now)                                      // Fecha de emisión
                .expiration(expiryDate)                             // Fecha de expiración
                .signWith(key)                                      // Firma con clave secreta
                .compact();
    }

    /**
     * Extrae el email (subject) del token JWT.
     * 
     * @param token El token JWT
     * @return El email del usuario
     * @throws JwtException Si el token es inválido o expirado
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * Extrae la autoridad del token JWT.
     * 
     * @param token El token JWT
     * @return La autoridad del usuario
     * @throws JwtException Si el token es inválido o expirado
     */
    public Autoridad getAutoridadFromToken(String token) {
        Claims claims = parseToken(token);
        String autoridad = claims.get("autoridad", String.class);
        return Autoridad.valueOf(autoridad);
    }

    /**
     * Valida si un token JWT es válido y no ha expirado.
     * 
     * @param token El token JWT a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token inválido, expirado, o manipulado
            return false;
        }
    }

    /**
     * Parsea un token JWT y extrae sus claims.
     * 
     * @param token El token JWT
     * @return Los claims del token
     * @throws JwtException Si el token es inválido, expirado o manipulado
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Obtiene la fecha de expiración del token.
     * 
     * @param token El token JWT
     * @return La fecha de expiración
     * @throws JwtException Si el token es inválido
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }

    /**
     * Verifica si un token ha expirado.
     * 
     * @param token El token JWT
     * @return true si el token ha expirado
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}

