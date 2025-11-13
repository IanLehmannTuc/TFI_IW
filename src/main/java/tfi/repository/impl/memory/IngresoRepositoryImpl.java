package tfi.repository.impl.memory;

import org.springframework.stereotype.Repository;
import tfi.model.entity.Ingreso;
import tfi.repository.interfaces.IngresoRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class IngresoRepositoryImpl implements IngresoRepository {

    private final Map<String, Ingreso> store;
    private final AtomicLong idGenerator;

    public IngresoRepositoryImpl() {
        this.store = new ConcurrentHashMap<>();
        this.idGenerator = new AtomicLong(0);
    }

    @Override
    public List<Ingreso> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Ingreso add(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }
        
        if (ingreso.getFechaHoraIngreso() == null) {
            ingreso.setFechaHoraIngreso(LocalDateTime.now());
        }
        
        String ingresoId = String.valueOf(idGenerator.incrementAndGet());
        ingreso.setId(ingresoId);
        
        store.put(ingresoId, ingreso);
        
        return ingreso;
    }

    @Override
    public Ingreso update(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }
        
        if (ingreso.getId() == null || ingreso.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ingreso debe tener un ID");
        }
        
        if (!store.containsKey(ingreso.getId())) {
            throw new IllegalStateException("No existe un ingreso con el ID: " + ingreso.getId());
        }
        
        store.put(ingreso.getId(), ingreso);
        
        return ingreso;
    }

    @Override
    public Ingreso delete(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }
        
        if (ingreso.getId() == null || ingreso.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ingreso debe tener un ID");
        }
        
        Ingreso removed = store.remove(ingreso.getId());
        
        if (removed == null) {
            throw new IllegalStateException("No existe un ingreso con el ID: " + ingreso.getId());
        }
        
        return removed;
    }

    @Override
    public Optional<Ingreso> findById(String ingresoId) {
        if (ingresoId == null || ingresoId.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(store.get(ingresoId));
    }

    /**
     * Método auxiliar para buscar la clave de un ingreso por su valor (referencia).
     * @param ingreso el ingreso a buscar
     * @return el ID del ingreso, o null si no se encuentra
     */
    private String findKeyByValue(Ingreso ingreso) {
        for (Map.Entry<String, Ingreso> entry : store.entrySet()) {
            if (entry.getValue() == ingreso) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void clear() {
        store.clear();
        idGenerator.set(0);
    }

    public int count() {
        return store.size();
    }
}
