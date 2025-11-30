package tfi.domain.entity;

import tfi.domain.valueObject.Cuil;
import tfi.domain.valueObject.Domicilio;
import tfi.domain.valueObject.Email;

public class Paciente {
    private Cuil cuil;
    private String nombre;
    private String apellido;
    private Email email;
    private Afiliado obraSocial;
    private Domicilio domicilio;

    public Paciente(String cuil) {
        this.cuil = new Cuil(cuil);
    }

    public Paciente(String cuil, Domicilio domicilio, Afiliado obraSocial) {
        this.cuil = new Cuil(cuil);
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
    }

    public Paciente(String cuil, String nombre, String apellido) {
        this.cuil = new Cuil(cuil);
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public Paciente(String cuil, String nombre, String apellido, String email, Domicilio domicilio, Afiliado obraSocial) {
        this.cuil = new Cuil(cuil);
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email != null ? Email.from(email) : null;
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
    }

    public Cuil getCuilVO() {
        return cuil;
    }

    public Email getEmailVO() {
        return email;
    }

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

    public Afiliado getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(Afiliado obraSocial) {
        this.obraSocial = obraSocial;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }
}
