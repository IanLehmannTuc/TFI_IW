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
        // Arrange
        Email email = Email.from("medico@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        Autoridad autoridad = Autoridad.MEDICO;
        
        // Act
        Usuario usuario = new Usuario(email, passwordHash, autoridad);
        
        // Assert
        assertNotNull(usuario);
        assertEquals(email, usuario.getEmail());
        assertEquals(passwordHash, usuario.getPasswordHash());
        assertEquals(autoridad, usuario.getAutoridad());
    }

    @Test
    void debeLanzarExcepcionSiEmailEsNull() {
        // Arrange
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(null, passwordHash, Autoridad.MEDICO)
        );
        
        assertEquals("El email no puede ser nulo", exception.getMessage());
    }

    @Test
    void debeLanzarExcepcionSiPasswordHashEsNull() {
        // Arrange
        Email email = Email.from("medico@hospital.com");
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(email, null, Autoridad.MEDICO)
        );
        
        assertEquals("El hash de contraseña no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void debeLanzarExcepcionSiPasswordHashEsVacio() {
        // Arrange
        Email email = Email.from("medico@hospital.com");
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(email, "", Autoridad.MEDICO)
        );
    }

    @Test
    void debeLanzarExcepcionSiAutoridadEsNull() {
        // Arrange
        Email email = Email.from("medico@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> new Usuario(email, passwordHash, null)
        );
        
        assertEquals("La autoridad no puede ser nula", exception.getMessage());
    }

    @Test
    void debeCrearUsuarioConAutoridadMedico() {
        // Arrange
        Email email = Email.from("medico@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
        // Act
        Usuario usuario = new Usuario(email, passwordHash, Autoridad.MEDICO);
        
        // Assert
        assertEquals(Autoridad.MEDICO, usuario.getAutoridad());
    }

    @Test
    void debeCrearUsuarioConAutoridadEnfermera() {
        // Arrange
        Email email = Email.from("enfermera@hospital.com");
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuvwxyz";
        
        // Act
        Usuario usuario = new Usuario(email, passwordHash, Autoridad.ENFERMERA);
        
        // Assert
        assertEquals(Autoridad.ENFERMERA, usuario.getAutoridad());
    }

    @Test
    void debePermitirActualizarEmail() {
        // Arrange
        Email emailOriginal = Email.from("medico@hospital.com");
        Usuario usuario = new Usuario(emailOriginal, "hash", Autoridad.MEDICO);
        Email nuevoEmail = Email.from("nuevo@hospital.com");
        
        // Act
        usuario.setEmail(nuevoEmail);
        
        // Assert
        assertEquals(nuevoEmail, usuario.getEmail());
    }

    @Test
    void setEmailDebeLanzarExcepcionSiEsNull() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setEmail(null)
        );
    }

    @Test
    void debePermitirActualizarPasswordHash() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hashOriginal", 
            Autoridad.MEDICO
        );
        
        // Act
        usuario.setPasswordHash("nuevoHash");
        
        // Assert
        assertEquals("nuevoHash", usuario.getPasswordHash());
    }

    @Test
    void setPasswordHashDebeLanzarExcepcionSiEsNull() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setPasswordHash(null)
        );
    }

    @Test
    void setPasswordHashDebeLanzarExcepcionSiEsVacio() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setPasswordHash("")
        );
    }

    @Test
    void debePermitirActualizarAutoridad() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
        // Act
        usuario.setAutoridad(Autoridad.ENFERMERA);
        
        // Assert
        assertEquals(Autoridad.ENFERMERA, usuario.getAutoridad());
    }

    @Test
    void setAutoridadDebeLanzarExcepcionSiEsNull() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "hash", 
            Autoridad.MEDICO
        );
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> usuario.setAutoridad(null)
        );
    }

    @Test
    void dosUsuariosConMismoEmailDebenSerIguales() {
        // Arrange
        Email email = Email.from("medico@hospital.com");
        Usuario usuario1 = new Usuario(email, "hash1", Autoridad.MEDICO);
        Usuario usuario2 = new Usuario(email, "hash2", Autoridad.ENFERMERA);
        
        // Act & Assert
        assertEquals(usuario1, usuario2);
        assertEquals(usuario1.hashCode(), usuario2.hashCode());
    }

    @Test
    void dosUsuariosConDiferenteEmailNoDebenSerIguales() {
        // Arrange
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
        
        // Act & Assert
        assertNotEquals(usuario1, usuario2);
    }

    @Test
    void toStringNoDebeRevelarPasswordHash() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"), 
            "secretHash", 
            Autoridad.MEDICO
        );
        
        // Act
        String resultado = usuario.toString();
        
        // Assert
        assertFalse(resultado.contains("secretHash"));
        assertTrue(resultado.contains("medico@hospital.com"));
        assertTrue(resultado.contains("MEDICO"));
    }
}

