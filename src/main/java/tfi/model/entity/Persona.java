package tfi.model.entity;

import tfi.model.valueObjects.Cuil;
import tfi.model.valueObjects.Email;

public class Persona {
    private Cuil cuil;
    private String nombre;
    private String apellido;
    private Email email = null;

    public Persona(String cuil) {
        this.cuil = new Cuil(cuil);
    }
    
    public Persona(String cuil, String nombre, String apellido) {
        this.cuil = new Cuil(cuil);
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Persona(String cuil, String nombre, String apellido, String email) {
        this.cuil = new Cuil(cuil);
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email != null ? Email.from(email) : null;
    }

    // Métodos para obtener los Value Objects
    public Cuil getCuilVO() {
        return cuil;
    }

    public Email getEmailVO() {
        return email;
    }

    // Métodos de compatibilidad que retornan String
    public String getCuil() {
        return cuil != null ? cuil.getValor() : null;
    }

    public void setCuil(String cuil) {
        this.cuil = new Cuil(cuil);
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

    public String getEmail() {
        return email != null ? email.getValue() : null;
    }

    public void setEmail(String email) {
        this.email = email != null ? Email.from(email) : null;
    }
}
