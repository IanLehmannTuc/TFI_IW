package tfi.model.valueObjects;

import java.util.Objects;

public final class Domicilio {
    private final String calle;
    private final String numero;
    private final String ciudad;
    private final String pais;

    public Domicilio(String calle, String numero, String ciudad, String pais) {
        if (calle == null || calle.trim().isEmpty()) {
            throw new IllegalArgumentException("La calle no puede ser nula o vacía");
        }
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("El número no puede ser nulo o vacío");
        }
        if (ciudad == null || ciudad.trim().isEmpty()) {
            throw new IllegalArgumentException("La ciudad no puede ser nula o vacía");
        }
        if (pais == null || pais.trim().isEmpty()) {
            throw new IllegalArgumentException("El país no puede ser nulo o vacío");
        }
        this.calle = calle.trim();
        this.numero = numero.trim();
        this.ciudad = ciudad.trim();
        this.pais = pais.trim();
    }

    public String getCalle() {
        return calle;
    }

    public String getNumero() {
        return numero;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getPais() {
        return pais;
    }

    public String getDireccionCompleta() {
        return String.format("%s %s, %s, %s", calle, numero, ciudad, pais);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Domicilio)) return false;
        Domicilio domicilio = (Domicilio) o;
        return Objects.equals(calle, domicilio.calle) &&
               Objects.equals(numero, domicilio.numero) &&
               Objects.equals(ciudad, domicilio.ciudad) &&
               Objects.equals(pais, domicilio.pais);
    }

    @Override
    public int hashCode() {
        return Objects.hash(calle, numero, ciudad, pais);
    }

    @Override
    public String toString() {
        return getDireccionCompleta();
    }
}
