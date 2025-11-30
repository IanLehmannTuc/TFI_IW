package tfi.domain.valueObject;

import java.util.Objects;

public abstract class Frecuencia {
    protected final int value;

    public Frecuencia(int value) {
        if (value < 0) {
            throw notificarError();
        }
        this.value = value;
    }

    protected abstract RuntimeException notificarError();

    public abstract String getValorFormateado();

    public int getValor() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Frecuencia that = (Frecuencia) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return getValorFormateado();
    }
}
