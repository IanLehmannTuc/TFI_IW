package tfi.domain.entity;

import tfi.domain.valueObject.Cuil;
import tfi.domain.valueObject.Domicilio;
import tfi.domain.valueObject.Email;

/**
 * Entidad que representa un paciente del sistema.
 * Es un agregado raíz que contiene la información del paciente, su domicilio y afiliación a obra social.
 * 
 * Esta entidad implementa Rich Domain Model con factory methods expresivos
 * y métodos de negocio que encapsulan las reglas del dominio.
 */
public class Paciente {
    private String id;
    private Cuil cuil;
    private String nombre;
    private String apellido;
    private Email email;
    private Afiliado obraSocial;
    private Domicilio domicilio;

    /**
     * Constructor privado para uso interno.
     * Usar factory methods estáticos para crear instancias.
     */
    private Paciente(String cuil, String nombre, String apellido, Email email, Domicilio domicilio, Afiliado obraSocial) {
        if (cuil == null || cuil.trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL es obligatorio");
        }
        this.cuil = new Cuil(cuil);
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.domicilio = domicilio;
        this.obraSocial = obraSocial;
    }

    /**
     * Factory method: Crea un paciente con datos básicos (solo CUIL).
     * Útil cuando solo se conoce el CUIL del paciente.
     * 
     * @param cuil CUIL del paciente
     * @return Nueva instancia de Paciente
     */
    public static Paciente crearConCuil(String cuil) {
        return new Paciente(cuil, null, null, null, null, null);
    }

    /**
     * Factory method: Crea un paciente con datos básicos (CUIL, nombre y apellido).
     * 
     * @param cuil CUIL del paciente
     * @param nombre Nombre del paciente
     * @param apellido Apellido del paciente
     * @return Nueva instancia de Paciente
     */
    public static Paciente crearConDatosBasicos(String cuil, String nombre, String apellido) {
        Email emailVO = null;
        return new Paciente(cuil, nombre, apellido, emailVO, null, null);
    }

    /**
     * Factory method: Crea un paciente con domicilio y obra social (sin datos personales completos).
     * 
     * @param cuil CUIL del paciente
     * @param domicilio Domicilio del paciente
     * @param obraSocial Afiliación a obra social
     * @return Nueva instancia de Paciente
     */
    public static Paciente crearConDomicilioYObraSocial(String cuil, Domicilio domicilio, Afiliado obraSocial) {
        Email emailVO = null;
        return new Paciente(cuil, null, null, emailVO, domicilio, obraSocial);
    }

    /**
     * Factory method: Crea un paciente completo con todos los datos disponibles.
     * 
     * @param cuil CUIL del paciente
     * @param nombre Nombre del paciente
     * @param apellido Apellido del paciente
     * @param email Email del paciente (puede ser null)
     * @param domicilio Domicilio del paciente (puede ser null)
     * @param obraSocial Afiliación a obra social (puede ser null)
     * @return Nueva instancia de Paciente
     */
    public static Paciente crearCompleto(String cuil, String nombre, String apellido, String email, 
                                         Domicilio domicilio, Afiliado obraSocial) {
        Email emailVO = email != null ? Email.from(email) : null;
        return new Paciente(cuil, nombre, apellido, emailVO, domicilio, obraSocial);
    }


    public Cuil getCuilVO() {
        return cuil;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Email getEmailVO() {
        return email;
    }

    /**
     * Método de negocio: Actualiza los datos personales del paciente.
     * 
     * @param nombre Nuevo nombre (puede ser null)
     * @param apellido Nuevo apellido (puede ser null)
     */
    public void actualizarDatosPersonales(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    /**
     * Método de negocio: Actualiza el email del paciente.
     * 
     * @param email Nuevo email (puede ser null para eliminar el email)
     */
    public void actualizarEmail(String email) {
        this.email = email != null ? Email.from(email) : null;
    }

    /**
     * Método de negocio: Actualiza el domicilio del paciente.
     * 
     * @param domicilio Nuevo domicilio (puede ser null)
     */
    public void actualizarDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }

    /**
     * Método de negocio: Actualiza la afiliación a obra social.
     * 
     * @param obraSocial Nueva afiliación (puede ser null para eliminar la afiliación)
     */
    public void actualizarObraSocial(Afiliado obraSocial) {
        this.obraSocial = obraSocial;
    }

    /**
     * Método de consulta: Verifica si el paciente tiene datos personales completos.
     * 
     * @return true si tiene nombre y apellido, false en caso contrario
     */
    public boolean tieneDatosPersonalesCompletos() {
        return nombre != null && !nombre.trim().isEmpty() && 
               apellido != null && !apellido.trim().isEmpty();
    }

    /**
     * Método de consulta: Verifica si el paciente tiene domicilio registrado.
     * 
     * @return true si tiene domicilio, false en caso contrario
     */
    public boolean tieneDomicilio() {
        return domicilio != null;
    }

    /**
     * Método de consulta: Verifica si el paciente tiene obra social.
     * 
     * @return true si tiene obra social, false en caso contrario
     */
    public boolean tieneObraSocial() {
        return obraSocial != null;
    }

    /**
     * Método de consulta: Obtiene el nombre completo del paciente.
     * 
     * @return Nombre completo en formato "Apellido, Nombre" o solo nombre/apellido si falta uno
     */
    public String obtenerNombreCompleto() {
        if (nombre != null && apellido != null) {
            return apellido + ", " + nombre;
        } else if (nombre != null) {
            return nombre;
        } else if (apellido != null) {
            return apellido;
        }
        return "Paciente " + cuil.getValor();
    }


    public String getCuil() {
        return cuil != null ? cuil.getValor() : null;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getEmail() {
        return email != null ? email.getValue() : null;
    }

    public Afiliado getObraSocial() {
        return obraSocial;
    }

    public Domicilio getDomicilio() {
        return domicilio;
    }


    /**
     * Setter para CUIL - SOLO para uso interno del repositorio al recuperar desde BD.
     * @deprecated El CUIL no debería cambiarse después de la creación.
     */
    @Deprecated
    public void setCuil(String cuil) {
        this.cuil = new Cuil(cuil);
    }

    /**
     * Setter para nombre - SOLO para uso interno del repositorio al recuperar desde BD.
     * @deprecated Usar actualizarDatosPersonales() en su lugar.
     */
    @Deprecated
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Setter para apellido - SOLO para uso interno del repositorio al recuperar desde BD.
     * @deprecated Usar actualizarDatosPersonales() en su lugar.
     */
    @Deprecated
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Setter para email - SOLO para uso interno del repositorio al recuperar desde BD.
     * @deprecated Usar actualizarEmail() en su lugar.
     */
    @Deprecated
    public void setEmail(String email) {
        this.email = email != null ? Email.from(email) : null;
    }

    /**
     * Setter para obra social - SOLO para uso interno del repositorio al recuperar desde BD.
     * @deprecated Usar actualizarObraSocial() en su lugar.
     */
    @Deprecated
    public void setObraSocial(Afiliado obraSocial) {
        this.obraSocial = obraSocial;
    }

    /**
     * Setter para domicilio - SOLO para uso interno del repositorio al recuperar desde BD.
     * @deprecated Usar actualizarDomicilio() en su lugar.
     */
    @Deprecated
    public void setDomicilio(Domicilio domicilio) {
        this.domicilio = domicilio;
    }
}
