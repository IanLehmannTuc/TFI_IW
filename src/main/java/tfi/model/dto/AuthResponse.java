package tfi.model.dto;

import tfi.model.enums.Autoridad;

/**
 * DTO para la respuesta de autenticación exitosa.
 * Contiene el token JWT y la información del usuario autenticado.
 */
public class AuthResponse {
    private String token;
    private String email;
    private Autoridad autoridad;
    private Long expiresIn;

    /**
     * Constructor por defecto necesario para serialización JSON
     */
    public AuthResponse() {
    }

    /**
     * Constructor completo
     * 
     * @param token JWT token generado
     * @param email Email del usuario autenticado
     * @param autoridad Rol/autoridad del usuario
     * @param expiresIn Tiempo de expiración del token en milisegundos
     */
    public AuthResponse(String token, String email, Autoridad autoridad, Long expiresIn) {
        this.token = token;
        this.email = email;
        this.autoridad = autoridad;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Autoridad getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(Autoridad autoridad) {
        this.autoridad = autoridad;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "email='" + email + '\'' +
                ", autoridad=" + autoridad +
                ", expiresIn=" + expiresIn +
                '}';
    }
}

