package tfi.domain.entity;

import tfi.domain.enums.NivelEmergencia;
import tfi.domain.enums.Estado;
import tfi.domain.valueObject.Temperatura;
import tfi.domain.valueObject.TensionArterial;
import tfi.domain.valueObject.FrecuenciaCardiaca;
import tfi.domain.valueObject.FrecuenciaRespiratoria;
import java.time.LocalDateTime;

public class Ingreso {
    private String id;
    private Atencion atencion;
    private Paciente paciente;
    private Usuario enfermero;
    private String descripcion;
    private LocalDateTime fechaHoraIngreso;
    private Temperatura temperatura;
    private TensionArterial tensionArterial;
    private FrecuenciaCardiaca frecuenciaCardiaca;
    private FrecuenciaRespiratoria frecuenciaRespiratoria;
    private NivelEmergencia nivelEmergencia;
    private Estado estado;

    public Ingreso(Atencion atencion, Paciente paciente, Usuario enfermero, String descripcion, LocalDateTime fechaHoraIngreso,
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
        this.estado = Estado.PENDIENTE;
    }

    public Ingreso(
        Paciente paciente, 
        Usuario enfermero, 
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
        this.estado = Estado.PENDIENTE;
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

    public Usuario getEnfermero() {
        return enfermero;
    }

    public void setEnfermero(Usuario enfermero) {
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

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }
}
