package tfi.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para respuestas de error estandarizadas.
 * Proporciona información detallada sobre errores que ocurren en la aplicación.
 */
public class ErrorResponse {
    private String mensaje;
    private LocalDateTime timestamp;
    private Integer status;

    /**
     * Constructor con mensaje
     * 
     * @param mensaje Descripción del error
     */
    public ErrorResponse(String mensaje) {
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor completo
     * 
     * @param mensaje Descripción del error
     * @param status Código de estado HTTP
     */
    public ErrorResponse(String mensaje, Integer status) {
        this.mensaje = mensaje;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "mensaje='" + mensaje + '\'' +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }
}

