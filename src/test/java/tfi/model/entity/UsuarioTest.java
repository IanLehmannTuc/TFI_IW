package tfi.model.entity;

import org.junit.jupiter.api.Test;
import tfi.model.enums.Autoridad;
import tfi.model.valueObjects.Email;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para la entidad Usuario.
 * Verifica validaciones defensivas y comportamiento de la entidad.
 */
class UsuarioTest {

    @Test
    void debeCrearUsuarioConDatosValidos() {
        Email email = Email.from("medico@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        Autoridad autoridad = Autoridad.MEDICO;
        
        Usuario usuario = new Usuario(email, passwordHash, autoridad);
        
        assertNotNull(usuario);
        assertEquals(email, usuario.getEmail());
        assertEquals(passwordHash, usuario.getPasswordHash());
        assertEquals(autoridad, usuario.getAutoridad());
    }

    @Test
    void debeLanzarExcepcionSiEmailEsNull() {
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
 & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(null, passwordHash, Autoridad.MEDICO)
        );
        
        assertEquals("El email no puede ser nulo", exception.getMessage());
    }

    @Test
    void debeLanzarExcepcionSiPasswordHashEsNull() {
        Email email = Email.from("medico@hospital.com");
        
 & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(email, null, Autoridad.MEDICO)
        );
        
        assertEquals("El hash de contraseña no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void debeLanzarExcepcionSiPasswordHashEsVacio() {
        Email email = Email.from("medico@hospital.com");
        
 & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(email, "", Autoridad.MEDICO)
        );
    }

    @Test
    void debeLanzarExcepcionSiAutoridadEsNull() {
        Email email = Email.from("medico@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
 & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(email, passwordHash, null)
        );
        
        assertEquals("La autoridad no puede ser nula", exception.getMessage());
    }

    @Test
    void debeCrearUsuarioConAutoridadMedico() {
        Email email = Email.from("medico@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
        Usuario usuario = new Usuario(email, passwordHash, Autoridad.MEDICO);
        
        assertEquals(Autoridad.MEDICO, usuario.getAutoridad());
    }

    @Test
    void debeCrearUsuarioConAutoridadEnfermera() {
        Email email = Email.from("enfermera@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
        Usuario usuario = new Usuario(email, passwordHash, Autoridad.ENFERMERA);
        
        assertEquals(Autoridad.ENFERMERA, usuario.getAutoridad());
    }

    @Test
    void debePermitirActualizarEmail() {
        Email emailOriginal = Email.from("medico@hospital.com");
        Usuario usuario = new Usuario(emailOriginal, "hash", Autoridad.MEDICO);
        Email nuevoEmail = Email.from("nuevo@hospital.com");
        
        usuario.setEmail(nuevoEmail);
        
        assertEquals(nuevoEmail, usuario.getEmail());
    }

    @Test
    void setEmailDebeLanzarExcepcionSiEsNull() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
 & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setEmail(null)
        );
    }

    @Test
    void debePermitirActualizarPasswordHash() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hashOriginal", 
            Autoridad.MEDICO
        );
        
        usuario.setPasswordHash("nuevoHash");
        
        assertEquals("nuevoHash", usuario.getPasswordHash());
    }

    @Test
    void setPasswordHashDebeLanzarExcepcionSiEsNull() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
 & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setPasswordHash(null)
        );
    }

    @Test
    void setPasswordHashDebeLanzarExcepcionSiEsVacio() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
 & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setPasswordHash("")
        );
    }

    @Test
    void debePermitirActualizarAutoridad() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
        usuario.setAutoridad(Autoridad.ENFERMERA);
        
        assertEquals(Autoridad.ENFERMERA, usuario.getAutoridad());
    }

    @Test
    void setAutoridadDebeLanzarExcepcionSiEsNull() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
 & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setAutoridad(null)
        );
    }

    @Test
    void dosUsuariosConMismoEmailDebenSerIguales() {
        Email email = Email.from("medico@hospital.com");
        Usuario usuario1 = new Usuario(email, "hash1", Autoridad.MEDICO);
        Usuario usuario2 = new Usuario(email, "hash2", Autoridad.ENFERMERA);
        
 & Assert
        assertEquals(usuario1, usuario2);
        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }

    @Test
    void dosUsuariosConDiferenteEmailNoDebenSerIguales() {
        Usuario usuario1 = new Usuario(
            Email.from("medico1@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        Usuario usuario2 = new Usuario(
            Email.from("medico2@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
 & Assert
        assertNotEquals(usuario1, usuario2);
    }

    @Test
    void toStringNoDebeRevelarPasswordHash() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "secretHash", 
            Autoridad.MEDICO
        );
        
        String resultado = usuario.toString();
        
        assertFalse(resultado.contains("secretHash"));
        assertTrue(resultado.contains("medico@hospital.com"));
        assertTrue(resultado.contains("MEDICO"));
    }
}

