package tfi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tfi.model.enums.Autoridad;

/**
 * DTO para la solicitud de registro de un nuevo usuario.
 * Contiene los datos necesarios para crear una cuenta.
 * 
 * NOTA: La validación de formato de email y longitud de password se realiza
 * en los Value Objects (Email y Password) del dominio, que son la fuente de verdad.
 * Aquí solo validamos que los campos no sean nulos/vacíos para fallar rápido.
 */
public class RegistroRequest {
    
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    @NotNull(message = "La autoridad es obligatoria")
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

