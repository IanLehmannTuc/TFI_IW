package tfi.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para la solicitud de inicio de sesión.
 * Contiene las credenciales del usuario.
 * 
 * NOTA: La validación de formato de email se realiza en el Value Object Email
 * del dominio, que es la fuente de verdad. Aquí solo validamos que no sea vacío
 * para fallar rápido.
 */
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /**
     * Constructor por defecto necesario para deserialización JSON
     */
    public LoginRequest() {
    }

    /**
     * Constructor completo
     * 
     * @param email Email del usuario
     * @param password Contraseña en texto plano
     */
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "email='" + email + '\'' +
                '}';
    }
}

