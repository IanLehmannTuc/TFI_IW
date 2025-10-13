package model;

public class ObraSocial {
    private int idObraSocial;
    private String nombreObraSocial;

    public ObraSocial(int idObraSocial, String nombreObraSocial) {
        this.idObraSocial = idObraSocial;
        this.nombreObraSocial = nombreObraSocial;
    }

    public int getIdObraSocial() {
        return idObraSocial;
    }

    public void setIdObraSocial(int idObraSocial) {
        this.idObraSocial = idObraSocial;
    }

    public String getNombreObraSocial() {
        return nombreObraSocial;
    }

    public void setNombreObraSocial(String nombreObraSocial) {
        this.nombreObraSocial = nombreObraSocial;
    }
}
