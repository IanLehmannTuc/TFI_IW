package tfi.domain.entity;

public class Atencion {
    private Usuario medico;
    private String informe;

    public Atencion(Usuario medico, String informe) {
        this.medico = medico;
        this.informe = informe;
    }

    public Usuario getMedico() {
        return medico;
    }

    public void setMedico(Usuario medico) {
        this.medico = medico;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }
}
