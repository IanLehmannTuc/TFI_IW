package tfi.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO para representar una obra social en las respuestas.
 * Usado para listar obras sociales disponibles.
 */
public class ObraSocialResponse {

    private Integer id;
    private String nombre;

    /**
     * Constructor por defecto necesario para deserialización JSON
     */
    public ObraSocialResponse() {
    }

    /**
     * Constructor completo
     */
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
