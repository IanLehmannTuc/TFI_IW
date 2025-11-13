package tfi.model.dto;

import tfi.model.enums.Autoridad;

/**
 * DTO que representa un usuario autenticado extraído del token JWT.
 * Se utiliza en el contexto de seguridad para acceder a la información del usuario actual.
 */
public class UsuarioAutenticado {
    private String email;
    private Autoridad autoridad;

    /**
     * Constructor completo
     * 
     * @param email Email del usuario autenticado
     * @param autoridad Rol/autoridad del usuario
     */
    public UsuarioAutenticado(String email, Autoridad autoridad) {
        this.email = email;
        this.autoridad = autoridad;
    }

    public String getEmail() {
        return email;
    }

    public Autoridad getAutoridad() {
        return autoridad;
    }

    /**
     * Verifica si el usuario tiene una autoridad específica
     * 
     * @param autoridad La autoridad a verificar
     * @return true si el usuario tiene esa autoridad
     */
    public boolean hasAutoridad(Autoridad autoridad) {
        return this.autoridad == autoridad;
    }

    @Override
    public String toString() {
        return "UsuarioAutenticado{" +
                "email='" + email + '\'' +
                ", autoridad=" + autoridad +
                '}';
    }
}

