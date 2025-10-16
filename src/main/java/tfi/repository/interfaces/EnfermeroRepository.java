package tfi.repository.interfaces;

import tfi.model.entity.Enfermero;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la persistencia de enfermeros.
 */
public interface EnfermeroRepository {
    
    /**
     * Obtiene todos los enfermeros registrados.
     * @return lista de todos los enfermeros
     */
    List<Enfermero> findAll();
    
    /**
     * Busca un enfermero por su CUIL.
     * @param cuil el CUIL del enfermero a buscar
     * @return un Optional con el enfermero si existe, o vacío si no se encuentra
     */
    Optional<Enfermero> findByCuil(String cuil);
    
    /**
     * Verifica si existe un enfermero con el CUIL especificado.
     * @param cuil el CUIL a verificar
     * @return true si existe un enfermero con ese CUIL, false en caso contrario
     */
    boolean existsByCuil(String cuil);
    
    /**
     * Agrega un nuevo enfermero al repositorio.
     * @param enfermero el enfermero a agregar
     * @return el enfermero agregado
     */
    Enfermero add(Enfermero enfermero);
    
    /**
     * Actualiza los datos de un enfermero existente.
     * @param enfermero el enfermero con los datos actualizados
     * @return el enfermero actualizado
     */
    Enfermero update(Enfermero enfermero);
    
    /**
     * Elimina un enfermero del repositorio.
     * @param enfermero el enfermero a eliminar
     * @return el enfermero eliminado
     */
    Enfermero delete(Enfermero enfermero);
}
