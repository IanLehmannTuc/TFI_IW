package tfi.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utilidad para hashear y verificar contraseñas usando BCrypt.
 * BCrypt es un algoritmo de hashing diseñado específicamente para contraseñas.
 * 
 * IMPORTANTE: Nunca almacenar contraseñas en texto plano.
 * Siempre usar esta utilidad para hashear antes de guardar.
 */
public class PasswordHasher {

    /**
     * Número de rondas para el algoritmo BCrypt.
     * Valor por defecto recomendado es 10.
     * Mayor número = más seguro pero más lento.
     */
    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Constructor privado para prevenir instanciación
     */
    private PasswordHasher() {
        throw new UnsupportedOperationException("Clase utilitaria no instanciable");
    }

    /**
     * Hashea una contraseña en texto plano usando BCrypt.
     * Cada invocación genera un salt único, por lo que el mismo password
     * producirá hashes diferentes.
     * 
     * @param plainPassword La contraseña en texto plano
     * @return El hash BCrypt de la contraseña
     * @throws IllegalArgumentException Si la contraseña es null o vacía
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash BCrypt.
     * 
     * @param plainPassword La contraseña en texto plano a verificar
     * @param hashedPassword El hash BCrypt almacenado
     * @return true si la contraseña coincide, false en caso contrario
     * @throws IllegalArgumentException Si alguno de los parámetros es null o vacío
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede ser nula o vacía");
        }
        if (hashedPassword == null || hashedPassword.isEmpty()) {
            throw new IllegalArgumentException("El hash no puede ser nulo o vacío");
        }

        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}

