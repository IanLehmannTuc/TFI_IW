package tfi.repository.interfaces;

import tfi.model.entity.Paciente;
import java.util.List;

/**
 * Repositorio para gestionar la persistencia de pacientes.
 */
public interface PacientesRepository {
    
    /**
     * Obtiene todos los pacientes registrados.
     * @return lista de todos los pacientes
     */
    List<Paciente> findAll();
    
    /**
     * Busca un paciente por su CUIL.
     * @param cuil el CUIL del paciente a buscar
     * @return un Optional con el paciente si existe, o vacío si no se encuentra
     */
    Paciente findByCuil(String cuil);
    
    /**
     * Verifica si existe un paciente con el CUIL especificado.
     * @param cuil el CUIL a verificar
     * @return true si existe un paciente con ese CUIL, false en caso contrario
     */
    boolean existsByCuil(String cuil);
    
    /**
     * Agrega un nuevo paciente al repositorio.
     * @param paciente el paciente a agregar
     * @return el paciente agregado
     */
    Paciente add(Paciente paciente);
    
    /**
     * Actualiza los datos de un paciente existente.
     * @param paciente el paciente con los datos actualizados
     * @return el paciente actualizado
     */
    Paciente update(Paciente paciente);
    
    /**
     * Elimina un paciente del repositorio.
     * @param paciente el paciente a eliminar
     * @return el paciente eliminado
     */
    Paciente delete(Paciente paciente);
}

