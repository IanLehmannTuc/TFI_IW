package tfi.application.dto;

import tfi.domain.enums.NivelEmergencia;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la solicitud de registro de un nuevo ingreso a urgencias.
 * Contiene los datos necesarios para crear un ingreso en el sistema.
 */
public class RegistroIngresoRequest {
    
    @NotBlank(message = "El CUIL del paciente es obligatorio")
    private String pacienteCuil;
    
    private String pacienteNombre;
    private String pacienteApellido;
    private String pacienteEmail;
    
    private RegistroPacienteRequest.DomicilioRequest pacienteDomicilio;
    
    private RegistroPacienteRequest.AfiliadoRequest pacienteObraSocial;
    
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
    private NivelEmergencia nivelEmergencia;

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
                                  NivelEmergencia nivelEmergencia) {
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

    public String getPacienteNombre() {
        return pacienteNombre;
    }

    public void setPacienteNombre(String pacienteNombre) {
        this.pacienteNombre = pacienteNombre;
    }

    public String getPacienteApellido() {
        return pacienteApellido;
    }

    public void setPacienteApellido(String pacienteApellido) {
        this.pacienteApellido = pacienteApellido;
    }

    public String getPacienteEmail() {
        return pacienteEmail;
    }

    public void setPacienteEmail(String pacienteEmail) {
        this.pacienteEmail = pacienteEmail;
    }

    public RegistroPacienteRequest.DomicilioRequest getPacienteDomicilio() {
        return pacienteDomicilio;
    }

    public void setPacienteDomicilio(RegistroPacienteRequest.DomicilioRequest pacienteDomicilio) {
        this.pacienteDomicilio = pacienteDomicilio;
    }

    public RegistroPacienteRequest.AfiliadoRequest getPacienteObraSocial() {
        return pacienteObraSocial;
    }

    public void setPacienteObraSocial(RegistroPacienteRequest.AfiliadoRequest pacienteObraSocial) {
        this.pacienteObraSocial = pacienteObraSocial;
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

    public NivelEmergencia getNivelEmergencia() {
        return nivelEmergencia;
    }

    public void setNivelEmergencia(NivelEmergencia nivelEmergencia) {
        this.nivelEmergencia = nivelEmergencia;
    }
}
