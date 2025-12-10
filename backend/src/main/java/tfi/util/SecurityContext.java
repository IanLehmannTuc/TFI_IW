package tfi.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.lang.NonNull;
import tfi.exception.AutenticacionException;
import tfi.exception.ForbiddenException;
import tfi.application.dto.UsuarioAutenticado;
import tfi.domain.enums.Autoridad;

/**
 * Utilidad para acceder al contexto de seguridad de la aplicación.
 * Proporciona métodos para obtener el usuario autenticado actual
 * y verificar permisos.
 */
public class SecurityContext {

    private static final String USER_ID_ATTRIBUTE = "userId";
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
     * @throws IllegalArgumentException Si el request es null
     */
    public static UsuarioAutenticado getUsuarioAutenticado(@NonNull HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser nulo");
        }
        String id = (String) request.getAttribute(USER_ID_ATTRIBUTE);
        String email = (String) request.getAttribute(USER_EMAIL_ATTRIBUTE);
        Autoridad autoridad = (Autoridad) request.getAttribute(USER_AUTORIDAD_ATTRIBUTE);

        if (id == null || email == null || autoridad == null) {
            throw new AutenticacionException(MensajesError.NO_AUTENTICADO);
        }

        return new UsuarioAutenticado(id, email, autoridad);
    }

    /**
     * Intenta obtener el usuario autenticado, retornando null si no existe.
     * No lanza excepción.
     * 
     * @param request El request HTTP actual
     * @return El usuario autenticado o null si no está autenticado
     * @throws IllegalArgumentException Si el request es null
     */
    public static UsuarioAutenticado getUsuarioAutenticadoOrNull(@NonNull HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser nulo");
        }
        String id = (String) request.getAttribute(USER_ID_ATTRIBUTE);
        String email = (String) request.getAttribute(USER_EMAIL_ATTRIBUTE);
        Autoridad autoridad = (Autoridad) request.getAttribute(USER_AUTORIDAD_ATTRIBUTE);

        if (id == null || email == null || autoridad == null) {
            return null;
        }

        return new UsuarioAutenticado(id, email, autoridad);
    }

    /**
     * Verifica si el usuario tiene una autoridad específica.
     * Lanza excepción si no tiene el permiso requerido.
     * 
     * @param request El request HTTP actual
     * @param autoridadRequerida La autoridad necesaria
     * @throws ForbiddenException Si el usuario no tiene el permiso
     * @throws AutenticacionException Si no hay usuario autenticado
     * @throws IllegalArgumentException Si el request o la autoridad son null
     */
    public static void requireAutoridad(@NonNull HttpServletRequest request, @NonNull Autoridad autoridadRequerida) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser nulo");
        }
        if (autoridadRequerida == null) {
            throw new IllegalArgumentException("La autoridad requerida no puede ser nula");
        }
        UsuarioAutenticado usuario = getUsuarioAutenticado(request);

        if (usuario.getAutoridad() != autoridadRequerida) {
            throw new ForbiddenException(
                MensajesError.sinPermisosParaAutoridad(autoridadRequerida.name())
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
     * @throws IllegalArgumentException Si el request es null o las autoridades están vacías
     */
    public static void requireAnyAutoridad(@NonNull HttpServletRequest request, Autoridad... autoridades) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser nulo");
        }
        if (autoridades == null || autoridades.length == 0) {
            throw new IllegalArgumentException("Debe especificar al menos una autoridad");
        }
        UsuarioAutenticado usuario = getUsuarioAutenticado(request);

        for (Autoridad autoridad : autoridades) {
            if (usuario.getAutoridad() == autoridad) {
                return;
            }
        }

        String autoridadesStr = String.join(", ", java.util.Arrays.stream(autoridades)
                .map(Enum::name)
                .toArray(String[]::new));
        throw new ForbiddenException(
            MensajesError.sinPermisosParaAutoridades(autoridadesStr)
        );
    }

    /**
     * Verifica si hay un usuario autenticado sin lanzar excepción.
     * 
     * @param request El request HTTP actual
     * @return true si hay usuario autenticado, false en caso contrario
     * @throws IllegalArgumentException Si el request es null
     */
    public static boolean isAuthenticated(@NonNull HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("El request no puede ser nulo");
        }
        return getUsuarioAutenticadoOrNull(request) != null;
    }
}

