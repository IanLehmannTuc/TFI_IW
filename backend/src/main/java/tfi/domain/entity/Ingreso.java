package tfi.domain.entity;

import tfi.domain.enums.NivelEmergencia;
import tfi.domain.enums.Estado;
import tfi.domain.valueObject.Temperatura;
import tfi.domain.valueObject.TensionArterial;
import tfi.domain.valueObject.FrecuenciaCardiaca;
import tfi.domain.valueObject.FrecuenciaRespiratoria;
import java.time.LocalDateTime;

/**
 * Entidad que representa un ingreso de paciente a urgencias.
 * Es un agregado raíz que contiene la información del ingreso y su atención médica.
 * 
 * Esta entidad implementa Rich Domain Model con métodos de negocio que encapsulan
 * las reglas de negocio y protegen las invariantes del dominio.
 */
public class Ingreso {
    private String id;
    private Atencion atencion;
    private Paciente paciente;
    private Usuario enfermero;
    private String descripcion;
    private LocalDateTime fechaHoraIngreso;
    private Temperatura temperatura;
    private TensionArterial tensionArterial;
    private FrecuenciaCardiaca frecuenciaCardiaca;
    private FrecuenciaRespiratoria frecuenciaRespiratoria;
    private NivelEmergencia nivelEmergencia;
    private Estado estado; // Estado privado, solo modificable mediante métodos de negocio

    public Ingreso(Atencion atencion, Paciente paciente, Usuario enfermero, String descripcion, LocalDateTime fechaHoraIngreso,
                   Temperatura temperatura, TensionArterial tensionArterial, FrecuenciaCardiaca frecuenciaCardiaca,
                   FrecuenciaRespiratoria frecuenciaRespiratoria, NivelEmergencia nivelEmergencia) {
        this.atencion = atencion;
        this.paciente = paciente;
        this.enfermero = enfermero;
        this.descripcion = descripcion;
        this.fechaHoraIngreso = fechaHoraIngreso;
        this.temperatura = temperatura;
        this.tensionArterial = tensionArterial;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.nivelEmergencia = nivelEmergencia;
        this.estado = Estado.PENDIENTE;
    }

    public Ingreso(
        Paciente paciente, 
        Usuario enfermero, 
        String descripcion, 
        Temperatura temperatura, 
        TensionArterial tensionArterial, 
        FrecuenciaCardiaca frecuenciaCardiaca, 
        FrecuenciaRespiratoria frecuenciaRespiratoria, 
        NivelEmergencia nivelEmergencia) {

        this.atencion = null;
        this.paciente = paciente;
        this.enfermero = enfermero;
        this.descripcion = descripcion;
        this.fechaHoraIngreso = LocalDateTime.now();
        this.temperatura = temperatura;
        this.tensionArterial = tensionArterial;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.nivelEmergencia = nivelEmergencia;
        this.estado = Estado.PENDIENTE;
    }

