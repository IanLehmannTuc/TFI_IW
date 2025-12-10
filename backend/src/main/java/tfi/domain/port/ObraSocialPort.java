package tfi.domain.port;

import tfi.application.dto.ObraSocialResponse;
import tfi.application.dto.VerificacionAfiliacionResponse;

import java.util.List;

/**
 * Puerto (interfaz) para operaciones con obras sociales.
 * Define el contrato que debe cumplir cualquier implementación de servicios de obras sociales.
 * Sigue el principio de inversión de dependencias (DIP).
 */
public interface ObraSocialPort {

    /**
     * Lista todas las obras sociales disponibles.
     * 
     * @return Lista de obras sociales disponibles
     * @throws tfi.exception.ObraSocialException Si la API no está disponible 
     *         o hay un error en la comunicación
     */
    List<ObraSocialResponse> listarObrasSociales();

    /**
     * Verifica si un paciente está afiliado a una obra social específica.
     * 
     * @param obraSocialId ID de la obra social a verificar
     * @param numeroAfiliado Número de afiliado del paciente
     * @return VerificacionAfiliacionResponse con el resultado de la verificación
     * @throws tfi.exception.ObraSocialException Si la obra social no existe, 
     *         la API no está disponible, o hay un error en la comunicación
     */
    VerificacionAfiliacionResponse verificarAfiliacion(int obraSocialId, String numeroAfiliado);
}
