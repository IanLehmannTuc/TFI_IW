package tfi.model.dto;

/**
 * DTO para la solicitud de inicio de sesión.
 * Contiene las credenciales del usuario.
 */
public class LoginRequest {
    private String email;
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

