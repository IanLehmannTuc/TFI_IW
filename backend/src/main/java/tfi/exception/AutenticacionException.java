package tfi.exception;

/**
 * Excepción lanzada cuando falla la autenticación de un usuario.
 * IMPORTANTE: El mensaje debe ser siempre genérico para prevenir 
 * ataques de enumeración de usuarios.
 */
public class AutenticacionException extends RuntimeException {

    /**
     * Constructor con mensaje
     * 
     * @param mensaje Mensaje de error (debe ser genérico)
     */
    public AutenticacionException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * 
     * @param mensaje Mensaje de error
     * @param causa Causa raíz de la excepción
     */
    public AutenticacionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

