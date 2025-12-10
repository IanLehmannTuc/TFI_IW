package tfi.domain.valueObject;

import java.util.Objects;

/**
 * Value Object que representa una contraseña válida.
 * Encapsula las reglas de validación de contraseñas.
 */
public class Password {
    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128; 

    private final String value;

    /**
     * Constructor privado. Usar el método estático from() para crear instancias.
     * 
     * @param value El valor de la contraseña
     */
    private Password(String value) {
        this.value = value;
    }

    /**
     * Crea una instancia de Password a partir de un String.
     * 
     * @param password La contraseña a validar
     * @return Una instancia de Password si es válida
     * @throws IllegalArgumentException Si la contraseña no cumple los requisitos
     */
    public static Password from(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }

        if (password.length() < MIN_LENGTH) {
            throw new IllegalArgumentException(
                "La contraseña debe tener al menos " + MIN_LENGTH + " caracteres"
            );
        }

        if (password.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                "La contraseña no puede tener más de " + MAX_LENGTH + " caracteres"
            );
        }

        return new Password(password);
    }

    /**
     * Valida si un string cumple con los requisitos de contraseña sin lanzar excepción.
     * 
     * @param password La contraseña a validar
     * @return true si es válida, false en caso contrario
     */
    public static boolean isValid(String password) {
        return password != null && password.length() >= MIN_LENGTH;
    }

    /**
     * Obtiene la longitud mínima requerida para contraseñas.
     * 
     * @return La longitud mínima
     */
    public static int getMinLength() {
        return MIN_LENGTH;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "***********"; 
    }
}

