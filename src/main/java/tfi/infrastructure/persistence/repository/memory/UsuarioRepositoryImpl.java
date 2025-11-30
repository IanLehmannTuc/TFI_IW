package tfi.infrastructure.persistence.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.Usuario;
import tfi.domain.enums.Autoridad;
import tfi.domain.repository.UsuarioRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del repositorio de usuarios.
 * Utiliza un ConcurrentHashMap para almacenar usuarios indexados por email.
 * Thread-safe para entornos concurrentes.
 * 
 * Se activa cuando el perfil "memory" está activo.
 */
@Repository
@Profile("memory")
public class UsuarioRepositoryImpl implements UsuarioRepository {
    
    private final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();

    @Override
    public Usuario add(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getEmail() == null) {
            throw new IllegalArgumentException("El email del usuario no puede ser nulo");
        }
        
        String emailKey = usuario.getEmail().getValue().toLowerCase();
        
        if (usuarios.containsKey(emailKey)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail().getValue());
        }
        
        // Verificar CUIL único
        if (usuario.getCuilVO() != null && existsByCuil(usuario.getCuil())) {
            throw new IllegalArgumentException("Ya existe un usuario con el CUIL: " + usuario.getCuil());
        }
        
        // Verificar matrícula única
        if (usuario.getMatricula() != null && existsByMatricula(usuario.getMatricula())) {
            throw new IllegalArgumentException("Ya existe un usuario con la matrícula: " + usuario.getMatricula());
        }
        
        usuarios.put(emailKey, usuario);
        return usuario;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        
        String emailKey = email.toLowerCase();
        return Optional.ofNullable(usuarios.get(emailKey));
    }

    @Override
    public Optional<Usuario> findByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return usuarios.values().stream()
            .filter(u -> u.getCuil() != null && u.getCuil().equals(cuil))
            .findFirst();
    }

    @Override
    public Optional<Usuario> findByMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return Optional.empty();
        }
        
        return usuarios.values().stream()
            .filter(u -> u.getMatricula() != null && u.getMatricula().equals(matricula))
            .findFirst();
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailKey = email.toLowerCase();
        return usuarios.containsKey(emailKey);
    }

    @Override
    public boolean existsByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return false;
        }
        
        return usuarios.values().stream()
            .anyMatch(u -> u.getCuil() != null && u.getCuil().equals(cuil));
    }

    @Override
    public boolean existsByMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return false;
        }
        
        return usuarios.values().stream()
            .anyMatch(u -> u.getMatricula() != null && u.getMatricula().equals(matricula));
    }

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(usuarios.values());
    }

    @Override
    public List<Usuario> findByAutoridad(Autoridad autoridad) {
        if (autoridad == null) {
            throw new IllegalArgumentException("La autoridad no puede ser nula");
        }
        
        return usuarios.values().stream()
            .filter(u -> u.getAutoridad() == autoridad)
            .collect(Collectors.toList());
    }

    @Override
    public Usuario update(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getEmail() == null) {
            throw new IllegalArgumentException("El email del usuario no puede ser nulo");
        }
        
        String emailKey = usuario.getEmail().getValue().toLowerCase();
        
        if (!usuarios.containsKey(emailKey)) {
            throw new IllegalArgumentException("No existe un usuario con el email: " + usuario.getEmail().getValue());
        }
        
        usuarios.put(emailKey, usuario);
        return usuario;
    }

    @Override
    public Usuario delete(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        
        String emailKey = email.toLowerCase();
        Usuario usuario = usuarios.remove(emailKey);
        
        if (usuario == null) {
            throw new IllegalArgumentException("No existe un usuario con el email: " + email);
        }
        
        return usuario;
    }

    @Override
    public void deleteAll() {
        usuarios.clear();
    }
}

