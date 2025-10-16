package tfi.model.valueObjects;

import java.util.Objects;

public final class FrecuenciaCardiaca extends Frecuencia {

    public FrecuenciaCardiaca(int value) {
        super(value);
    }

    @Override
    protected RuntimeException notificarError() {
        return new RuntimeException("La frecuencia cardíaca no puede ser negativa");
    }

    @Override
    public String getValorFormateado() {
        return String.format("%.1f lpm", this.value);
    }

    public int getValor() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrecuenciaCardiaca)) return false;
        FrecuenciaCardiaca that = (FrecuenciaCardiaca) o;
        return Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

    @Override
    public String toString() {
        return getValorFormateado();
    }
}
