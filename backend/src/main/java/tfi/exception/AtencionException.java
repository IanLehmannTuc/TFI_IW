package tfi.exception;

/**
 * Excepción lanzada cuando ocurre un error durante el registro o gestión de atenciones médicas.
 * Puede contener mensajes específicos sobre problemas de validación, duplicados o estados inválidos.
 */
public class AtencionException extends RuntimeException {

    /**
     * Constructor con mensaje
     * 
     * @param mensaje Mensaje descriptivo del error
     */
    public AtencionException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * 
     * @param mensaje Mensaje descriptivo del error
     * @param causa Causa raíz de la excepción
     */
    public AtencionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
