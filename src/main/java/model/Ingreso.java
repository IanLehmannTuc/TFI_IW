package model;

import java.time.LocalDate;

public class Ingreso {
    private Atencion atencion;
    private Paciente paciente;
    private Enfermero enfermero;
    private String descripcion;
    private LocalDate fechaIngreso;
    private Temperatura temperatura;
    private TensionArterial tensionArterial;
    private FrecuenciaCardiaca frecuenciaCardiaca;
    private FrecuenciaRespiratoria frecuenciaRespiratoria;

    public Ingreso() {

    }

}
