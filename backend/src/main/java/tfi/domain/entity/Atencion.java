package tfi.domain.entity;

import java.time.LocalDateTime;

/**
 * Entidad que representa una atención médica realizada a un ingreso en urgencias.
 * Contiene el informe médico y los datos del médico que realizó la atención.
 * 
 * Esta entidad implementa Rich Domain Model con validaciones en el constructor
 * y métodos de negocio que encapsulan las reglas del dominio.
 */
public class Atencion {
    private String id;
    private String ingresoId;
    private String medicoId;
    private String informeMedico;
    private LocalDateTime fechaAtencion;

    /**
     * Constructor completo para crear una atención con todos los datos.
     * Usado principalmente al recuperar desde la base de datos.
     * 
     * @param id ID único de la atención
     * @param ingresoId ID del ingreso al que pertenece esta atención
     * @param medicoId ID del médico que realizó la atención
     * @param informeMedico Informe médico de la atención (no puede ser nulo o vacío)
     * @param fechaAtencion Fecha y hora en que se realizó la atención
     * @throws IllegalArgumentException si algún parámetro requerido es inválido
     */
    public Atencion(String id, String ingresoId, String medicoId, String informeMedico, LocalDateTime fechaAtencion) {
        validarParametros(ingresoId, medicoId, informeMedico);
        this.id = id;
        this.ingresoId = ingresoId;
        this.medicoId = medicoId;
        this.informeMedico = informeMedico.trim();
        this.fechaAtencion = fechaAtencion != null ? fechaAtencion : LocalDateTime.now();
    }

    /**
     * Constructor para crear una nueva atención sin ID.
     * El ID será asignado por el repositorio al persistir.
     * La fecha de atención se establece automáticamente al momento actual.
     * 
     * @param ingresoId ID del ingreso al que pertenece esta atención
     * @param medicoId ID del médico que realizó la atención
     * @param informeMedico Informe médico de la atención (no puede ser nulo o vacío)
     * @throws IllegalArgumentException si algún parámetro requerido es inválido
     */
    public Atencion(String ingresoId, String medicoId, String informeMedico) {
        validarParametros(ingresoId, medicoId, informeMedico);
        this.ingresoId = ingresoId;
        this.medicoId = medicoId;
        this.informeMedico = informeMedico.trim();
        this.fechaAtencion = LocalDateTime.now();
    }

    /**
     * Valida los parámetros requeridos para crear una atención.
     * 
     * @param ingresoId ID del ingreso
     * @param medicoId ID del médico
     * @param informeMedico Informe médico
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    private void validarParametros(String ingresoId, String medicoId, String informeMedico) {
        if (ingresoId == null || ingresoId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del ingreso no puede ser nulo o vacío");
        }
        if (medicoId == null || medicoId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del médico no puede ser nulo o vacío");
        }
        if (informeMedico == null || informeMedico.trim().isEmpty()) {
            throw new IllegalArgumentException("El informe médico es obligatorio y no puede estar vacío");
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIngresoId() {
        return ingresoId;
    }

    public void setIngresoId(String ingresoId) {
        this.ingresoId = ingresoId;
    }

    public String getMedicoId() {
        return medicoId;
    }

    public void setMedicoId(String medicoId) {
        this.medicoId = medicoId;
    }

    public String getInformeMedico() {
        return informeMedico;
    }

    /**
     * Método de negocio: Actualiza el informe médico de la atención.
     * Valida que el nuevo informe no sea nulo o vacío.
     * 
     * @param nuevoInforme Nuevo informe médico
     * @throws IllegalArgumentException si el informe es inválido
     */
    public void actualizarInforme(String nuevoInforme) {
        if (nuevoInforme == null || nuevoInforme.trim().isEmpty()) {
            throw new IllegalArgumentException("El informe médico es obligatorio y no puede estar vacío");
        }
        this.informeMedico = nuevoInforme.trim();
    }

    /**
     * Método de consulta: Verifica si la atención tiene un informe válido.
     * 
     * @return true si tiene informe no vacío, false en caso contrario
     */
    public boolean tieneInformeValido() {
        return informeMedico != null && !informeMedico.trim().isEmpty();
    }

    public LocalDateTime getFechaAtencion() {
        return fechaAtencion;
    }

    /**
     * Método específico para repositorios: Restaura el informe médico desde la base de datos.
     * SOLO debe usarse por repositorios al recuperar entidades desde BD.
     * NO debe usarse para actualizar el informe. Usar actualizarInforme() en su lugar.
     * 
     * @param informeMedico Informe a restaurar desde BD
     */
    public void restoreInformeMedicoFromPersistence(String informeMedico) {
        this.informeMedico = informeMedico;
    }

    /**
     * Método específico para repositorios: Restaura la fecha de atención desde la base de datos.
     * SOLO debe usarse por repositorios al recuperar entidades desde BD.
     * 
     * @param fechaAtencion Fecha a restaurar desde BD
     */
    public void restoreFechaAtencionFromPersistence(LocalDateTime fechaAtencion) {
        this.fechaAtencion = fechaAtencion;
    }
    
    /**
     * Setter para informe médico - SOLO para uso interno del repositorio al recuperar desde BD.
     * NO debe usarse para actualizar el informe. Usar actualizarInforme() en su lugar.
     * 
     * @deprecated Usar restoreInformeMedicoFromPersistence() en su lugar. Este método se mantiene por compatibilidad.
     */
    @Deprecated
    public void setInformeMedico(String informeMedico) {
        restoreInformeMedicoFromPersistence(informeMedico);
    }

    /**
     * Setter para fecha de atención - SOLO para uso interno del repositorio al recuperar desde BD.
     * 
     * @deprecated Usar restoreFechaAtencionFromPersistence() en su lugar. Este método se mantiene por compatibilidad.
     */
    @Deprecated
    public void setFechaAtencion(LocalDateTime fechaAtencion) {
        restoreFechaAtencionFromPersistence(fechaAtencion);
    }
}
