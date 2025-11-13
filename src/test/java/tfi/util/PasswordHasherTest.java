package tfi.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para PasswordHasher.
 * Verifica el correcto funcionamiento de BCrypt.
 */
class PasswordHasherTest {

    @Test
    void debeHashearPasswordCorrectamente() {
        // Arrange
        String plainPassword = "password123";
        
        // Act
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        // Assert
        assertNotNull(hash);
        assertNotEquals(plainPassword, hash);
        assertTrue(hash.startsWith("$2a$")); // BCrypt hash prefix
    }

    @Test
    void hashGeneradoDebeTenerLongitudCorrecta() {
        // Arrange
        String plainPassword = "password123";
        
        // Act
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        // Assert
        assertEquals(60, hash.length()); // BCrypt genera hashes de 60 caracteres
    }

    @Test
    void dosHashesDelMismoPasswordDebenSerDiferentes() {
        // Arrange
        String plainPassword = "password123";
        
        // Act
        String hash1 = PasswordHasher.hashPassword(plainPassword);
        String hash2 = PasswordHasher.hashPassword(plainPassword);
        
        // Assert
        assertNotEquals(hash1, hash2); // Cada hash tiene un salt único
    }

    @Test
    void debeVerificarPasswordCorrectaConExito() {
        // Arrange
        String plainPassword = "password123";
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        // Act
        boolean resultado = PasswordHasher.checkPassword(plainPassword, hash);
        
        // Assert
        assertTrue(resultado);
    }

    @Test
    void debeRechazarPasswordIncorrecta() {
        // Arrange
        String plainPassword = "password123";
        String wrongPassword = "password456";
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        // Act
        boolean resultado = PasswordHasher.checkPassword(wrongPassword, hash);
        
        // Assert
        assertFalse(resultado);
    }

    @Test
    void checkPasswordDebeSerCaseSensitive() {
        // Arrange
        String plainPassword = "Password123";
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        // Act
        boolean resultadoCorrecto = PasswordHasher.checkPassword("Password123", hash);
        boolean resultadoIncorrecto = PasswordHasher.checkPassword("password123", hash);
        
        // Assert
        assertTrue(resultadoCorrecto);
        assertFalse(resultadoIncorrecto);
    }

    @Test
    void debeLanzarExcepcionSiPasswordEsNull() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.hashPassword(null)
        );
    }

    @Test
    void debeLanzarExcepcionSiPasswordEsVacia() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.hashPassword("")
        );
    }

    @Test
    void checkPasswordDebeLanzarExcepcionSiPasswordEsNull() {
        // Arrange
        String hash = PasswordHasher.hashPassword("password123");
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.checkPassword(null, hash)
        );
    }

    @Test
    void checkPasswordDebeLanzarExcepcionSiHashEsNull() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.checkPassword("password123", null)
        );
    }

    @Test
    void checkPasswordDebeRetornarFalseSiHashEsInvalido() {
        // Arrange
        String invalidHash = "hashInvalido";
        
        // Act
        boolean resultado = PasswordHasher.checkPassword("password123", invalidHash);
        
        // Assert
        assertFalse(resultado);
    }

    @Test
    void debeHashearPasswordsConCaracteresEspeciales() {
        // Arrange
        String plainPassword = "P@ssw0rd!#$%";
        
        // Act
        String hash = PasswordHasher.hashPassword(plainPassword);
        boolean verifica = PasswordHasher.checkPassword(plainPassword, hash);
        
        // Assert
        assertNotNull(hash);
        assertTrue(verifica);
    }

    @Test
    void debeHashearPasswordsLargas() {
        // Arrange
        String plainPassword = "estaesunapasswordmuylargatienemasdecincuentacaracteres123456";
        
        // Act
        String hash = PasswordHasher.hashPassword(plainPassword);
        boolean verifica = PasswordHasher.checkPassword(plainPassword, hash);
        
        // Assert
        assertNotNull(hash);
        assertTrue(verifica);
    }
}

