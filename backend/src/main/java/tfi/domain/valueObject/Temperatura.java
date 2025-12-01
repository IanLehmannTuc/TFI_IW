package tfi.domain.valueObject;

import java.util.Objects;

public final class Temperatura {
    private final Double valor;

    public Temperatura(Double valor) {
        if (valor == null) {
            throw new IllegalArgumentException("La temperatura no puede ser nula");
        }
        this.valor = valor;
    }

    public Double getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Temperatura)) return false;
        Temperatura that = (Temperatura) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return String.format("%.1f °C", valor);
    }
}
