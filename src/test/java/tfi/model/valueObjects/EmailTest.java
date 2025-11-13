package tfi.model.valueObjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el Value Object Email.
 * Verifica validación de formato y comportamiento del VO.
 */
class EmailTest {

    @Test
    void debeCrearEmailValido() {
        // Arrange & Act
        Email email = Email.from("usuario@example.com");
        
        // Assert
        assertNotNull(email);
        assertEquals("usuario@example.com", email.getValue());
    }

    @Test
    void debeNormalizarEmailAMinusculas() {
        // Arrange & Act
        Email email = Email.from("USUARIO@EXAMPLE.COM");
        
        // Assert
        assertEquals("usuario@example.com", email.getValue());
    }

    @Test
    void debeEliminarEspaciosEnBlanco() {
        // Arrange & Act
        Email email = Email.from("  usuario@example.com  ");
        
        // Assert
        assertEquals("usuario@example.com", email.getValue());
    }

    @Test
    void debeAceptarEmailsConCaracteresEspeciales() {
        // Arrange & Act
        Email email1 = Email.from("user+test@example.com");
        Email email2 = Email.from("user.name@example.com");
        Email email3 = Email.from("user_name@example.com");
        
        // Assert
        assertNotNull(email1);
        assertNotNull(email2);
        assertNotNull(email3);
    }

    @Test
    void debeRechazarEmailSinArroba() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("usuarioexample.com")
        );
        
        assertEquals("El email no tiene un formato válido", exception.getMessage());
    }

    @Test
    void debeRechazarEmailSinDominio() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("usuario@")
        );
    }

    @Test
    void debeRechazarEmailSinExtension() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("usuario@example")
        );
    }

    @Test
    void debeRechazarEmailVacio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("")
        );
        
        assertEquals("El email no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void debeRechazarEmailNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.from(null)
        );
        
        assertEquals("El email no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void debeRechazarEmailSoloEspacios() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("   ")
        );
    }

    @Test
    void isValidDebeRetornarTrueParaEmailValido() {
        // Act & Assert
        assertTrue(Email.isValid("usuario@example.com"));
    }

    @Test
    void isValidDebeRetornarFalseParaEmailInvalido() {
        // Act & Assert
        assertFalse(Email.isValid("usuarioexample.com"));
        assertFalse(Email.isValid("usuario@"));
        assertFalse(Email.isValid("@example.com"));
        assertFalse(Email.isValid(""));
        assertFalse(Email.isValid(null));
    }

    @Test
    void dosEmailsConMismoValorDebenSerIguales() {
        // Arrange
        Email email1 = Email.from("usuario@example.com");
        Email email2 = Email.from("usuario@example.com");
        
        // Act & Assert
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void dosEmailsConDiferenteValorNoDebenSerIguales() {
        // Arrange
        Email email1 = Email.from("usuario1@example.com");
        Email email2 = Email.from("usuario2@example.com");
        
        // Act & Assert
        assertNotEquals(email1, email2);
    }

    @Test
    void toStringDebeRetornarElEmail() {
        // Arrange
        Email email = Email.from("usuario@example.com");
        
        // Act & Assert
        assertEquals("usuario@example.com", email.toString());
    }

    @Test
    void emailEsInmutable() {
        // Arrange
        Email email = Email.from("usuario@example.com");
        String valorOriginal = email.getValue();
        
        // Act
        // No hay setters, el objeto es inmutable
        
        // Assert
        assertEquals(valorOriginal, email.getValue());
    }
}

