package tfi.infrastructure.persistence.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.Paciente;
import tfi.domain.repository.PacientesRepository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
    public Page<Paciente> findAll(Pageable pageable) {
        List<Paciente> allPacientes = new ArrayList<>(store.values());
        
        // Aplicar ordenamiento si existe
        if (pageable.getSort().isSorted()) {
            Comparator<Paciente> comparator = null;
            for (Sort.Order order : pageable.getSort()) {
                Comparator<Paciente> orderComparator = getComparator(order);
                if (comparator == null) {
                    comparator = orderComparator;
                } else {
                    comparator = comparator.thenComparing(orderComparator);
                }
            }
            if (comparator != null) {
                allPacientes = allPacientes.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
            }
        } else {
            // Ordenamiento por defecto por CUIL
            allPacientes = allPacientes.stream()
                .sorted(Comparator.comparing(Paciente::getCuil))
                .collect(Collectors.toList());
        }
        
        // Aplicar paginación
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allPacientes.size());
        List<Paciente> pageContent = start < allPacientes.size() 
            ? allPacientes.subList(start, end)
            : new ArrayList<>();
        
        return new PageImpl<>(pageContent, pageable, allPacientes.size());
    }
    
    /**
     * Obtiene un Comparator para ordenar pacientes según una propiedad.
     */
    private Comparator<Paciente> getComparator(Sort.Order order) {
        Comparator<Paciente> comparator = switch (order.getProperty().toLowerCase()) {
            case "cuil" -> Comparator.comparing(p -> p.getCuil() != null ? p.getCuil() : "");
            case "nombre" -> Comparator.comparing(p -> p.getNombre() != null ? p.getNombre() : "");
            case "apellido" -> Comparator.comparing(p -> p.getApellido() != null ? p.getApellido() : "");
            case "email" -> Comparator.comparing(p -> p.getEmail() != null ? p.getEmail() : "");
            case "id" -> Comparator.comparing(p -> p.getId() != null ? p.getId() : "");
            default -> Comparator.comparing(p -> p.getCuil() != null ? p.getCuil() : "");
        };
        
        if (order.getDirection() == Sort.Direction.DESC) {
            comparator = comparator.reversed();
        }
        
        return comparator;
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
