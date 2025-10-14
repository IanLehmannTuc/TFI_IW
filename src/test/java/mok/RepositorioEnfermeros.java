package mok;

import model.Enfermero;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RepositorioEnfermeros {

    private final Map<String, Enfermero> store;

    public RepositorioEnfermeros() {
        this.store = new ConcurrentHashMap<>();
    }

    public List<Enfermero> findAll() {
        return store.values().stream().collect(Collectors.toList());
    }

    public Enfermero findByCuil(Enfermero enfermero) {
        return store.get(enfermero.getCuil());
    }

    public void add(Enfermero enfermero) {
        store.put(enfermero.getCuil(), enfermero);
    }

    public Enfermero update(Enfermero enfermero) {
        if(enfermero.getCuil().equals(store.get(enfermero.getCuil()).getCuil())) {
            store.replace(enfermero.getCuil(), enfermero);
            return enfermero;
        }
        else{
            return null;
        }
    }

    public Enfermero delete(Enfermero enfermero) {
        if(enfermero.getCuil().equals(store.get(enfermero.getCuil()).getCuil())) {
            store.remove(enfermero.getCuil());
            return enfermero;
        }
        else{
            return null;
        }
    }
}
