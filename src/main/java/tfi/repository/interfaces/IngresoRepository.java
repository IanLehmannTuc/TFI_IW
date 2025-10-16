package tfi.repository.interfaces;

import tfi.model.entity.Ingreso;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la persistencia de ingresos de pacientes a urgencias.
 */
public interface IngresoRepository {

    /**
     * Obtiene todos los ingresos registrados en el sistema.
     * @return lista de todos los ingresos
     */
    List<Ingreso> findAll();

    /**
     * Guarda un nuevo ingreso en el repositorio.
     * @param ingreso el ingreso a registrar
     * @return el ingreso registrado
     */
    Ingreso add(Ingreso ingreso);

    /**
     * Elimina un ingreso del repositorio.
     * @param ingreso el ingreso a eliminar
     * @return el ingreso eliminado
     */
    Ingreso delete(Ingreso ingreso);

    /**
     * Actualiza un ingreso existente en el repositorio.
     * @param ingreso el ingreso con los datos actualizados
     * @return el ingreso actualizado
     */
    Ingreso update(Ingreso ingreso);
    
    /**
     * Busca un ingreso por un identificador único, si existe.
     * @param ingresoId identificador del ingreso
     * @return un Optional con el ingreso si existe, o vacío si no se encuentra
     */
    Optional<Ingreso> findById(String ingresoId);
}
