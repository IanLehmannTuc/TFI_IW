package tfi.infrastructure.persistence.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.Paciente;
import tfi.domain.repository.PacientesRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementación en memoria del repositorio de Paciente.
 * Útil para testing y desarrollo sin base de datos.
 * 
 * Se activa cuando el perfil "memory" está activo.
 */
@Repository
@Profile("memory")
public class PacientesRepositoryImpl implements PacientesRepository {

    private final Map<String, Paciente> store;

    public PacientesRepositoryImpl() {
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public List<Paciente> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Paciente> findByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(cuil));
    }

    @Override
    public boolean existsByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return false;
        }
        return store.containsKey(cuil);
    }

    @Override
    public Paciente add(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (paciente.getCuil() == null || paciente.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del paciente no puede ser nulo o vacío");
        }
        if (store.containsKey(paciente.getCuil())) {
            throw new IllegalStateException("Ya existe un paciente con el CUIL: " + paciente.getCuil());
        }
        
        // Generar ID único si no tiene
        if (paciente.getId() == null) {
            paciente.setId(java.util.UUID.randomUUID().toString());
        }
        
        store.put(paciente.getCuil(), paciente);
        return paciente;
    }

    @Override
    public Paciente update(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (paciente.getCuil() == null || paciente.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del paciente no puede ser nulo o vacío");
        }
        if (!store.containsKey(paciente.getCuil())) {
            throw new IllegalStateException("No existe un paciente con el CUIL: " + paciente.getCuil());
        }
        store.put(paciente.getCuil(), paciente);
        return paciente;
    }

    @Override
    public Paciente delete(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (paciente.getCuil() == null || paciente.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del paciente no puede ser nulo o vacío");
        }
        Paciente removed = store.remove(paciente.getCuil());
        if (removed == null) {
            throw new IllegalStateException("No existe un paciente con el CUIL: " + paciente.getCuil());
        }
        return removed;
    }

    public void clear() {
        store.clear();
    }

    public int count() {
        return store.size();
    }
}
