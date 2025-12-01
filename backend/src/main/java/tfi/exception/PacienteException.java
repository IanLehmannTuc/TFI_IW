package tfi.exception;

/**
 * Excepción lanzada cuando ocurre un error durante el registro o gestión de pacientes.
 * Puede contener mensajes específicos sobre problemas de validación o duplicados.
 */
public class PacienteException extends RuntimeException {
    
    /**
     * Constructor con mensaje
     * 
     * @param mensaje Mensaje descriptivo del error
     */
    public PacienteException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * 
     * @param mensaje Mensaje descriptivo del error
     * @param causa Causa raíz de la excepción
     */
    public PacienteException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

