package tfi.domain.repository;

import tfi.domain.entity.Atencion;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la persistencia de atenciones médicas.
 */
public interface AtencionRepository {

    /**
     * Busca una atención por su identificador único.
     * @param id identificador de la atención
     * @return un Optional con la atención si existe, o vacío si no se encuentra
     */
    Optional<Atencion> findById(String id);

    /**
     * Busca la atención asociada a un ingreso específico.
     * @param ingresoId identificador del ingreso
     * @return un Optional con la atención si existe, o vacío si no se encuentra
     */
    Optional<Atencion> findByIngresoId(String ingresoId);

    /**
     * Guarda una nueva atención en el repositorio.
     * @param atencion la atención a registrar
     * @return la atención registrada con su ID asignado
     */
    Atencion add(Atencion atencion);

    /**
     * Actualiza una atención existente en el repositorio.
     * @param atencion la atención con los datos actualizados
     * @return la atención actualizada
     */
    Atencion update(Atencion atencion);

    /**
     * Obtiene todas las atenciones registradas en el sistema.
     * @return lista de todas las atenciones
     */
    List<Atencion> findAll();

    /**
     * Elimina una atención del repositorio.
     * @param atencion la atención a eliminar
     * @return la atención eliminada
     */
    Atencion delete(Atencion atencion);
}
