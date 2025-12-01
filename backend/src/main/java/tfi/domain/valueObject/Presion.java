package tfi.domain.valueObject;

import java.util.Objects;

/**
 * Value Object que representa una presión arterial (sistólica o diastólica).
 * Es inmutable y garantiza que el valor esté dentro de rangos médicamente válidos.
 */
public final class Presion {
    private final Integer valor;

    /**
     * Constructor que crea una nueva Presion.
     * 
     * @param valor el valor de la presión en mmHg (milímetros de mercurio)
     * @throws IllegalArgumentException si el valor es nulo o está fuera del rango válido
     */
    public Presion(Integer valor) {
        if (valor == null) {
            throw new IllegalArgumentException("La presión no puede ser nula");
        }
        if (valor < 0) {
            throw new IllegalArgumentException("La presión no puede ser negativa");
        }
        if (valor > 300) {
            throw new IllegalArgumentException("La presión no puede ser mayor a 300 mmHg");
        }
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Presion)) return false;
        Presion presion = (Presion) o;
        return Objects.equals(valor, presion.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return String.format("%d mmHg", valor);
    }
}

