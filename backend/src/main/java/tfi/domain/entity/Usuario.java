package tfi.domain.entity;

import tfi.domain.enums.Autoridad;
import tfi.domain.valueObject.Cuil;
import tfi.domain.valueObject.Email;

/**
 * Entidad que representa un usuario del sistema (médico o enfermero).
 * Unifica la información de autenticación y datos personales del personal médico.
 * 
 * Siguiendo Clean Architecture:
 * - Email y Cuil son Value Objects que encapsulan validación
 * - passwordHash es String porque es el resultado de BCrypt (no tiene sentido como VO)
 * - Autoridad es un Enum del dominio (MEDICO o ENFERMERO)
 */
public class Usuario {
    private String id;
    private Email email;
    private String passwordHash;
    private Autoridad autoridad;
    
    // Datos personales del personal médico
    private Cuil cuil;
    private String nombre;
    private String apellido;
    private String matricula;

    /**
     * Constructor completo para crear un usuario con todos los datos.
     * 
     * @param email Email del usuario como Value Object (debe ser válido y único)
     * @param passwordHash Contraseña hasheada con BCrypt
     * @param autoridad Rol/autoridad del usuario en el sistema (MEDICO o ENFERMERO)
     * @param cuil CUIL del usuario como Value Object
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param matricula Matrícula profesional del usuario
     */
    public Usuario(Email email, String passwordHash, Autoridad autoridad, Cuil cuil, 
                   String nombre, String apellido, String matricula) {
        if (email == null) {
            throw new IllegalArgumentException("El email no puede ser nulo");
        }
        if (passwordHash == null || passwordHash.isEmpty()) {
            throw new IllegalArgumentException("El hash de contraseña no puede ser nulo o vacío");
        }
        if (autoridad == null) {
            throw new IllegalArgumentException("La autoridad no puede ser nula");
        }
        if (cuil == null) {
            throw new IllegalArgumentException("El CUIL no puede ser nulo");
        }
        if (matricula == null || matricula.isEmpty()) {
            throw new IllegalArgumentException("La matrícula no puede ser nula o vacía");
        }
        
        this.email = email;
        this.passwordHash = passwordHash;
        this.autoridad = autoridad;
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.matricula = matricula;
    }
    
    /**
     * Constructor básico para autenticación (sin datos personales completos).
     * Útil para casos donde solo se necesita autenticación.
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
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    public Email getEmailVO() {
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
    
    public Cuil getCuilVO() {
        return cuil;
    }
    
    public String getCuil() {
        return cuil != null ? cuil.getValor() : null;
    }

    public void setCuil(Cuil cuil) {
        if (cuil == null) {
            throw new IllegalArgumentException("El CUIL no puede ser nulo");
        }
        this.cuil = cuil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        if (matricula == null || matricula.isEmpty()) {
            throw new IllegalArgumentException("La matrícula no puede ser nula o vacía");
        }
        this.matricula = matricula;
    }
    
    /**
     * Helper method para obtener el nombre completo del usuario.
     * 
     * @return Nombre completo en formato "Apellido, Nombre"
     */
    public String getNombreCompleto() {
        if (nombre != null && apellido != null) {
            return apellido + ", " + nombre;
        } else if (nombre != null) {
            return nombre;
        } else if (apellido != null) {
            return apellido;
        }
        return email.getValue();
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "email=" + email +
                ", autoridad=" + autoridad +
                ", cuil=" + (cuil != null ? cuil.getValor() : "null") +
                ", nombreCompleto='" + getNombreCompleto() + '\'' +
                ", matricula='" + matricula + '\'' +
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

