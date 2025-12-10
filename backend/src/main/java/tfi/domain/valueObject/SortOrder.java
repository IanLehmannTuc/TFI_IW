package tfi.domain.valueObject;

/**
 * Value Object que representa un criterio de ordenamiento.
 * Es independiente de frameworks externos.
 */
public class SortOrder {
    private final String property;
    private final Direction direction;

    /**
     * Enum que representa la dirección del ordenamiento.
     */
    public enum Direction {
        ASC, DESC
    }

    /**
     * Constructor para crear un criterio de ordenamiento.
     * 
     * @param property Nombre de la propiedad por la cual ordenar
     * @param direction Dirección del ordenamiento (ASC o DESC)
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public SortOrder(String property, Direction direction) {
        if (property == null || property.trim().isEmpty()) {
            throw new IllegalArgumentException("La propiedad de ordenamiento no puede ser nula o vacía");
        }
        if (direction == null) {
            throw new IllegalArgumentException("La dirección de ordenamiento no puede ser nula");
        }
        this.property = property.trim();
        this.direction = direction;
    }

    /**
     * Constructor con dirección ASC por defecto.
     * 
     * @param property Nombre de la propiedad por la cual ordenar
     */
    public SortOrder(String property) {
        this(property, Direction.ASC);
    }

    public String getProperty() {
        return property;
    }

    public Direction getDirection() {
        return direction;
    }

    /**
     * Verifica si el ordenamiento es ascendente.
     * 
     * @return true si es ASC, false si es DESC
     */
    public boolean isAscending() {
        return direction == Direction.ASC;
    }

    /**
     * Verifica si el ordenamiento es descendente.
     * 
     * @return true si es DESC, false si es ASC
     */
    public boolean isDescending() {
        return direction == Direction.DESC;
    }

    @Override
    public String toString() {
        return property + " " + direction;
    }
}

