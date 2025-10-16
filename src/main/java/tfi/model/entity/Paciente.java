package tfi.model.entity;

import tfi.model.valueObjects.Domicilio;

public class Paciente extends Persona {
    private Afiliado obraSocial;
    private Domicilio domicilio;

    public Paciente(String cuil, Domicilio domicilio, Afiliado obraSocial) {
        super(cuil);
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
    }

    public Paciente(String cuil, String nombre, String apellido) {
        super(cuil, nombre, apellido);
    }

    public Paciente(String cuil, String nombre, String apellido, String email, Domicilio domicilio, Afiliado obraSocial) {
        super(cuil, nombre, apellido, email);
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
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
