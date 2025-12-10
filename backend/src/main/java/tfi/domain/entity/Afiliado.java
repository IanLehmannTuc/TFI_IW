package tfi.domain.entity;

import java.util.Objects;

/**
 * Value Object que representa la afiliación de un paciente a una obra social.
 * Es inmutable - una vez creado, no puede modificarse.
 * 
 * Siguiendo principios DDD:
 * - Inmutabilidad garantizada (campos final)
 * - Validación en el constructor
 * - Igualdad por valor (equals/hashCode basados en valores)
 */
public class Afiliado {
    private final ObraSocial obraSocial;
    private final String numeroAfiliado;

    /**
     * Constructor para crear una afiliación.
     * 
     * @param obraSocial Obra social a la que está afiliado (no puede ser null)
     * @param numeroAfiliado Número de afiliado (no puede ser null o vacío)
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Afiliado(ObraSocial obraSocial, String numeroAfiliado) {
        if (obraSocial == null) {
            throw new IllegalArgumentException("La obra social no puede ser nula");
        }
        if (numeroAfiliado == null || numeroAfiliado.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de afiliado es obligatorio y no puede estar vacío");
        }
        this.obraSocial = obraSocial;
        this.numeroAfiliado = numeroAfiliado.trim();
    }

    public ObraSocial getObraSocial() {
        return obraSocial;
    }

    public String getNumeroAfiliado() {
        return numeroAfiliado;
    }

    /**
     * Método de consulta: Verifica si la afiliación es válida.
     * Una afiliación es válida si tiene obra social y número de afiliado.
     * 
     * @return true siempre, ya que la validación está en el constructor
     */
    public boolean esValida() {
        return obraSocial != null && numeroAfiliado != null && !numeroAfiliado.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Afiliado afiliado = (Afiliado) o;
        return Objects.equals(obraSocial, afiliado.obraSocial) &&
               Objects.equals(numeroAfiliado, afiliado.numeroAfiliado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(obraSocial, numeroAfiliado);
    }

    @Override
    public String toString() {
        return "Afiliado{" +
                "obraSocial=" + obraSocial +
                ", numeroAfiliado='" + numeroAfiliado + '\'' +
                '}';
    }
}
