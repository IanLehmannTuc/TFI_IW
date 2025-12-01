package tfi.application.service;

import tfi.domain.entity.Ingreso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import org.springframework.stereotype.Service;
/**
 * Servicio Singleton para gestionar la cola de atención de urgencias.
 * Mantiene una cola de prioridad ordenada automáticamente por:
 * 1. Nivel de emergencia (mayor prioridad primero)
 * 2. Orden de llegada (primero en llegar, primero en atender)
 */
@Service
public class ColaAtencionService {
    
    private static ColaAtencionService instancia;
    private final PriorityQueue<Ingreso> colaAtencion;
    
    /**
     * Constructor privado para patrón Singleton.
     */
    private ColaAtencionService() {
        this.colaAtencion = new PriorityQueue<>(
            Comparator.comparing((Ingreso i) -> i.getNivelEmergencia().getPrioridad())
                .reversed()  
                .thenComparing(Ingreso::getFechaHoraIngreso)  
        );
    }
    
    /**
     * Obtiene la instancia única del servicio (Singleton).
     * Thread-safe mediante sincronización.
     * 
     * @return la instancia única de ColaAtencionService
     */
    public static synchronized ColaAtencionService getInstance() {
        if (instancia == null) {
            instancia = new ColaAtencionService();
        }
        return instancia;
    }
    
    /**
     * Agrega un ingreso a la cola de atención.
     * Se inserta automáticamente en la posición correcta según prioridad.
     * 
     * @param ingreso el ingreso a agregar a la cola
     */
    public synchronized void agregarACola(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }
        colaAtencion.offer(ingreso);
    }
    
    /**
     * Obtiene la cola de atención completa ordenada por prioridad.
     * No modifica la cola, solo retorna una copia.
     * 
     * @return lista ordenada de ingresos pendientes de atención
     */
    public synchronized List<Ingreso> obtenerCola() {
        return new ArrayList<>(colaAtencion);
    }
    
    /**
     * Atiende y remueve el siguiente paciente de la cola (el de mayor prioridad).
     * 
     * @return el ingreso de mayor prioridad, o null si la cola está vacía
     */
    public synchronized Ingreso atenderSiguiente() {
        return colaAtencion.poll();
    }
    
    /**
     * Remueve un ingreso específico de la cola.
     * Útil cuando un paciente es dado de alta o transferido.
     * 
     * @param ingreso el ingreso a remover
     * @return true si se removió, false si no estaba en la cola
     */
    public synchronized boolean removerDeCola(Ingreso ingreso) {
        return colaAtencion.remove(ingreso);
    }
    
    /**
     * Actualiza un ingreso en la cola (útil si cambia su prioridad).
     * Remueve el ingreso viejo y agrega el nuevo en la posición correcta.
     * 
     * @param ingresoViejo el ingreso a actualizar (referencia actual)
     * @param ingresoNuevo el ingreso con datos actualizados
     */
    public synchronized void actualizarEnCola(Ingreso ingresoViejo, Ingreso ingresoNuevo) {
        colaAtencion.remove(ingresoViejo);
        colaAtencion.offer(ingresoNuevo);
    }
    
    /**
     * Verifica si un ingreso está en la cola de atención.
     * 
     * @param ingreso el ingreso a verificar
     * @return true si está en la cola, false en caso contrario
     */
    public synchronized boolean estaEnCola(Ingreso ingreso) {
        return colaAtencion.contains(ingreso);
    }
    
    /**
     * Obtiene la cantidad de pacientes en espera.
     * 
     * @return cantidad de ingresos en la cola
     */
    public synchronized int cantidadEnEspera() {
        return colaAtencion.size();
    }
    
    /**
     * Consulta el siguiente paciente a atender sin removerlo de la cola.
     * 
     * @return el ingreso de mayor prioridad, o null si la cola está vacía
     */
    public synchronized Ingreso verSiguiente() {
        return colaAtencion.peek();
    }
    
    /**
     * Limpia completamente la cola de atención.
     * PRECAUCIÓN: Esta operación es irreversible.
     * Útil para testing o reinicio del sistema.
     */
    public synchronized void limpiarCola() {
        colaAtencion.clear();
    }
    
    /**
     * Reconstruye la cola desde una lista de ingresos.
     * Útil para sincronizar con el repositorio después de un reinicio.
     * 
     * @param ingresos lista de ingresos a agregar a la cola
     */
    public synchronized void reconstruirCola(List<Ingreso> ingresos) {
        colaAtencion.clear();
        if (ingresos != null) {
            colaAtencion.addAll(ingresos);
        }
    }
    
    /**
     * Resetea la instancia singleton (solo para testing).
     * ADVERTENCIA: No usar en código de producción.
     */
    public static synchronized void resetInstance() {
        instancia = null;
    }
}

