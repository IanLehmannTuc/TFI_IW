package model;

public class Afiliado {
    private ObraSocial obraSocial;
    private String numeroAfiliado;

    public Afiliado(ObraSocial obraSocial, String numeroAfiliado) {
        this.obraSocial = obraSocial;
        this.numeroAfiliado = numeroAfiliado;
    }

    public ObraSocial getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(ObraSocial obraSocial) {
        this.obraSocial = obraSocial;
    }

    public String getNumeroAfiliado() {
        return numeroAfiliado;
    }

    public void setNumeroAfiliado(String numeroAfiliado) {
        this.numeroAfiliado = numeroAfiliado;
    }
}
