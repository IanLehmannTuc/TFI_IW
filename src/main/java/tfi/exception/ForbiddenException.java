package tfi.exception;

/**
 * Excepción lanzada cuando un usuario autenticado intenta acceder a un recurso
 * para el cual no tiene permisos suficientes.
 */
public class ForbiddenException extends RuntimeException {
    
    /**
     * Constructor con mensaje
     * 
     * @param mensaje Mensaje describiendo el problema de permisos
     */
    public ForbiddenException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa
     * 
     * @param mensaje Mensaje describiendo el problema de permisos
     * @param causa Causa raíz de la excepción
     */
    public ForbiddenException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

