package tfi.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para JWT (JSON Web Tokens).
 * Lee las propiedades desde application.properties.
 */
@Configuration
public class JwtConfig {

    private static final int MIN_SECRET_LENGTH = 32; 

    @Value("${jwt.secret:mi-clave-secreta-super-segura-que-debe-ser-muy-larga-y-compleja-para-jwt-authentication-module-2024}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private Long expirationTime;

    /**
     * Valida que la clave secreta tenga una longitud mínima segura.
     * Se ejecuta después de la inyección de dependencias.
     */
    @PostConstruct
    public void validateSecretKey() {
        if (secretKey == null || secretKey.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException(
                "JWT secret key debe tener al menos " + MIN_SECRET_LENGTH + 
                " caracteres para ser seguro. Longitud actual: " + 
                (secretKey != null ? secretKey.length() : 0)
            );
        }
    }

    /**
     * Obtiene la clave secreta para firmar tokens JWT.
     * 
     * @return La clave secreta
     */
    public String getSecretKey() {
        return secretKey;
    }

    /**
     * Obtiene el tiempo de expiración de tokens en milisegundos.
     * Por defecto: 86400000ms = 24 horas
     * 
     * @return El tiempo de expiración en milisegundos
     */
    public Long getExpirationTime() {
        return expirationTime;
    }

    /**
     * Configura la clave secreta (útil para testing)
     * 
     * @param secretKey La nueva clave secreta
     */
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    /**
     * Configura el tiempo de expiración (útil para testing)
     * 
     * @param expirationTime El nuevo tiempo de expiración
     */
    public void setExpirationTime(Long expirationTime) {
        this.expirationTime = expirationTime;
    }
}

