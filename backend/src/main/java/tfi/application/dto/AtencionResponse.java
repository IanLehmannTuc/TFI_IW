package tfi.application.dto;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para una atención médica.
 * Contiene todos los datos de una atención registrada.
 */
public class AtencionResponse {

    private String id;
    private String ingresoId;
    private String medicoId;
    private String informeMedico;
    private LocalDateTime fechaAtencion;

    public AtencionResponse() {
    }

    public AtencionResponse(String id, String ingresoId, String medicoId, String informeMedico, LocalDateTime fechaAtencion) {
        this.id = id;
        this.ingresoId = ingresoId;
        this.medicoId = medicoId;
        this.informeMedico = informeMedico;
        this.fechaAtencion = fechaAtencion;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIngresoId() {
        return ingresoId;
    }

    public void setIngresoId(String ingresoId) {
        this.ingresoId = ingresoId;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(String medicoId) {
        this.medicoId = medicoId;
    }

    public String getInformeMedico() {
        return informeMedico;
    }

    public void setInformeMedico(String informeMedico) {
        this.informeMedico = informeMedico;
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    public void setFechaAtencion(LocalDateTime fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }
}
