package tfi.model.entity;

import tfi.model.enums.NivelEmergencia;
import tfi.model.valueObjects.Temperatura;
import tfi.model.valueObjects.TensionArterial;
import tfi.model.valueObjects.FrecuenciaCardiaca;
import tfi.model.valueObjects.FrecuenciaRespiratoria;
import java.time.LocalDateTime;

public class Ingreso {
    private String id;
    private Atencion atencion;
    private Paciente paciente;
    private Enfermero enfermero;
    private String descripcion;
    private LocalDateTime fechaHoraIngreso;
    private Temperatura temperatura;
    private TensionArterial tensionArterial;
    private FrecuenciaCardiaca frecuenciaCardiaca;
    private FrecuenciaRespiratoria frecuenciaRespiratoria;
    private NivelEmergencia nivelEmergencia;

    public Ingreso(Atencion atencion, Paciente paciente, Enfermero enfermero, String descripcion, LocalDateTime fechaHoraIngreso,
                   Temperatura temperatura, TensionArterial tensionArterial, FrecuenciaCardiaca frecuenciaCardiaca,
                   FrecuenciaRespiratoria frecuenciaRespiratoria, NivelEmergencia nivelEmergencia) {
        this.atencion = atencion;
        this.paciente = paciente;
        this.enfermero = enfermero;
        this.descripcion = descripcion;
        this.fechaHoraIngreso = fechaHoraIngreso;
        this.temperatura = temperatura;
        this.tensionArterial = tensionArterial;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.nivelEmergencia = nivelEmergencia;
    }

    public Ingreso(
        Paciente paciente, 
        Enfermero enfermero, 
        String descripcion, 
        Temperatura temperatura, 
        TensionArterial tensionArterial, 
        FrecuenciaCardiaca frecuenciaCardiaca, 
        FrecuenciaRespiratoria frecuenciaRespiratoria, 
        NivelEmergencia nivelEmergencia) {

        this.atencion = null;
        this.paciente = paciente;
        this.enfermero = enfermero;
        this.descripcion = descripcion;
        this.fechaHoraIngreso = LocalDateTime.now();
        this.temperatura = temperatura;
        this.tensionArterial = tensionArterial;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.nivelEmergencia = nivelEmergencia;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public void setAtencion(Atencion atencion) {
        this.atencion = atencion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Enfermero getEnfermero() {
        return enfermero;
    }

    public void setEnfermero(Enfermero enfermero) {
        this.enfermero = enfermero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }

    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }

    public Temperatura getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Temperatura temperatura) {
        this.temperatura = temperatura;
    }

    public TensionArterial getTensionArterial() {
        return tensionArterial;
    }

    public void setTensionArterial(TensionArterial tensionArterial) {
        this.tensionArterial = tensionArterial;
    }

    public FrecuenciaCardiaca getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(FrecuenciaCardiaca frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public FrecuenciaRespiratoria getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(FrecuenciaRespiratoria frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public NivelEmergencia getNivelEmergencia() {
        return nivelEmergencia;
    }

    public void setNivelEmergencia(NivelEmergencia nivelEmergencia) {
        this.nivelEmergencia = nivelEmergencia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
