package model;

public class Doctor extends Persona{
    private String matricula;

    public Doctor(String cuil, String matricula) {
        super(cuil);
        this.matricula = matricula;
    }

    public Doctor(String cuil, String nombre, String apellido, String email, String matricula) {
        super(cuil, nombre, apellido, email);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }
}
