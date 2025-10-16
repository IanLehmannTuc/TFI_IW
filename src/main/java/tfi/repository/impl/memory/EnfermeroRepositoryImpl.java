package tfi.repository.impl.memory;

import tfi.model.entity.Enfermero;
import tfi.repository.interfaces.EnfermeroRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class EnfermeroRepositoryImpl implements EnfermeroRepository {

    private final Map<String, Enfermero> store;

    public EnfermeroRepositoryImpl() {
        this.store = new ConcurrentHashMap<>();
    }

    @Override
    public List<Enfermero> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Enfermero> findByCuil(String cuil) {
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
    public Enfermero add(Enfermero enfermero) {
        if (enfermero == null) {
            throw new IllegalArgumentException("El enfermero no puede ser nulo");
        }
        if (enfermero.getCuil() == null || enfermero.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del enfermero no puede ser nulo o vacío");
        }
        if (store.containsKey(enfermero.getCuil())) {
            throw new IllegalStateException("Ya existe un enfermero con el CUIL: " + enfermero.getCuil());
        }
        store.put(enfermero.getCuil(), enfermero);
        return enfermero;
    }

    @Override
    public Enfermero update(Enfermero enfermero) {
        if (enfermero == null) {
            throw new IllegalArgumentException("El enfermero no puede ser nulo");
        }
        if (enfermero.getCuil() == null || enfermero.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del enfermero no puede ser nulo o vacío");
        }
        if (!store.containsKey(enfermero.getCuil())) {
            throw new IllegalStateException("No existe un enfermero con el CUIL: " + enfermero.getCuil());
        }
        store.put(enfermero.getCuil(), enfermero);
        return enfermero;
    }

    @Override
    public Enfermero delete(Enfermero enfermero) {
        if (enfermero == null) {
            throw new IllegalArgumentException("El enfermero no puede ser nulo");
        }
        if (enfermero.getCuil() == null || enfermero.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del enfermero no puede ser nulo o vacío");
        }
        Enfermero removed = store.remove(enfermero.getCuil());
        if (removed == null) {
            throw new IllegalStateException("No existe un enfermero con el CUIL: " + enfermero.getCuil());
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
