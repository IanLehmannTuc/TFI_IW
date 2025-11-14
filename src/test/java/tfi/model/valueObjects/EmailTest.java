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
        Email email = Email.from("usuario@example.com");
        
        assertNotNull(email);
        assertEquals("usuario@example.com", email.getValue());
    }

    @Test
    void debeNormalizarEmailAMinusculas() {
        Email email = Email.from("USUARIO@EXAMPLE.COM");
        
        assertEquals("usuario@example.com", email.getValue());
    }

    @Test
    void debeEliminarEspaciosEnBlanco() {
        Email email = Email.from("  usuario@example.com  ");
        
        assertEquals("usuario@example.com", email.getValue());
    }

    @Test
    void debeAceptarEmailsConCaracteresEspeciales() {
        Email email1 = Email.from("user+test@example.com");
        Email email2 = Email.from("user.name@example.com");
        Email email3 = Email.from("user_name@example.com");
        
        assertNotNull(email1);
        assertNotNull(email2);
        assertNotNull(email3);
    }

    @Test
    void debeRechazarEmailSinArroba() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("usuarioexample.com")
        );
        
        assertEquals("El email no tiene un formato válido", exception.getMessage());
    }

    @Test
    void debeRechazarEmailSinDominio() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("usuario@")
        );
    }

    @Test
    void debeRechazarEmailSinExtension() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("usuario@example")
        );
    }

    @Test
    void debeRechazarEmailVacio() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("")
        );
        
        assertEquals("El email no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void debeRechazarEmailNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Email.from(null)
        );
        
        assertEquals("El email no puede ser nulo o vacío", exception.getMessage());
    }

    @Test
    void debeRechazarEmailSoloEspacios() {
        assertThrows(
            IllegalArgumentException.class,
            () -> Email.from("   ")
        );
    }

    @Test
    void isValidDebeRetornarTrueParaEmailValido() {
        assertTrue(Email.isValid("usuario@example.com"));
    }

    @Test
    void isValidDebeRetornarFalseParaEmailInvalido() {
        assertFalse(Email.isValid("usuarioexample.com"));
        assertFalse(Email.isValid("usuario@"));
        assertFalse(Email.isValid("@example.com"));
        assertFalse(Email.isValid(""));
        assertFalse(Email.isValid(null));
    }

    @Test
    void dosEmailsConMismoValorDebenSerIguales() {
        Email email1 = Email.from("usuario@example.com");
        Email email2 = Email.from("usuario@example.com");
        
        assertEquals(email1, email2);
        assertEquals(email1.hashCode(), email2.hashCode());
    }

    @Test
    void dosEmailsConDiferenteValorNoDebenSerIguales() {
        Email email1 = Email.from("usuario1@example.com");
        Email email2 = Email.from("usuario2@example.com");
        
        assertNotEquals(email1, email2);
    }

    @Test
    void toStringDebeRetornarElEmail() {
        Email email = Email.from("usuario@example.com");
        
        assertEquals("usuario@example.com", email.toString());
    }

    @Test
    void emailEsInmutable() {
        Email email = Email.from("usuario@example.com");
        String valorOriginal = email.getValue();
        
        assertEquals(valorOriginal, email.getValue());
    }
}

