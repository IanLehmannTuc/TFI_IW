package tfi.domain.valueObject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa un email válido.
 * Encapsula las reglas de validación de formato de email.
 */
public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    private final String value;

    /**
     * Constructor privado. Usar el método estático from() para crear instancias.
     * 
     * @param value El valor del email
     */
    private Email(String value) {
        this.value = value;
    }

    /**
     * Crea una instancia de Email a partir de un String.
     * 
     * @param email El email a validar
     * @return Una instancia de Email si es válido
     * @throws IllegalArgumentException Si el email no es válido
     */
    public static Email from(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        
        String trimmedEmail = email.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }
        
        return new Email(trimmedEmail);
    }

    /**
     * Valida si un string tiene formato de email válido sin lanzar excepción.
     * 
     * @param email El email a validar
     * @return true si es válido, false en caso contrario
     */
    public static boolean isValid(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
