package tfi.application.dto;

/**
 * DTO para el registro de una atención médica.
 * Contiene los datos necesarios para crear una nueva atención.
 */
public class RegistroAtencionRequest {
    
    private String ingresoId;
    private String informe;

    public RegistroAtencionRequest() {
    }

    public RegistroAtencionRequest(String ingresoId, String informe) {
        this.ingresoId = ingresoId;
        this.informe = informe;
    }

    public String getIngresoId() {
        return ingresoId;
    }

    public void setIngresoId(String ingresoId) {
        this.ingresoId = ingresoId;
    }

    public String getInforme() {
        return informe;
    }

    public void setInforme(String informe) {
        this.informe = informe;
    }
}
