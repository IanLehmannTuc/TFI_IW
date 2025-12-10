package tfi.domain.valueObject;

import java.util.Objects;

/**
 * Value Object que representa un número de teléfono.
 */
public class Telefono {
    private final String valor;

    /**
     * Constructor privado para crear una instancia de Telefono.
     *
     * @param valor el valor del teléfono
     */
    private Telefono(String valor) {
        this.valor = valor;
    }

    /**
     * Crea una instancia de Telefono a partir de un String.
     *
     * @param telefono el número de teléfono como String
     * @return una instancia de Telefono
     * @throws IllegalArgumentException si el teléfono no es válido
     */
    public static Telefono from(String telefono) {
        validar(telefono);


        String normalizado = telefono.trim().replaceAll("\\s+", " ");

        return new Telefono(normalizado);
    }

    /**
     * Valida que el teléfono cumpla con los requisitos establecidos.
     *
     * @param telefono el teléfono a validar
     * @throws IllegalArgumentException si el teléfono no es válido
     */
    private static void validar(String telefono) {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El teléfono no puede ser nulo o vacío");
        }

        String limpio = telefono.trim();


        if (limpio.length() < 6) {
            throw new IllegalArgumentException(
                "El teléfono es demasiado corto. Mínimo 6 caracteres"
            );
        }

        if (limpio.length() > 30) {
            throw new IllegalArgumentException(
                "El teléfono es demasiado largo. Máximo 30 caracteres"
            );
        }


        if (!limpio.matches(".*\\d.*")) {
            throw new IllegalArgumentException(
                "El teléfono debe contener al menos un dígito"
            );
        }


        if (!limpio.matches("[0-9\\s()\\-+]+")) {
            throw new IllegalArgumentException(
                "El teléfono contiene caracteres inválidos. Solo se permiten: números, espacios, (), -, +"
            );
        }
    }

    /**
     * Obtiene el valor completo del teléfono.
     *
     * @return el valor del teléfono
     */
    public String getValor() {
        return valor;
    }

    /**
     * Obtiene solo los dígitos del teléfono, sin espacios ni caracteres especiales.
     *
     * @return los dígitos del teléfono
     */
    public String getSoloDigitos() {
        return valor.replaceAll("[^0-9]", "");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Telefono telefono = (Telefono) o;

        return getSoloDigitos().equals(telefono.getSoloDigitos());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSoloDigitos());
    }

    @Override
    public String toString() {
        return valor;
    }
}