    public Atencion getAtencion() {
        return atencion;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public Usuario getEnfermero() {
        return enfermero;
    }

    public void setEnfermero(Usuario enfermero) {
        this.enfermero = enfermero;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaHoraIngreso() {
        return fechaHoraIngreso;
    }


    public Temperatura getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Temperatura temperatura) {
        this.temperatura = temperatura;
    }

    public TensionArterial getTensionArterial() {
        return tensionArterial;
    }

    public void setTensionArterial(TensionArterial tensionArterial) {
        this.tensionArterial = tensionArterial;
    }

    public FrecuenciaCardiaca getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public void setFrecuenciaCardiaca(FrecuenciaCardiaca frecuenciaCardiaca) {
        this.frecuenciaCardiaca = frecuenciaCardiaca;
    }

    public FrecuenciaRespiratoria getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public void setFrecuenciaRespiratoria(FrecuenciaRespiratoria frecuenciaRespiratoria) {
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    public NivelEmergencia getNivelEmergencia() {
        return nivelEmergencia;
    }

    public void setNivelEmergencia(NivelEmergencia nivelEmergencia) {
        this.nivelEmergencia = nivelEmergencia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Estado getEstado() {
        return estado;
    }

    /**
     * Método de negocio: Inicia la atención de un ingreso pendiente.
     * Valida que el ingreso esté en estado PENDIENTE antes de cambiar a EN_PROCESO.
     * 
     * @throws IllegalStateException si el ingreso no está en estado PENDIENTE
     */
    public void iniciarAtencion() {
        if (this.estado != Estado.PENDIENTE) {
            throw new IllegalStateException(
                String.format("Solo se pueden iniciar ingresos PENDIENTES. Estado actual: %s", this.estado)
            );
        }
        this.estado = Estado.EN_PROCESO;
    }

    /**
     * Método de negocio: Asigna una atención médica al ingreso.
     * Valida que el ingreso esté en estado EN_PROCESO y que no tenga ya una atención asignada.
     * 
     * @param atencion La atención médica a asignar
     * @throws IllegalStateException si el ingreso no está en estado EN_PROCESO o ya tiene atención
     * @throws IllegalArgumentException si la atención es nula
     */
    public void asignarAtencion(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("La atención no puede ser nula");
        }
        if (this.atencion != null) {
            throw new IllegalStateException("El ingreso ya tiene una atención asignada");
        }
        if (this.estado != Estado.EN_PROCESO) {
            throw new IllegalStateException(
                String.format("Solo se puede asignar atención a ingresos EN_PROCESO. Estado actual: %s", this.estado)
            );
        }
        this.atencion = atencion;
    }

    /**
     * Método de negocio: Finaliza un ingreso asignándole una atención.
     * Valida que el ingreso esté en estado EN_PROCESO y tenga una atención asignada.
     * 
     * @param atencion La atención médica que finaliza el ingreso
     * @throws IllegalStateException si el ingreso no está en estado EN_PROCESO o no tiene atención
     * @throws IllegalArgumentException si la atención es nula
     */
    public void finalizar(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("La atención no puede ser nula para finalizar el ingreso");
        }
        if (this.estado != Estado.EN_PROCESO) {
            throw new IllegalStateException(
                String.format("Solo se pueden finalizar ingresos EN_PROCESO. Estado actual: %s", this.estado)
            );
        }
        asignarAtencion(atencion);
        this.estado = Estado.FINALIZADO;
    }

    /**
     * Método de consulta: Verifica si el ingreso está pendiente.
     * 
     * @return true si el ingreso está en estado PENDIENTE, false en caso contrario
     */
    public boolean estaPendiente() {
        return this.estado == Estado.PENDIENTE;
    }

    /**
     * Método de consulta: Verifica si el ingreso está en proceso.
     * 
     * @return true si el ingreso está en estado EN_PROCESO, false en caso contrario
     */
    public boolean estaEnProceso() {
        return this.estado == Estado.EN_PROCESO;
    }

    /**
     * Método de consulta: Verifica si el ingreso está finalizado.
     * 
     * @return true si el ingreso está en estado FINALIZADO, false en caso contrario
     */
    public boolean estaFinalizado() {
        return this.estado == Estado.FINALIZADO;
    }

    /**
     * Método de consulta: Verifica si el ingreso puede ser atendido.
     * Un ingreso puede ser atendido si está pendiente y tiene paciente y enfermero asignados.
     * 
     * @return true si el ingreso puede ser atendido, false en caso contrario
     */
    public boolean puedeSerAtendido() {
        return estaPendiente() && this.paciente != null && this.enfermero != null;
    }

    /**
     * Método de consulta: Verifica si el ingreso tiene atención asignada.
     * 
     * @return true si el ingreso tiene atención, false en caso contrario
     */
    public boolean tieneAtencion() {
        return this.atencion != null;
    }

    /**
     * Método de negocio: Valida que el ingreso puede recibir una atención médica.
     * Un ingreso puede recibir atención si:
     * - Está en estado EN_PROCESO
     * - No tiene ya una atención asignada
     * 
     * @throws IllegalStateException si el ingreso no puede recibir atención
     */
    public void puedeRecibirAtencion() {
        if (this.estado != Estado.EN_PROCESO) {
            throw new IllegalStateException(
                String.format("El ingreso debe estar en estado EN_PROCESO para recibir atención. Estado actual: %s", this.estado)
            );
        }
        if (this.atencion != null) {
            throw new IllegalStateException("El ingreso ya tiene una atención asignada");
        }
    }

    /**
     * Método específico para repositorios: Restaura el estado desde la base de datos.
     * SOLO debe usarse por repositorios al recuperar entidades desde BD.
     * NO debe usarse para cambiar el estado del negocio. Usar métodos de negocio en su lugar.
     * 
     * @param estado Estado a restaurar desde BD
     */
    public void restoreEstadoFromPersistence(Estado estado) {
        this.estado = estado;
    }

    /**
     * Método específico para repositorios: Restaura la atención desde la base de datos.
     * SOLO debe usarse por repositorios al recuperar entidades desde BD.
     * NO debe usarse para asignar atención. Usar asignarAtencion() o finalizar() en su lugar.
     * 
     * @param atencion Atención a restaurar desde BD
     */
    public void restoreAtencionFromPersistence(Atencion atencion) {
        this.atencion = atencion;
    }
    
    /**
     * Método específico para repositorios: Restaura la fecha de ingreso desde la base de datos.
     * SOLO debe usarse por repositorios al recuperar entidades desde BD.
     * 
     * @param fechaHoraIngreso Fecha a restaurar desde BD
     */
    public void restoreFechaHoraIngresoFromPersistence(LocalDateTime fechaHoraIngreso) {
        this.fechaHoraIngreso = fechaHoraIngreso;
    }
    
    /**
     * Setter para estado - SOLO para uso interno del repositorio al recuperar desde BD.
     * NO debe usarse para cambiar el estado del negocio. Usar métodos de negocio en su lugar.
     * 
     * @deprecated Usar restoreEstadoFromPersistence() en su lugar. Este método se mantiene por compatibilidad.
     */
    @Deprecated
    public void setEstado(Estado estado) {
        restoreEstadoFromPersistence(estado);
    }

    /**
     * Setter para atención - SOLO para uso interno del repositorio al recuperar desde BD.
     * NO debe usarse para asignar atención. Usar asignarAtencion() o finalizar() en su lugar.
     * 
     * @deprecated Usar restoreAtencionFromPersistence() en su lugar. Este método se mantiene por compatibilidad.
     */
    @Deprecated
    public void setAtencion(Atencion atencion) {
        restoreAtencionFromPersistence(atencion);
    }
    
    /**
     * Setter para fecha de ingreso - SOLO para uso interno del repositorio al recuperar desde BD.
     * 
     * @deprecated Usar restoreFechaHoraIngresoFromPersistence() en su lugar. Este método se mantiene por compatibilidad.
     */
    @Deprecated
    public void setFechaHoraIngreso(LocalDateTime fechaHoraIngreso) {
        restoreFechaHoraIngresoFromPersistence(fechaHoraIngreso);
    }

    /**
     * Método de negocio: Actualiza los signos vitales del ingreso.
     * Valida que los parámetros no sean nulos.
     * 
     * @param temperatura Nueva temperatura
     * @param tensionArterial Nueva tensión arterial
     * @param frecuenciaCardiaca Nueva frecuencia cardíaca
     * @param frecuenciaRespiratoria Nueva frecuencia respiratoria
     * @throws IllegalArgumentException si algún parámetro es nulo
     */
    public void actualizarVitales(Temperatura temperatura, 
                                   TensionArterial tensionArterial,
                                   FrecuenciaCardiaca frecuenciaCardiaca,
                                   FrecuenciaRespiratoria frecuenciaRespiratoria) {
        if (temperatura == null) {
            throw new IllegalArgumentException("La temperatura no puede ser nula");
        }
        if (tensionArterial == null) {
            throw new IllegalArgumentException("La tensión arterial no puede ser nula");
        }
        if (frecuenciaCardiaca == null) {
            throw new IllegalArgumentException("La frecuencia cardíaca no puede ser nula");
        }
        if (frecuenciaRespiratoria == null) {
            throw new IllegalArgumentException("La frecuencia respiratoria no puede ser nula");
        }
        this.temperatura = temperatura;
        this.tensionArterial = tensionArterial;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
    }

    /**
     * Método de negocio: Actualiza la descripción del ingreso.
     * 
     * @param descripcion Nueva descripción
     * @throws IllegalArgumentException si la descripción es nula o vacía
     */
    public void actualizarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede ser nula o vacía");
        }
        this.descripcion = descripcion.trim();
    }

    /**
     * Método de negocio: Actualiza el nivel de emergencia del ingreso.
     * 
     * @param nivelEmergencia Nuevo nivel de emergencia
     * @throws IllegalArgumentException si el nivel de emergencia es nulo
     */
    public void actualizarNivelEmergencia(NivelEmergencia nivelEmergencia) {
        if (nivelEmergencia == null) {
            throw new IllegalArgumentException("El nivel de emergencia no puede ser nulo");
        }
        this.nivelEmergencia = nivelEmergencia;
    }

    /**
     * Método de negocio: Actualiza el paciente del ingreso.
     * 
     * @param paciente Nuevo paciente
     * @throws IllegalArgumentException si el paciente es nulo
     */
    public void actualizarPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        this.paciente = paciente;
    }

    /**
     * Método de negocio: Actualiza el enfermero del ingreso.
     * Valida que el usuario sea un enfermero.
     * 
     * @param enfermero Nuevo enfermero
     * @throws IllegalArgumentException si el enfermero es nulo o no es un enfermero
     */
    public void actualizarEnfermero(Usuario enfermero) {
        if (enfermero == null) {
            throw new IllegalArgumentException("El enfermero no puede ser nulo");
        }
        if (!enfermero.esEnfermero()) {
            throw new IllegalArgumentException("El usuario debe ser un enfermero");
        }
        this.enfermero = enfermero;
    }
}
