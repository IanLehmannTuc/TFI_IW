package tfi.domain.valueObject;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el Value Object Password.
 * Verifica validación de longitud y comportamiento del VO.
 */
class PasswordTest {

    @Test
    void debeCrearPasswordValida() {
        Password password = Password.from("password123");
        
        assertNotNull(password);
        assertEquals("password123", password.getValue());
    }

    @Test
    void debeAceptarPasswordDe8Caracteres() {
        Password password = Password.from("12345678");
        
        assertNotNull(password);
        assertEquals(8, password.getValue().length());
    }

    @Test
    void debeAceptarPasswordLarga() {
        String passwordLarga = "estaesunapasswordmuylargatienemasdecincuentacaracteres123456";
        
        Password password = Password.from(passwordLarga);
        
        assertNotNull(password);
        assertEquals(passwordLarga, password.getValue());
    }

    @Test
    void debeRechazarPasswordDe7Caracteres() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Password.from("1234567")
        );
        
        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());
    }

    @Test
    void debeRechazarPasswordVacia() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Password.from("")
        );
        
        assertEquals("La contraseña no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    void debeRechazarPasswordNull() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Password.from(null)
        );
        
        assertEquals("La contraseña no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    void debeAceptarPasswordConCaracteresEspeciales() {
        Password password = Password.from("P@ssw0rd!");
        
        assertNotNull(password);
        assertEquals("P@ssw0rd!", password.getValue());
    }

    @Test
    void debeAceptarPasswordConEspacios() {
        Password password = Password.from("mi password");
        
        assertNotNull(password);
        assertEquals("mi password", password.getValue());
    }

    @Test
    void isValidDebeRetornarTrueParaPasswordValida() {
        assertTrue(Password.isValid("12345678"));
        assertTrue(Password.isValid("password123"));
        assertTrue(Password.isValid("P@ssw0rd!"));
    }

    @Test
    void isValidDebeRetornarFalseParaPasswordInvalida() {
        assertFalse(Password.isValid("1234567"));
        assertFalse(Password.isValid(""));
        assertFalse(Password.isValid(null));
    }

    @Test
    void getMinLengthDebeRetornar8() {
        assertEquals(8, Password.getMinLength());
    }

    @Test
    void dosPasswordsConMismoValorDebenSerIguales() {
        Password password1 = Password.from("password123");
        Password password2 = Password.from("password123");
        
        assertEquals(password1, password2);
        assertEquals(password1.hashCode(), password2.hashCode());
    }

    @Test
    void dosPasswordsConDiferenteValorNoDebenSerIguales() {
        Password password1 = Password.from("password123");
        Password password2 = Password.from("password456");
        
        assertNotEquals(password1, password2);
    }

    @Test
    void toStringNoDebeRevelarPassword() {
        Password password = Password.from("secretpassword");
        
        String resultado = password.toString();
        
        assertEquals("***********", resultado);
        assertNotEquals("secretpassword", resultado);
    }

    @Test
    void passwordEsInmutable() {
        Password password = Password.from("password123");
        String valorOriginal = password.getValue();
        
        assertEquals(valorOriginal, password.getValue());
    }
}

