package tfi.exception;

/**
 * Excepción lanzada cuando ocurre un error durante el registro de un usuario.
 * A diferencia de AutenticacionException, esta puede contener mensajes específicos
 * sobre problemas de validación o duplicados.
 */
public class RegistroException extends RuntimeException {
    
    /**
     * Constructor con mensaje
     * 
     * @param mensaje Mensaje descriptivo del error
     */
    public RegistroException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * 
     * @param mensaje Mensaje descriptivo del error
     * @param causa Causa raíz de la excepción
     */
    public RegistroException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

