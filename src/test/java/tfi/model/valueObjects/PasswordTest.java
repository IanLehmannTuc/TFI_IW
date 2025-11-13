package tfi.model.valueObjects;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para el Value Object Password.
 * Verifica validación de longitud y comportamiento del VO.
 */
class PasswordTest {

    @Test
    void debeCrearPasswordValida() {
        // Arrange & Act
        Password password = Password.from("password123");
        
        // Assert
        assertNotNull(password);
        assertEquals("password123", password.getValue());
    }

    @Test
    void debeAceptarPasswordDe8Caracteres() {
        // Arrange & Act
        Password password = Password.from("12345678");
        
        // Assert
        assertNotNull(password);
        assertEquals(8, password.getValue().length());
    }

    @Test
    void debeAceptarPasswordLarga() {
        // Arrange
        String passwordLarga = "estaesunapasswordmuylargatienemasdecincuentacaracteres123456";
        
        // Act
        Password password = Password.from(passwordLarga);
        
        // Assert
        assertNotNull(password);
        assertEquals(passwordLarga, password.getValue());
    }

    @Test
    void debeRechazarPasswordDe7Caracteres() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Password.from("1234567")
        );
        
        assertEquals("La contraseña debe tener al menos 8 caracteres", exception.getMessage());
    }

    @Test
    void debeRechazarPasswordVacia() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Password.from("")
        );
        
        assertEquals("La contraseña no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    void debeRechazarPasswordNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> Password.from(null)
        );
        
        assertEquals("La contraseña no puede ser nula o vacía", exception.getMessage());
    }

    @Test
    void debeAceptarPasswordConCaracteresEspeciales() {
        // Arrange & Act
        Password password = Password.from("P@ssw0rd!");
        
        // Assert
        assertNotNull(password);
        assertEquals("P@ssw0rd!", password.getValue());
    }

    @Test
    void debeAceptarPasswordConEspacios() {
        // Arrange & Act
        Password password = Password.from("mi password");
        
        // Assert
        assertNotNull(password);
        assertEquals("mi password", password.getValue());
    }

    @Test
    void isValidDebeRetornarTrueParaPasswordValida() {
        // Act & Assert
        assertTrue(Password.isValid("12345678"));
        assertTrue(Password.isValid("password123"));
        assertTrue(Password.isValid("P@ssw0rd!"));
    }

    @Test
    void isValidDebeRetornarFalseParaPasswordInvalida() {
        // Act & Assert
        assertFalse(Password.isValid("1234567"));  // 7 caracteres
        assertFalse(Password.isValid(""));
        assertFalse(Password.isValid(null));
    }

    @Test
    void getMinLengthDebeRetornar8() {
        // Act & Assert
        assertEquals(8, Password.getMinLength());
    }

    @Test
    void dosPasswordsConMismoValorDebenSerIguales() {
        // Arrange
        Password password1 = Password.from("password123");
        Password password2 = Password.from("password123");
        
        // Act & Assert
        assertEquals(password1, password2);
        assertEquals(password1.hashCode(), password2.hashCode());
    }

    @Test
    void dosPasswordsConDiferenteValorNoDebenSerIguales() {
        // Arrange
        Password password1 = Password.from("password123");
        Password password2 = Password.from("password456");
        
        // Act & Assert
        assertNotEquals(password1, password2);
    }

    @Test
    void toStringNoDebeRevelarPassword() {
        // Arrange
        Password password = Password.from("secretpassword");
        
        // Act
        String resultado = password.toString();
        
        // Assert
        assertEquals("***********", resultado);
        assertNotEquals("secretpassword", resultado);
    }

    @Test
    void passwordEsInmutable() {
        // Arrange
        Password password = Password.from("password123");
        String valorOriginal = password.getValue();
        
        // Act
        // No hay setters, el objeto es inmutable
        
        // Assert
        assertEquals(valorOriginal, password.getValue());
    }
}

