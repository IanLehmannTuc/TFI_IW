package tfi.repository.impl.memory;

import org.springframework.stereotype.Repository;
import tfi.model.entity.Usuario;
import tfi.repository.interfaces.UsuarioRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del repositorio de usuarios.
 * Utiliza un ConcurrentHashMap para almacenar usuarios indexados por email.
 * Thread-safe para entornos concurrentes.
 */
@Repository
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
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailKey = email.toLowerCase();
        return usuarios.containsKey(emailKey);
    }

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(usuarios.values());
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

