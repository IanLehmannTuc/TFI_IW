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
        String plainPassword = "password123";
        
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        assertNotNull(hash);
        assertNotEquals(plainPassword, hash);
        assertTrue(hash.startsWith("$2a$"));
    }

    @Test
    void hashGeneradoDebeTenerLongitudCorrecta() {
        String plainPassword = "password123";
        
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        assertEquals(60, hash.length());
    }

    @Test
    void dosHashesDelMismoPasswordDebenSerDiferentes() {
        String plainPassword = "password123";
        
        String hash1 = PasswordHasher.hashPassword(plainPassword);
        String hash2 = PasswordHasher.hashPassword(plainPassword);
        
        assertNotEquals(hash1, hash2);
    }

    @Test
    void debeVerificarPasswordCorrectaConExito() {
        String plainPassword = "password123";
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        boolean resultado = PasswordHasher.checkPassword(plainPassword, hash);
        
        assertTrue(resultado);
    }

    @Test
    void debeRechazarPasswordIncorrecta() {
        String plainPassword = "password123";
        String wrongPassword = "password456";
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        boolean resultado = PasswordHasher.checkPassword(wrongPassword, hash);
        
        assertFalse(resultado);
    }

    @Test
    void checkPasswordDebeSerCaseSensitive() {
        String plainPassword = "Password123";
        String hash = PasswordHasher.hashPassword(plainPassword);
        
        boolean resultadoCorrecto = PasswordHasher.checkPassword("Password123", hash);
        boolean resultadoIncorrecto = PasswordHasher.checkPassword("password123", hash);
        
        assertTrue(resultadoCorrecto);
        assertFalse(resultadoIncorrecto);
    }

    @Test
    void debeLanzarExcepcionSiPasswordEsNull() {
 
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.hashPassword(null)
        );
    }

    @Test
    void debeLanzarExcepcionSiPasswordEsVacia() {
 
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.hashPassword("")
        );
    }

    @Test
    void checkPasswordDebeLanzarExcepcionSiPasswordEsNull() {
        String hash = PasswordHasher.hashPassword("password123");
        
 
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.checkPassword(null, hash)
        );
    }

    @Test
    void checkPasswordDebeLanzarExcepcionSiHashEsNull() {
 
        assertThrows(
            IllegalArgumentException.class,
            () -> PasswordHasher.checkPassword("password123", null)
        );
    }

    @Test
    void checkPasswordDebeRetornarFalseSiHashEsInvalido() {
        String invalidHash = "hashInvalido";
        
        boolean resultado = PasswordHasher.checkPassword("password123", invalidHash);
        
        assertFalse(resultado);
    }

    @Test
    void debeHashearPasswordsConCaracteresEspeciales() {
        String plainPassword = "P@ssw0rd!#$%";
        
        String hash = PasswordHasher.hashPassword(plainPassword);
        boolean verifica = PasswordHasher.checkPassword(plainPassword, hash);
        
        assertNotNull(hash);
        assertTrue(verifica);
    }

    @Test
    void debeHashearPasswordsLargas() {
        String plainPassword = "estaesunapasswordmuylargatienemasdecincuentacaracteres123456";
        
        String hash = PasswordHasher.hashPassword(plainPassword);
        boolean verifica = PasswordHasher.checkPassword(plainPassword, hash);
        
        assertNotNull(hash);
        assertTrue(verifica);
    }
}

