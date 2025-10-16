package tfi.model.valueObjects;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object que representa un CUIL (Clave Única de Identificación Laboral).
 * Es inmutable y garantiza que el formato sea válido.
 * Formato: XX-XXXXXXXX-X (ejemplo: 20-12345678-9)
 */
public final class Cuil {
    private static final Pattern CUIL_PATTERN = Pattern.compile("^\\d{2}-\\d{8}-\\d{1}$");
    private static final String[] PREFIJOS_VALIDOS = {"20", "23", "24", "27", "30", "33", "34"};
    private final String valor;

    /**
     * Constructor que crea un nuevo CUIL.
     * 
     * @param valor el CUIL en formato XX-XXXXXXXX-X
     * @throws IllegalArgumentException 
     */
    public Cuil(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL no puede ser nulo o vacío");
        }
        
        String cuilLimpio = valor.trim();
        
        if (!CUIL_PATTERN.matcher(cuilLimpio).matches()) {
            throw new IllegalArgumentException(
                "El CUIL debe tener el formato XX-XXXXXXXX-X (ejemplo: 20-12345678-9)"
            );
        }
        
        // Validar que el prefijo sea válido
        String prefijo = cuilLimpio.substring(0, 2);
        boolean prefijoValido = false;
        for (String p : PREFIJOS_VALIDOS) {
            if (p.equals(prefijo)) {
                prefijoValido = true;
                break;
            }
        }
        if (!prefijoValido) {
            throw new IllegalArgumentException(
                "El CUIL debe comenzar con un prefijo válido: 20, 23, 24, 27, 30, 33 o 34"
            );
        }
        
        if (!validarDigitoVerificador(cuilLimpio)) {
            throw new IllegalArgumentException(
                "El CUIL tiene un dígito verificador inválido"
            );
        }
        
        this.valor = cuilLimpio;
    }

    /**
     * Valida el dígito verificador del CUIL usando el algoritmo oficial.
     * 
     * @param cuil el CUIL completo con guiones
     * @return true si el dígito verificador es válido
     */
    private boolean validarDigitoVerificador(String cuil) {
        try {
            // Remover guiones y obtener los dígitos
            String sinGuiones = cuil.replace("-", "");
            
            // Los primeros 10 dígitos se usan para calcular
            String base = sinGuiones.substring(0, 10);
            int digitoVerificador = Integer.parseInt(sinGuiones.substring(10, 11));
            
            // Multiplicadores según algoritmo de CUIL
            int[] multiplicadores = {5, 4, 3, 2, 7, 6, 5, 4, 3, 2};
            int suma = 0;
            
            for (int i = 0; i < 10; i++) {
                suma += Character.getNumericValue(base.charAt(i)) * multiplicadores[i];
            }
            
            int resto = suma % 11;
            int digitoCalculado = 11 - resto;
            
            // Casos especiales
            if (digitoCalculado == 11) {
                digitoCalculado = 0;
            } else if (digitoCalculado == 10) {
                digitoCalculado = 9;
            }
            
            return digitoCalculado == digitoVerificador;
        } catch (Exception e) {
            return false;
        }
    }

    public String getValor() {
        return valor;
    }

    /**
     * Retorna el CUIL sin guiones (solo números).
     * 
     * @return el CUIL sin guiones (ejemplo: 20123456789)
     */
    public String getValorSinGuiones() {
        return valor.replace("-", "");
    }

    /**
     * Retorna el número de DNI extraído del CUIL (8 dígitos del medio).
     * 
     * @return el DNI (ejemplo: 12345678)
     */
    public String getDni() {
        return valor.substring(3, 11);
    }

    /**
     * Retorna el prefijo del CUIL (los 2 primeros dígitos).
     * 
     * @return el prefijo (20, 23, 24, 27, 30, 33 o 34)
     */
    public String getPrefijo() {
        return valor.substring(0, 2);
    }

    /**
     * Determina el género probable basándose en el prefijo del CUIL.
     * 
     * @return "M" para masculino (20, 27, 30), "F" para femenino (23, 24, 33, 34)
     */
    public String getGenero() {
        String prefijo = getPrefijo();
        switch (prefijo) {
            case "20":
            case "27":
            case "30":
                return "M";
            case "23":
            case "24":
            case "33":
            case "34":
                return "F";
            default:
                return null; // No debería llegar aquí por validación
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cuil)) return false;
        Cuil cuil = (Cuil) o;
        return Objects.equals(valor, cuil.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}

