package model;

public class Atencion {
    private Doctor doctor;
    private String informe;

    public Atencion(Doctor doctor, String informe) {
        this.doctor = doctor;
        this.informe = informe;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }
}
