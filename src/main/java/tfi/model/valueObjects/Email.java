package tfi.model.valueObjects;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa un Email.
 * Es inmutable y garantiza que el formato sea válido.
 */
public final class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    private final String valor;

    /**
     * Constructor que crea un nuevo Email.
     * 
     * @param valor el email en formato válido (ejemplo: usuario@dominio.com)
     * @throws IllegalArgumentException si el valor es nulo, vacío o tiene formato inválido
     */
    public Email(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }
        
        String emailLimpio = valor.trim().toLowerCase();
        
        if (!EMAIL_PATTERN.matcher(emailLimpio).matches()) {
            throw new IllegalArgumentException(
                "El email debe tener un formato válido (ejemplo: usuario@dominio.com)"
            );
        }
        
        this.valor = emailLimpio;
    }

    public String getValor() {
        return valor;
    }

    /**
     * Retorna el usuario del email (parte antes del @).
     * 
     * @return el usuario (ejemplo: para "usuario@dominio.com" retorna "usuario")
     */
    public String getUsuario() {
        return valor.substring(0, valor.indexOf('@'));
    }

    /**
     * Retorna el dominio del email (parte después del @).
     * 
     * @return el dominio (ejemplo: para "usuario@dominio.com" retorna "dominio.com")
     */
    public String getDominio() {
        return valor.substring(valor.indexOf('@') + 1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(valor, email.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}

