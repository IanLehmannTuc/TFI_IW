package tfi.model.dto;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta de registro o consulta de ingreso.
 * Contiene la información del ingreso registrado.
 */
public class IngresoResponse {
    private String id;
    private String pacienteCuil;
    private String pacienteNombre;
    private String pacienteApellido;
    private String enfermeroCuil;
    private String enfermeroMatricula;
    private String descripcion;
    private LocalDateTime fechaHoraIngreso;
    private Double temperatura;
    private Integer tensionSistolica;
    private Integer tensionDiastolica;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private tfi.model.enums.NivelEmergencia nivelEmergencia;

    /**
     * Constructor por defecto necesario para serialización JSON
     */
    public IngresoResponse() {
    }

    /**
     * Constructor completo
     */
    public IngresoResponse(String id, String pacienteCuil, String pacienteNombre, String pacienteApellido,
                          String enfermeroCuil, String enfermeroMatricula, String descripcion,
                          LocalDateTime fechaHoraIngreso, Double temperatura, Integer tensionSistolica,
                          Integer tensionDiastolica, Integer frecuenciaCardiaca, Integer frecuenciaRespiratoria,
                          tfi.model.enums.NivelEmergencia nivelEmergencia) {
        this.id = id;
        this.pacienteCuil = pacienteCuil;
        this.pacienteNombre = pacienteNombre;
        this.pacienteApellido = pacienteApellido;
        this.enfermeroCuil = enfermeroCuil;
        this.enfermeroMatricula = enfermeroMatricula;
        this.descripcion = descripcion;
        this.fechaHoraIngreso = fechaHoraIngreso;
        this.temperatura = temperatura;
        this.tensionSistolica = tensionSistolica;
        this.tensionDiastolica = tensionDiastolica;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.nivelEmergencia = nivelEmergencia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getEnfermeroCuil() {
        return enfermeroCuil;
    }

    public void setEnfermeroCuil(String enfermeroCuil) {
        this.enfermeroCuil = enfermeroCuil;
    }

    public String getEnfermeroMatricula() {
        return enfermeroMatricula;
    }

    public void setEnfermeroMatricula(String enfermeroMatricula) {
        this.enfermeroMatricula = enfermeroMatricula;
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

