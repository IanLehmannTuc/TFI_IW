package tfi.infrastructure.persistence.repository.memory;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.Atencion;
import tfi.domain.repository.AtencionRepository;

import java.util.*;

/**
 * Implementación en memoria del repositorio de atenciones.
 * Utiliza un HashMap para almacenar las atenciones.
 * Activo solo cuando el perfil "memory" está habilitado.
 */
@Repository
@Profile("memory")
public class AtencionRepositoryImpl implements AtencionRepository {

    private final Map<String, Atencion> atenciones = new HashMap<>();

    @Override
    public Optional<Atencion> findById(String id) {
        return Optional.ofNullable(atenciones.get(id));
    }

    @Override
    public Optional<Atencion> findByIngresoId(String ingresoId) {
        return atenciones.values().stream()
                .filter(atencion -> atencion.getIngresoId().equals(ingresoId))
                .findFirst();
    }

    @Override
    public Atencion add(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("La atención no puede ser nula");
        }


        if (findByIngresoId(atencion.getIngresoId()).isPresent()) {
            throw new IllegalStateException("Ya existe una atención registrada para este ingreso");
        }


        if (atencion.getId() == null) {
            atencion.setId(UUID.randomUUID().toString());
        }

        atenciones.put(atencion.getId(), atencion);
        return atencion;
    }

    @Override
    public Atencion update(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("La atención no puede ser nula");
        }

        if (atencion.getId() == null) {
            throw new IllegalArgumentException("La atención debe tener un ID para ser actualizada");
        }

        if (!atenciones.containsKey(atencion.getId())) {
            throw new IllegalArgumentException("No se encontró la atención con ID: " + atencion.getId());
        }

        atenciones.put(atencion.getId(), atencion);
        return atencion;
    }

    @Override
    public List<Atencion> findAll() {
        return new ArrayList<>(atenciones.values());
    }

    @Override
    public Atencion delete(Atencion atencion) {
        if (atencion == null || atencion.getId() == null) {
            throw new IllegalArgumentException("La atención debe tener un ID válido para ser eliminada");
        }

        Atencion atencionEliminada = atenciones.remove(atencion.getId());

        if (atencionEliminada == null) {
            throw new IllegalArgumentException("No se encontró la atención con ID: " + atencion.getId());
        }

        return atencionEliminada;
    }
}
