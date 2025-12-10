package tfi.domain.repository;

import tfi.domain.entity.Usuario;
import tfi.domain.enums.Autoridad;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para gestionar la persistencia de usuarios (personal médico).
 * Define las operaciones CRUD básicas para la entidad Usuario.
 */
public interface UsuarioRepository {

    /**
     * Agrega un nuevo usuario al repositorio.
     * 
     * @param usuario El usuario a agregar
     * @return El usuario agregado
     * @throws IllegalArgumentException Si ya existe un usuario con ese email
     */
    Usuario add(Usuario usuario);

    /**
     * Busca un usuario por su ID.
     * 
     * @param id El ID del usuario a buscar
     * @return Un Optional con el usuario si existe, vacío si no se encuentra
     */
    Optional<Usuario> findById(String id);

    /**
     * Busca un usuario por su email.
     * 
     * @param email El email del usuario a buscar
     * @return Un Optional con el usuario si existe, vacío si no se encuentra
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Busca un usuario por su CUIL.
     * 
     * @param cuil El CUIL del usuario a buscar
     * @return Un Optional con el usuario si existe, vacío si no se encuentra
     */
    Optional<Usuario> findByCuil(String cuil);

    /**
     * Busca un usuario por su matrícula profesional.
     * 
     * @param matricula La matrícula del usuario a buscar
     * @return Un Optional con el usuario si existe, vacío si no se encuentra
     */
    Optional<Usuario> findByMatricula(String matricula);

    /**
     * Verifica si existe un usuario con el email especificado.
     * 
     * @param email El email a verificar
     * @return true si existe un usuario con ese email, false en caso contrario
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe un usuario con el CUIL especificado.
     * 
     * @param cuil El CUIL a verificar
     * @return true si existe un usuario con ese CUIL, false en caso contrario
     */
    boolean existsByCuil(String cuil);

    /**
     * Verifica si existe un usuario con la matrícula especificada.
     * 
     * @param matricula La matrícula a verificar
     * @return true si existe un usuario con esa matrícula, false en caso contrario
     */
    boolean existsByMatricula(String matricula);

    /**
     * Obtiene todos los usuarios registrados.
     * 
     * @return Lista de todos los usuarios
     */
    List<Usuario> findAll();

    /**
     * Obtiene todos los usuarios con una autoridad específica.
     * 
     * @param autoridad La autoridad a filtrar (MEDICO o ENFERMERO)
     * @return Lista de usuarios con la autoridad especificada
     */
    List<Usuario> findByAutoridad(Autoridad autoridad);

    /**
     * Actualiza los datos de un usuario existente.
     * 
     * @param usuario El usuario con los datos actualizados
     * @return El usuario actualizado
     * @throws IllegalArgumentException Si el usuario no existe
     */
    Usuario update(Usuario usuario);

    /**
     * Elimina un usuario del repositorio.
     * 
     * @param email El email del usuario a eliminar
     * @return El usuario eliminado
     * @throws IllegalArgumentException Si el usuario no existe
     */
    Usuario delete(String email);

    /**
     * Elimina todos los usuarios del repositorio.
     * Útil para testing.
     */
    void deleteAll();
}

