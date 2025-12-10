package tfi.domain.enums;

/**
 * Enum que representa el sexo de una persona.
 */
public enum Sexo {
    /**
     * Sexo masculino.
     */
    MASCULINO("M", "Masculino"),

    /**
     * Sexo femenino.
     */
    FEMENINO("F", "Femenino");

    private final String codigo;
    private final String descripcion;

    /**
     * Constructor del enum Sexo.
     *
     * @param codigo el código del sexo
     * @param descripcion la descripción del sexo
     */
    Sexo(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    /**
     * Obtiene el código del sexo.
     *
     * @return el código del sexo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * Obtiene la descripción del sexo.
     *
     * @return la descripción del sexo
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Crea una instancia de Sexo a partir de su código.
     *
     * @param codigo el código del sexo (M o F)
     * @return la instancia de Sexo correspondiente
     * @throws IllegalArgumentException si el código no es válido
     */
    public static Sexo fromCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código de sexo no puede ser nulo o vacío");
        }

        String codigoNormalizado = codigo.trim().toUpperCase();

        for (Sexo sexo : values()) {
            if (sexo.codigo.equals(codigoNormalizado)) {
                return sexo;
            }
        }

        throw new IllegalArgumentException(
            "Código de sexo inválido: '" + codigo + "'. Valores válidos: M, F"
        );
    }

    @Override
    public String toString() {
        return descripcion;
    }
}

