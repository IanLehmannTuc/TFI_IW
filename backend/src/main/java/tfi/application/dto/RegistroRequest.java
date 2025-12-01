package tfi.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tfi.domain.enums.Autoridad;

/**
 * DTO para la solicitud de registro de un nuevo usuario.
 * Contiene los datos necesarios para crear una cuenta.
 * 
 * NOTA: La validación de formato de email y longitud de password se realiza
 * en los Value Objects (Email y Password) del dominio, que son la fuente de verdad.
 * Aquí solo validamos que los campos no sean nulos/vacíos para fallar rápido.
 * 
 * DATOS PROFESIONALES: El registro ahora incluye opcionalmente los datos profesionales
 * (CUIL, nombre, apellido, matrícula) para crear automáticamente el perfil de Enfermero/Doctor.
 */
public class RegistroRequest {
    
    @NotBlank(message = "El email es obligatorio")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
    
    @NotNull(message = "La autoridad es obligatoria")
    private Autoridad autoridad;
    
    @NotBlank(message = "El CUIL es obligatorio")
    private String cuil;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;

    /**
     * Constructor por defecto necesario para deserialización JSON
     */
    public RegistroRequest() {
    }

    
    /**
     * Constructor completo con datos profesionales
     */
    public RegistroRequest(String email, String password, Autoridad autoridad, 
                          String cuil, String nombre, String apellido, String matricula) {
        this.email = email;
        this.password = password;
        this.autoridad = autoridad;
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
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

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    @Override
    public String toString() {
        return "RegistroRequest{" +
                "email='" + email + '\'' +
                ", autoridad=" + autoridad +
                ", cuil='" + cuil + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", matricula='" + matricula + '\'' +
                '}';
    }
}

