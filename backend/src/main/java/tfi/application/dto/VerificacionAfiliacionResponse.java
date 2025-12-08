package tfi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para la respuesta de verificación de afiliación a obra social.
 * Mapea la respuesta de la API externa de obras sociales.
 */
public class VerificacionAfiliacionResponse {
    
    @JsonProperty("esta_afiliado")
    private boolean estaAfiliado;
    
    @JsonProperty("numero_afiliado")
    private String numeroAfiliado;
    
    @JsonProperty("obra_social")
    private ObraSocialResponse obraSocial;

    /**
     * Constructor por defecto necesario para deserialización JSON
     */
    public VerificacionAfiliacionResponse() {
    }

    /**
     * Constructor completo
     */
    public VerificacionAfiliacionResponse(boolean estaAfiliado, String numeroAfiliado, ObraSocialResponse obraSocial) {
        this.estaAfiliado = estaAfiliado;
        this.numeroAfiliado = numeroAfiliado;
        this.obraSocial = obraSocial;
    }

    public boolean isEstaAfiliado() {
        return estaAfiliado;
    }

    public void setEstaAfiliado(boolean estaAfiliado) {
        this.estaAfiliado = estaAfiliado;
    }

    public String getNumeroAfiliado() {
        return numeroAfiliado;
    }

    public void setNumeroAfiliado(String numeroAfiliado) {
        this.numeroAfiliado = numeroAfiliado;
    }

    public ObraSocialResponse getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(ObraSocialResponse obraSocial) {
        this.obraSocial = obraSocial;
    }

    /**
     * DTO interno para la obra social en la respuesta de verificación
     */
    public static class ObraSocialResponse {
        private Integer id;
        private String nombre;

        public ObraSocialResponse() {
        }

        public ObraSocialResponse(Integer id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}
