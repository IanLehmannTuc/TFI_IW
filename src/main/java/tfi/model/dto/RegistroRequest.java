package tfi.model.dto;

import tfi.model.enums.Autoridad;

/**
 * DTO para la solicitud de registro de un nuevo usuario.
 * Contiene los datos necesarios para crear una cuenta.
 */
public class RegistroRequest {
    private String email;
    private String password;
    private Autoridad autoridad;

    /**
     * Constructor por defecto necesario para deserialización JSON
     */
    public RegistroRequest() {
    }

    /**
     * Constructor completo
     * 
     * @param email Email del nuevo usuario
     * @param password Contraseña en texto plano (será hasheada)
     * @param autoridad Rol/autoridad del usuario
     */
    public RegistroRequest(String email, String password, Autoridad autoridad) {
        this.email = email;
        this.password = password;
        this.autoridad = autoridad;
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

    public Autoridad getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(Autoridad autoridad) {
        this.autoridad = autoridad;
    }

    @Override
    public String toString() {
        return "RegistroRequest{" +
                "email='" + email + '\'' +
                ", autoridad=" + autoridad +
                '}';
    }
}

