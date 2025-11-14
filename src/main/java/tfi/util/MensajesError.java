package tfi.util;

/**
 * Constantes para mensajes de error estandarizados en la aplicación.
 * Centraliza todos los mensajes para facilitar mantenimiento e internacionalización futura.
 */
public final class MensajesError {
    
    /**
     * Constructor privado para prevenir instanciación
     */
    private MensajesError() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }
    
    /**
     * Mensaje genérico para errores de autenticación.
     * IMPORTANTE: Siempre usar este mensaje para prevenir enumeración de usuarios.
     */
    public static final String USUARIO_CONTRASENA_INVALIDOS = "Usuario o contraseña inválidos";
    
    /**
     * Mensaje cuando no hay token JWT en la petición.
     */
    public static final String NO_AUTENTICADO = "No autenticado. Token JWT requerido.";
    
    /**
     * Mensaje cuando los datos de registro son nulos.
     */
    public static final String DATOS_REGISTRO_NULOS = "Los datos de registro no pueden ser nulos";
    
    /**
     * Mensaje cuando el email ya está registrado.
     */
    public static final String EMAIL_YA_REGISTRADO = "El email ya está registrado";
    
    /**
     * Mensaje cuando no se especifica una autoridad.
     */
    public static final String AUTORIDAD_REQUERIDA = "Debe especificar una autoridad (MEDICO o ENFERMERA)";
    
    /**
     * Mensaje base para errores de permisos.
     */
    public static final String SIN_PERMISOS = "No tiene permisos para esta operación";
    
    /**
     * Genera mensaje de permisos con autoridad requerida.
     * 
     * @param autoridadRequerida La autoridad que se requiere
     * @return Mensaje de error con la autoridad especificada
     */
    public static String sinPermisosParaAutoridad(String autoridadRequerida) {
        return SIN_PERMISOS + ". Se requiere: " + autoridadRequerida;
    }
    
    /**
     * Genera mensaje de permisos con múltiples autoridades aceptadas.
     * 
     * @param autoridades Las autoridades aceptadas
     * @return Mensaje de error con las autoridades especificadas
     */
    public static String sinPermisosParaAutoridades(String autoridades) {
        return SIN_PERMISOS + ". Se requiere una de: " + autoridades;
    }
    
    /**
     * Mensaje cuando el CUIL del paciente ya está registrado.
     */
    public static final String CUIL_YA_REGISTRADO = "Ya existe un paciente con el CUIL especificado";
}

