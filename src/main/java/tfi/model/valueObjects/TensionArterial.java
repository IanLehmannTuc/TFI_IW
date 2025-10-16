package tfi.model.valueObjects;

import java.util.Objects;

public final class TensionArterial {
    private final Presion presionSistolica;
    private final Presion presionDiastolica;

    public TensionArterial(Presion presionSistolica, Presion presionDiastolica) {
        if (presionSistolica == null || presionDiastolica == null) {
            throw new IllegalArgumentException("Las presiones no pueden ser nulas");
        }
        this.presionSistolica = presionSistolica;
        this.presionDiastolica = presionDiastolica;
    }

    public Presion getPresionSistolica() {
        return presionSistolica;
    }

    public Presion getPresionDiastolica() {
        return presionDiastolica;
    }

    public String getValorFormateado() {
        return String.format("%s/%s", 
            presionSistolica.getValor(), 
            presionDiastolica.getValor());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TensionArterial)) return false;
        TensionArterial that = (TensionArterial) o;
        return Objects.equals(presionSistolica, that.presionSistolica) && 
               Objects.equals(presionDiastolica, that.presionDiastolica);
    }

    @Override
    public int hashCode() {
        return Objects.hash(presionSistolica, presionDiastolica);
    }

    @Override
    public String toString() {
        return getValorFormateado();
    }
}
