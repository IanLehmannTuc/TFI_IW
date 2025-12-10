package tfi.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para la solicitud de registro de un nuevo paciente.
 * Contiene los datos necesarios para crear un paciente en el sistema.
 */
public class RegistroPacienteRequest {

    @NotBlank(message = "El CUIL es obligatorio")
    private String cuil;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El domicilio es obligatorio")
    @Valid
    private DomicilioRequest domicilio;

    @Valid
    private AfiliadoRequest obraSocial;

    /**
     * Constructor por defecto necesario para deserialización JSON
     */
    public RegistroPacienteRequest() {
    }

    /**
     * Constructor completo
     */
    public RegistroPacienteRequest(String cuil, String apellido, String nombre, 
                                   DomicilioRequest domicilio, AfiliadoRequest obraSocial) {
        this.cuil = cuil;
        this.apellido = apellido;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public DomicilioRequest getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(DomicilioRequest domicilio) {
        this.domicilio = domicilio;
    }

    public AfiliadoRequest getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(AfiliadoRequest obraSocial) {
        this.obraSocial = obraSocial;
    }

    /**
     * DTO para el domicilio de un paciente.
     */
    public static class DomicilioRequest {
        @NotBlank(message = "La calle es obligatoria")
        private String calle;

        @NotNull(message = "El número es obligatorio")
        private Integer numero;

        @NotBlank(message = "La localidad es obligatoria")
        private String localidad;

        public DomicilioRequest() {
        }

        public DomicilioRequest(String calle, Integer numero, String localidad) {
            this.calle = calle;
            this.numero = numero;
            this.localidad = localidad;
        }

        public String getCalle() {
            return calle;
        }

        public void setCalle(String calle) {
            this.calle = calle;
        }

        public Integer getNumero() {
            return numero;
        }

        public void setNumero(Integer numero) {
            this.numero = numero;
        }

        public String getLocalidad() {
            return localidad;
        }

        public void setLocalidad(String localidad) {
            this.localidad = localidad;
        }
    }

    /**
     * DTO para la obra social.
     */
    public static class ObraSocialRequest {
        @NotNull(message = "El ID de la obra social es obligatorio")
        private Integer id;

        private String nombre;

        public ObraSocialRequest() {
        }

        public ObraSocialRequest(Integer id) {
            this.id = id;
        }

        public ObraSocialRequest(Integer id, String nombre) {
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

    /**
     * DTO para la afiliación a obra social de un paciente.
     */
    public static class AfiliadoRequest {
        @NotNull(message = "La obra social es obligatoria cuando se especifica afiliación")
        @Valid
        private ObraSocialRequest obraSocial;

        @NotBlank(message = "El número de afiliado es obligatorio")
        private String numeroAfiliado;

        public AfiliadoRequest() {
        }

        public AfiliadoRequest(ObraSocialRequest obraSocial, String numeroAfiliado) {
            this.obraSocial = obraSocial;
            this.numeroAfiliado = numeroAfiliado;
        }

        public ObraSocialRequest getObraSocial() {
            return obraSocial;
        }

        public void setObraSocial(ObraSocialRequest obraSocial) {
            this.obraSocial = obraSocial;
        }

        public String getNumeroAfiliado() {
            return numeroAfiliado;
        }

        public void setNumeroAfiliado(String numeroAfiliado) {
            this.numeroAfiliado = numeroAfiliado;
        }
    }
}

