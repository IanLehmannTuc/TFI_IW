package tfi.model.entity;

import tfi.model.enums.Autoridad;
import tfi.model.valueObjects.Email;

/**
 * Entidad que representa un usuario del sistema.
 * Contiene información de autenticación y autorización.
 * 
 * Siguiendo Clean Architecture:
 * - Email es un Value Object que encapsula validación
 * - passwordHash es String porque es el resultado de BCrypt (no tiene sentido como VO)
 * - Autoridad es un Enum del dominio
 */
public class Usuario {
    private Email email;
    private String passwordHash;
    private Autoridad autoridad;

    /**
     * Constructor completo para crear un usuario.
     * 
     * @param email Email del usuario como Value Object (debe ser válido y único)
     * @param passwordHash Contraseña hasheada con BCrypt
     * @param autoridad Rol/autoridad del usuario en el sistema
     */
    public Usuario(Email email, String passwordHash, Autoridad autoridad) {
        if (email == null) {
            throw new IllegalArgumentException("El email no puede ser nulo");
        }
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new IllegalArgumentException("El hash de contraseña no puede ser nulo o vacío");
        }
        if (autoridad == null) {
            throw new IllegalArgumentException("La autoridad no puede ser nula");
        }
        
        this.email = email;
        this.passwordHash = passwordHash;
        this.autoridad = autoridad;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        if (email == null) {
            throw new IllegalArgumentException("El email no puede ser nulo");
        }
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new IllegalArgumentException("El hash de contraseña no puede ser nulo o vacío");
        }
        this.passwordHash = passwordHash;
    }

    public Autoridad getAutoridad() {
        return autoridad;
    }

    public void setAutoridad(Autoridad autoridad) {
        if (autoridad == null) {
            throw new IllegalArgumentException("La autoridad no puede ser nula");
        }
        this.autoridad = autoridad;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "email=" + email +
                ", autoridad=" + autoridad +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return email.equals(usuario.email);
    }

    @Override
    public int hashCode() {
        return email.hashCode();
    }
}

