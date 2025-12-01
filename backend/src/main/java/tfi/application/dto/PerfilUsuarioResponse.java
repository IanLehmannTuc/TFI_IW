package tfi.application.dto;

import tfi.domain.enums.Autoridad;

/**
 * DTO para la respuesta del perfil completo del usuario autenticado.
 * Contiene todos los datos del usuario logueado.
 */
public class PerfilUsuarioResponse {
    private String id;
    private String email;
    private String nombre;
    private String apellido;
    private String cuil;
    private String matricula;
    private Autoridad autoridad;

    /**
     * Constructor por defecto necesario para serialización JSON
     */
    public PerfilUsuarioResponse() {
    }

    /**
     * Constructor completo
     * 
     * @param id ID del usuario
     * @param email Email del usuario
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param cuil CUIL del usuario
     * @param matricula Matrícula profesional del usuario
     * @param autoridad Rol/autoridad del usuario
     */
    public PerfilUsuarioResponse(String id, String email, String nombre, String apellido, 
                                String cuil, String matricula, Autoridad autoridad) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cuil = cuil;
        this.matricula = matricula;
        this.autoridad = autoridad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public Autoridad getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(Autoridad autoridad) {
        this.autoridad = autoridad;
    }

    @Override
    public String toString() {
        return "PerfilUsuarioResponse{" +
                "id='" + id + '\'' +
                ", email='" + email + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", cuil='" + cuil + '\'' +
                ", matricula='" + matricula + '\'' +
                ", autoridad=" + autoridad +
                '}';
    }
}

