package tfi.exception;

/**
 * Excepción lanzada cuando ocurre un error relacionado con obras sociales.
 * Puede ser por verificación de afiliación fallida, API no disponible, etc.
 */
public class ObraSocialException extends RuntimeException {
    
    /**
     * Constructor con mensaje
     * 
     * @param mensaje Mensaje descriptivo del error
     */
    public ObraSocialException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * 
     * @param mensaje Mensaje descriptivo del error
     * @param causa Causa raíz de la excepción
     */
    public ObraSocialException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
