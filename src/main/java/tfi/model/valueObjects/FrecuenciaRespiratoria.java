package tfi.model.valueObjects;

import java.util.Objects;

public final class FrecuenciaRespiratoria extends Frecuencia {

    public FrecuenciaRespiratoria(int value) {
        super(value);
    }

    @Override
    protected RuntimeException notificarError() {
        return new RuntimeException("La frecuencia respiratoria no puede ser negativa");
    }

    @Override
    public String getValorFormateado() {
        return String.format("%.1f rpm", this.value);
    }

    public int getValor() {
        return this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FrecuenciaRespiratoria)) return false;
        FrecuenciaRespiratoria that = (FrecuenciaRespiratoria) o;
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
