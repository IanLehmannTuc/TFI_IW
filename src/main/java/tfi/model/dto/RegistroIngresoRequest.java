package tfi.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la solicitud de registro de un nuevo ingreso a urgencias.
 * Contiene los datos necesarios para crear un ingreso en el sistema.
 */
public class RegistroIngresoRequest {
    
    @NotBlank(message = "El CUIL del paciente es obligatorio")
    private String pacienteCuil;
    
    @NotBlank(message = "El CUIL del enfermero es obligatorio")
    private String enfermeroCuil;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
    
    @NotNull(message = "La temperatura es obligatoria")
    private Double temperatura;
    
    @NotNull(message = "La tensión arterial sistólica es obligatoria")
    private Integer tensionSistolica;
    
    @NotNull(message = "La tensión arterial diastólica es obligatoria")
    private Integer tensionDiastolica;
    
    @NotNull(message = "La frecuencia cardíaca es obligatoria")
    private Integer frecuenciaCardiaca;
    
    @NotNull(message = "La frecuencia respiratoria es obligatoria")
    private Integer frecuenciaRespiratoria;
    
    @NotNull(message = "El nivel de emergencia es obligatorio")
    private tfi.model.enums.NivelEmergencia nivelEmergencia;

    /**
     * Constructor por defecto necesario para deserialización JSON
     */
    public RegistroIngresoRequest() {
    }

    /**
     * Constructor completo
     */
    public RegistroIngresoRequest(String pacienteCuil, String enfermeroCuil, String descripcion,
                                  Double temperatura, Integer tensionSistolica, Integer tensionDiastolica,
                                  Integer frecuenciaCardiaca, Integer frecuenciaRespiratoria,
                                  tfi.model.enums.NivelEmergencia nivelEmergencia) {
        this.pacienteCuil = pacienteCuil;
        this.enfermeroCuil = enfermeroCuil;
        this.descripcion = descripcion;
        this.temperatura = temperatura;
        this.tensionSistolica = tensionSistolica;
        this.tensionDiastolica = tensionDiastolica;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.nivelEmergencia = nivelEmergencia;
    }

    public String getPacienteCuil() {
        return pacienteCuil;
    }

    public void setPacienteCuil(String pacienteCuil) {
        this.pacienteCuil = pacienteCuil;
    }

    public String getEnfermeroCuil() {
        return enfermeroCuil;
    }

    public void setEnfermeroCuil(String enfermeroCuil) {
        this.enfermeroCuil = enfermeroCuil;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Integer getTensionSistolica() {
        return tensionSistolica;
    }

    public void setTensionSistolica(Integer tensionSistolica) {
        this.tensionSistolica = tensionSistolica;
    }

    public Integer getTensionDiastolica() {
        return tensionDiastolica;
    }

    public void setTensionDiastolica(Integer tensionDiastolica) {
        this.tensionDiastolica = tensionDiastolica;
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public Integer getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public tfi.model.enums.NivelEmergencia getNivelEmergencia() {
        return nivelEmergencia;
    }

    public void setNivelEmergencia(tfi.model.enums.NivelEmergencia nivelEmergencia) {
        this.nivelEmergencia = nivelEmergencia;
    }
}

