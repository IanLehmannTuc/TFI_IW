package tfi.util;

import jakarta.servlet.http.HttpServletRequest;
import tfi.exception.AutenticacionException;
import tfi.exception.ForbiddenException;
import tfi.model.dto.UsuarioAutenticado;
import tfi.model.enums.Autoridad;

/**
 * Utilidad para acceder al contexto de seguridad de la aplicación.
 * Proporciona métodos para obtener el usuario autenticado actual
 * y verificar permisos.
 */
public class SecurityContext {

    // Atributos del request donde se almacena la información del usuario
    private static final String USER_EMAIL_ATTRIBUTE = "userEmail";
    private static final String USER_AUTORIDAD_ATTRIBUTE = "userAutoridad";

    /**
     * Constructor privado para prevenir instanciación
     */
    private SecurityContext() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    /**
     * Obtiene el usuario autenticado desde el request actual.
     * El filtro JWT debe haber agregado los atributos previamente.
     * 
     * @param request El request HTTP actual
     * @return El usuario autenticado
     * @throws AutenticacionException Si no hay usuario autenticado
     */
    public static UsuarioAutenticado getUsuarioAutenticado(HttpServletRequest request) {
        String email = (String) request.getAttribute(USER_EMAIL_ATTRIBUTE);
        Autoridad autoridad = (Autoridad) request.getAttribute(USER_AUTORIDAD_ATTRIBUTE);
        
        if (email == null || autoridad == null) {
            throw new AutenticacionException("No autenticado. Token JWT requerido.");
        }
        
        return new UsuarioAutenticado(email, autoridad);
    }

    /**
     * Intenta obtener el usuario autenticado, retornando null si no existe.
     * No lanza excepción.
     * 
     * @param request El request HTTP actual
     * @return El usuario autenticado o null si no está autenticado
     */
    public static UsuarioAutenticado getUsuarioAutenticadoOrNull(HttpServletRequest request) {
        String email = (String) request.getAttribute(USER_EMAIL_ATTRIBUTE);
        Autoridad autoridad = (Autoridad) request.getAttribute(USER_AUTORIDAD_ATTRIBUTE);
        
        if (email == null || autoridad == null) {
            return null;
        }
        
        return new UsuarioAutenticado(email, autoridad);
    }

    /**
     * Verifica si el usuario tiene una autoridad específica.
     * Lanza excepción si no tiene el permiso requerido.
     * 
     * @param request El request HTTP actual
     * @param autoridadRequerida La autoridad necesaria
     * @throws ForbiddenException Si el usuario no tiene el permiso
     * @throws AutenticacionException Si no hay usuario autenticado
     */
    public static void requireAutoridad(HttpServletRequest request, Autoridad autoridadRequerida) {
        UsuarioAutenticado usuario = getUsuarioAutenticado(request);
        
        if (usuario.getAutoridad() != autoridadRequerida) {
            throw new ForbiddenException(
                "No tiene permisos para esta operación. Se requiere: " + autoridadRequerida
            );
        }
    }

    /**
     * Verifica si el usuario tiene alguna de las autoridades especificadas.
     * 
     * @param request El request HTTP actual
     * @param autoridades Las autoridades aceptadas
     * @throws ForbiddenException Si el usuario no tiene ninguna de las autoridades
     * @throws AutenticacionException Si no hay usuario autenticado
     */
    public static void requireAnyAutoridad(HttpServletRequest request, Autoridad... autoridades) {
        UsuarioAutenticado usuario = getUsuarioAutenticado(request);
        
        for (Autoridad autoridad : autoridades) {
            if (usuario.getAutoridad() == autoridad) {
                return;
            }
        }
        
        throw new ForbiddenException(
            "No tiene permisos para esta operación. Se requiere una de: " + 
            String.join(", ", java.util.Arrays.stream(autoridades)
                .map(Enum::name)
                .toArray(String[]::new))
        );
    }

    /**
     * Verifica si hay un usuario autenticado sin lanzar excepción.
     * 
     * @param request El request HTTP actual
     * @return true si hay usuario autenticado, false en caso contrario
     */
    public static boolean isAuthenticated(HttpServletRequest request) {
        return getUsuarioAutenticadoOrNull(request) != null;
    }
}

