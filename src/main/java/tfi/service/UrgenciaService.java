package tfi.service;

import tfi.model.entity.Ingreso;
import tfi.model.entity.Paciente;
import tfi.repository.interfaces.PacientesRepository;
import tfi.repository.interfaces.EnfermeroRepository;
import tfi.repository.interfaces.IngresoRepository;

import java.util.List;

public class UrgenciaService {
    private PacientesRepository pacientesRepository;
    private EnfermeroRepository enfermeroRepository;
    private IngresoRepository ingresoRepository;
    private ColaAtencionService colaAtencionService;

    public UrgenciaService(PacientesRepository pacientesRepository, 
                          EnfermeroRepository enfermeroRepository, 
                          IngresoRepository ingresoRepository) {
        this.pacientesRepository = pacientesRepository;
        this.enfermeroRepository = enfermeroRepository;
        this.ingresoRepository = ingresoRepository;
        this.colaAtencionService = ColaAtencionService.getInstance();
    }

    /**
     * Registra un nuevo ingreso de paciente a urgencias.
     * Persiste el ingreso en el repositorio y lo agrega a la cola de atención.
     * 
     * @param ingreso el ingreso a registrar
     * @return el ingreso registrado
     */
    public Ingreso registrarIngreso(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }
        
        if (ingreso.getPaciente() == null) {
            throw new IllegalArgumentException("El ingreso debe tener un paciente");
        }
        
        if (ingreso.getNivelEmergencia() == null) {
            throw new IllegalArgumentException("El ingreso debe tener un nivel de emergencia");
        }
        
        Paciente paciente = ingreso.getPaciente();
        boolean pacienteExists = pacientesRepository.existsByCuil(paciente.getCuil());
        if (!pacienteExists) {
            pacientesRepository.add(paciente);
        }
        
        Ingreso ingresoGuardado = ingresoRepository.add(ingreso);
        
        colaAtencionService.agregarACola(ingresoGuardado);
        
        return ingresoGuardado;
    }

    /**
     * Obtiene la cola de atención ordenada por:
     * 1. Prioridad (nivel de emergencia descendente - mayor prioridad primero)
     * 2. Orden de llegada (fecha/hora ascendente - primero en llegar, primero en la cola)
     * 
     * La cola se mantiene automáticamente ordenada mediante PriorityQueue en el servicio Singleton.
     * 
     * @return lista de ingresos ordenados según criterio de cola de atención
     */
    public List<Ingreso> obtenerColaDeAtencion() {
        return this.colaAtencionService.obtenerCola();
    }
    
    /**
     * Atiende al siguiente paciente en la cola (el de mayor prioridad).
     * Remueve el ingreso de la cola pero NO lo elimina del repositorio.
     * 
     * @return el ingreso atendido, o null si no hay pacientes en espera
     */
    public Ingreso atenderSiguientePaciente() {
        return colaAtencionService.atenderSiguiente();
    }
    
    /**
     * Elimina un ingreso del sistema (repositorio y cola de atención).
     * Útil cuando un paciente es dado de alta o transferido.
     * 
     * @param ingreso el ingreso a eliminar
     * @return el ingreso eliminado
     */
    public Ingreso eliminarIngreso(Ingreso ingreso) {
        colaAtencionService.removerDeCola(ingreso);
        return ingresoRepository.delete(ingreso);
    }
    
    /**
     * Actualiza un ingreso existente.
     * Si cambió la prioridad, actualiza su posición en la cola.
     * 
     * @param ingreso el ingreso con datos actualizados
     * @return el ingreso actualizado
     */
    public Ingreso actualizarIngreso(Ingreso ingreso) {
        Ingreso ingresoViejo = ingresoRepository.findById(ingreso.getId())
            .orElseThrow(() -> new IllegalStateException("Ingreso no encontrado"));
        Ingreso ingresoActualizado = ingresoRepository.update(ingreso);
        colaAtencionService.actualizarEnCola(ingresoViejo, ingresoActualizado);
        return ingresoActualizado;
    }
    
    /**
     * Obtiene la cantidad de pacientes en espera en la cola.
     * 
     * @return cantidad de pacientes en espera
     */
    public int cantidadPacientesEnEspera() {
        return colaAtencionService.cantidadEnEspera();
    }

    /**
     * Obtiene todos los ingresos registrados sin ordenar.
     * @return lista de todos los ingresos
     */
    public List<Ingreso> obtenerTodosLosIngresos() {
        return ingresoRepository.findAll();
    }
}