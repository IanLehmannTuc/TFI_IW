package tfi.application.dto;

/**
 * DTO para la respuesta de registro de paciente.
 * Contiene la información del paciente registrado.
 */
public class PacienteResponse {
    private String cuil;
    private String nombre;
    private String apellido;
    private DomicilioResponse domicilio;
    private AfiliadoResponse obraSocial;

    /**
     * Constructor por defecto necesario para serialización JSON
     */
    public PacienteResponse() {
    }

    /**
     * Constructor completo
     */
    public PacienteResponse(String cuil, String nombre, String apellido, 
                           DomicilioResponse domicilio, AfiliadoResponse obraSocial) {
        this.cuil = cuil;
        this.nombre = nombre;
        this.apellido = apellido;
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public DomicilioResponse getDomicilio() {
        return domicilio;
    }

    public void setDomicilio(DomicilioResponse domicilio) {
        this.domicilio = domicilio;
    }

    public AfiliadoResponse getObraSocial() {
        return obraSocial;
    }

    public void setObraSocial(AfiliadoResponse obraSocial) {
        this.obraSocial = obraSocial;
    }

    /**
     * DTO interno para el domicilio
     */
    public static class DomicilioResponse {
        private String calle;
        private Integer numero;
        private String localidad;

        public DomicilioResponse() {
        }

        public DomicilioResponse(String calle, Integer numero, String localidad) {
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
     * DTO interno para la afiliación
     */
    public static class AfiliadoResponse {
        private ObraSocialResponse obraSocial;
        private String numeroAfiliado;

        public AfiliadoResponse() {
        }

        public AfiliadoResponse(ObraSocialResponse obraSocial, String numeroAfiliado) {
            this.obraSocial = obraSocial;
            this.numeroAfiliado = numeroAfiliado;
        }

        public ObraSocialResponse getObraSocial() {
            return obraSocial;
        }

        public void setObraSocial(ObraSocialResponse obraSocial) {
            this.obraSocial = obraSocial;
        }

        public String getNumeroAfiliado() {
            return numeroAfiliado;
        }

        public void setNumeroAfiliado(String numeroAfiliado) {
            this.numeroAfiliado = numeroAfiliado;
        }
    }

    /**
     * DTO interno para la obra social
     */
    public static class ObraSocialResponse {
        private Integer idObraSocial;
        private String nombreObraSocial;

        public ObraSocialResponse() {
        }

        public ObraSocialResponse(Integer idObraSocial, String nombreObraSocial) {
            this.idObraSocial = idObraSocial;
            this.nombreObraSocial = nombreObraSocial;
        }

        public Integer getIdObraSocial() {
            return idObraSocial;
        }

        public void setIdObraSocial(Integer idObraSocial) {
            this.idObraSocial = idObraSocial;
        }

        public String getNombreObraSocial() {
            return nombreObraSocial;
        }

        public void setNombreObraSocial(String nombreObraSocial) {
            this.nombreObraSocial = nombreObraSocial;
        }
    }
}

