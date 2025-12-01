package tfi.application.dto;

import tfi.domain.enums.Autoridad;

/**
 * DTO para la respuesta de registro de un nuevo usuario.
 * Contiene la información básica del usuario creado sin incluir el token JWT.
 */
public class RegistroResponse {
    private String id;
    private String nombre;
    private String apellido;
    private Autoridad autoridad;

    /**
     * Constructor por defecto necesario para serialización JSON
     */
    public RegistroResponse() {
    }

    /**
     * Constructor completo
     * 
     * @param id ID del usuario creado
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param autoridad Rol/autoridad del usuario
     */
    public RegistroResponse(String id, String nombre, String apellido, Autoridad autoridad) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.autoridad = autoridad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Autoridad getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(Autoridad autoridad) {
        this.autoridad = autoridad;
    }

    @Override
    public String toString() {
        return "RegistroResponse{" +
                "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", autoridad=" + autoridad +
                '}';
    }
}

