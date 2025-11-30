package tfi.domain.valueObject;

import java.util.Objects;

public final class Domicilio {
    private final String calle;
    private final int numero;
    private final String localidad;

    public Domicilio(String calle, int numero, String localidad) {
        if (calle == null || calle.trim().isEmpty()) {
            throw new IllegalArgumentException("La calle no puede ser nula o vacía");
        }
        if (numero <= 0) {
            throw new IllegalArgumentException("El número debe ser mayor a cero");
        }
        if (localidad == null || localidad.trim().isEmpty()) {
            throw new IllegalArgumentException("La localidad no puede ser nula o vacía");
        }
        this.calle = calle.trim();
        this.numero = numero;
        this.localidad = localidad.trim();
    }

    public String getCalle() {
        return calle;
    }

    public int getNumero() {
        return numero;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getDireccionCompleta() {
        return String.format("%s %d, %s", calle, numero, localidad);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Domicilio)) return false;
        Domicilio domicilio = (Domicilio) o;
        return numero == domicilio.numero &&
               Objects.equals(calle, domicilio.calle) &&
               Objects.equals(localidad, domicilio.localidad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, numero, localidad);
    }

    @Override
    public String toString() {
        return getDireccionCompleta();
    }
}
