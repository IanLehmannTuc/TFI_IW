package tfi.domain.entity;

import java.util.Objects;

/**
 * Entidad que representa una obra social.
 * Es una entidad de referencia que puede ser compartida por múltiples pacientes.
 */
public class ObraSocial {
    private final int id;
    private final String nombre;

    /**
     * Constructor para crear una obra social.
     * 
     * @param id ID único de la obra social (debe ser mayor a 0)
     * @param nombre Nombre de la obra social (no puede ser null o vacío)
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public ObraSocial(int id, String nombre) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la obra social debe ser mayor a 0");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la obra social es obligatorio");
        }
        this.id = id;
        this.nombre = nombre.trim();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObraSocial that = (ObraSocial) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ObraSocial{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
