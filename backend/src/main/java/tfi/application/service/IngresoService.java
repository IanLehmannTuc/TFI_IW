package tfi.application.service;

import tfi.application.dto.IngresoResponse;
import tfi.application.dto.RegistroIngresoRequest;
import tfi.application.dto.RegistroPacienteRequest;
import tfi.application.mapper.IngresoMapper;
import tfi.domain.entity.Afiliado;
import tfi.domain.entity.Usuario;
import tfi.domain.entity.Ingreso;
import tfi.domain.entity.ObraSocial;
import tfi.domain.entity.Paciente;
import tfi.domain.repository.PacientesRepository;
import tfi.domain.repository.UsuarioRepository;
import tfi.domain.repository.IngresoRepository;
import tfi.domain.service.ColaAtencionService;
import tfi.domain.valueObject.Domicilio;
import tfi.domain.valueObject.FrecuenciaCardiaca;
import tfi.domain.valueObject.FrecuenciaRespiratoria;
import tfi.domain.valueObject.Presion;
import tfi.domain.valueObject.Temperatura;
import tfi.domain.valueObject.TensionArterial;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar ingresos de pacientes.
 * Maneja la lógica de negocio relacionada con el registro, actualización,
 * eliminación y consulta de ingresos.
 */
@Service
public class IngresoService {
    private PacientesRepository pacientesRepository;
    private UsuarioRepository usuarioRepository;
    private IngresoRepository ingresoRepository;
    private ColaAtencionService colaAtencionService;
    private IngresoMapper ingresoMapper;

    public IngresoService(PacientesRepository pacientesRepository, 
                          UsuarioRepository usuarioRepository, 
                          IngresoRepository ingresoRepository,
                          ColaAtencionService colaAtencionService,
                          IngresoMapper ingresoMapper) {
        this.pacientesRepository = pacientesRepository;
        this.usuarioRepository = usuarioRepository;
        this.ingresoRepository = ingresoRepository;
        this.colaAtencionService = colaAtencionService;
        this.ingresoMapper = ingresoMapper;
    }

    /**
     * Registra un nuevo ingreso de paciente.
     * Si el paciente no existe, lo crea automáticamente con los datos opcionales proporcionados.
     * Persiste el ingreso en el repositorio y lo agrega a la cola de atención.
     * 
     * @param ingresoDto DTO con los datos del ingreso y datos opcionales del paciente
     * @return el ingreso registrado como IngresoResponse
     */
    public IngresoResponse registrarIngreso(RegistroIngresoRequest ingresoDto) {
        
        Paciente paciente = pacientesRepository.findByCuil(ingresoDto.getPacienteCuil())
            .orElseGet(() -> {
                
                String cuil = ingresoDto.getPacienteCuil();
                String nombre = ingresoDto.getPacienteNombre();
                String apellido = ingresoDto.getPacienteApellido();
                String email = ingresoDto.getPacienteEmail();
                RegistroPacienteRequest.DomicilioRequest domicilioDto = ingresoDto.getPacienteDomicilio();
                RegistroPacienteRequest.AfiliadoRequest obraSocialDto = ingresoDto.getPacienteObraSocial();
                
                Domicilio domicilio = null;
                if (domicilioDto != null && domicilioDto.getCalle() != null && 
                    domicilioDto.getNumero() != null && domicilioDto.getLocalidad() != null) {
                    try {
                        domicilio = new Domicilio(
                            domicilioDto.getCalle(),
                            domicilioDto.getNumero(),
                            domicilioDto.getLocalidad()
                        );
                    } catch (IllegalArgumentException e) {
                        
                    }
                }
                
                Afiliado afiliado = null;
                if (obraSocialDto != null && obraSocialDto.getObraSocial() != null && 
                    obraSocialDto.getObraSocial().getId() != null &&
                    obraSocialDto.getNumeroAfiliado() != null && 
                    !obraSocialDto.getNumeroAfiliado().trim().isEmpty()) {
                    ObraSocial obraSocial = new ObraSocial(
                        obraSocialDto.getObraSocial().getId(),
                        obraSocialDto.getObraSocial().getNombre() != null 
                            ? obraSocialDto.getObraSocial().getNombre()
                            : "Obra Social " + obraSocialDto.getObraSocial().getId()
                    );
                    afiliado = new Afiliado(obraSocial, obraSocialDto.getNumeroAfiliado());
                }
                
                Paciente nuevoPaciente;
                if (nombre != null && apellido != null && email != null && domicilio != null && afiliado != null) {
                    nuevoPaciente = Paciente.crearCompleto(cuil, nombre, apellido, email, domicilio, afiliado);
                } else if (nombre != null && apellido != null && domicilio != null && afiliado != null) {
                    nuevoPaciente = Paciente.crearCompleto(cuil, nombre, apellido, null, domicilio, afiliado);
                } else if (nombre != null && apellido != null) {
                    nuevoPaciente = Paciente.crearConDatosBasicos(cuil, nombre, apellido);
                } else if (domicilio != null && afiliado != null) {
                    nuevoPaciente = Paciente.crearConDomicilioYObraSocial(cuil, domicilio, afiliado);
                } else {
                    nuevoPaciente = Paciente.crearConDatosBasicos(cuil, "Desconocido", "Desconocido");
                }
                
                return pacientesRepository.add(nuevoPaciente);
            });
        
        Usuario enfermero = usuarioRepository.findByCuil(ingresoDto.getEnfermeroCuil())
            .orElseThrow(() -> new IllegalArgumentException("Enfermero no encontrado con CUIL: " + ingresoDto.getEnfermeroCuil()));
        
        Temperatura temperatura = new Temperatura(ingresoDto.getTemperatura());
        TensionArterial tensionArterial = new TensionArterial(
            new Presion(ingresoDto.getTensionSistolica()),
            new Presion(ingresoDto.getTensionDiastolica())
        );
        FrecuenciaCardiaca frecuenciaCardiaca = new FrecuenciaCardiaca(ingresoDto.getFrecuenciaCardiaca());
        FrecuenciaRespiratoria frecuenciaRespiratoria = new FrecuenciaRespiratoria(ingresoDto.getFrecuenciaRespiratoria());
        
        Ingreso ingreso = new Ingreso(
            paciente,
            enfermero,
            ingresoDto.getDescripcion(),
            temperatura,
            tensionArterial,
            frecuenciaCardiaca,
            frecuenciaRespiratoria,
            ingresoDto.getNivelEmergencia()
        );

        Ingreso ingresoGuardado = ingresoRepository.add(ingreso);
        
        colaAtencionService.agregarACola(ingresoGuardado);

        IngresoResponse ingresoResponse = ingresoMapper.toResponse(ingresoGuardado);
        return ingresoResponse;
    }

    /**
     * Obtiene la cola de atención ordenada por:
     * 1. Prioridad (nivel de emergencia descendente - mayor prioridad primero)
     * 2. Orden de llegada (fecha/hora ascendente - primero en llegar, primero en la cola)
     * 
     * La cola se mantiene automáticamente ordenada mediante PriorityQueue en el servicio Singleton.
     * 
     * @return lista de ingresos como DTOs ordenados según criterio de cola de atención
     */
    public List<IngresoResponse> obtenerColaDeAtencion() {
        List<Ingreso> cola = this.colaAtencionService.obtenerCola();
        return cola.stream()
            .map(ingresoMapper::toResponse)
            .collect(Collectors.toList());
    }
    
    /**
     * Atiende al siguiente paciente en la cola (el de mayor prioridad).
     * Remueve el ingreso de la cola, inicia su atención (cambia estado a EN_PROCESO) y lo persiste.
     * 
     * @return el ingreso atendido como DTO, o null si no hay pacientes en espera
     * @throws IllegalStateException si el ingreso no puede ser atendido (no está pendiente)
     */
    public IngresoResponse atenderSiguientePaciente() {
        Ingreso ingreso = colaAtencionService.atenderSiguiente();
        
        if (ingreso != null) {
            // Usar método de negocio en lugar de setter directo
            ingreso.iniciarAtencion();
            
            ingresoRepository.update(ingreso);
            return ingresoMapper.toResponse(ingreso);
        }
        
        return null;
    }
    
    /**
     * Elimina un ingreso del sistema (método interno).
     * Útil cuando un paciente es dado de alta o transferido.
     * 
     * @param ingreso el ingreso a eliminar
     * @return el ingreso eliminado
     * @deprecated Usar eliminarIngreso(String id) en su lugar.
     *             Este método se mantiene para compatibilidad interna.
     */
    @Deprecated
    private Ingreso eliminarIngresoInterno(Ingreso ingreso) {
        colaAtencionService.removerDeCola(ingreso);
        return ingresoRepository.delete(ingreso);
    }
    
    /**
     * Actualiza un ingreso existente (método interno).
     * Si cambió la prioridad, actualiza su posición en la cola.
     * 
     * @param ingreso el ingreso con datos actualizados
     * @return el ingreso actualizado
     * @deprecated Usar actualizarIngreso(String id, RegistroIngresoRequest request) en su lugar.
     *             Este método se mantiene para compatibilidad interna.
     */
    @Deprecated
    private Ingreso actualizarIngresoInterno(Ingreso ingreso) {
        Ingreso ingresoViejo = ingresoRepository.findById(ingreso.getId())
            .orElseThrow(() -> new IllegalStateException("Ingreso no encontrado"));
        Ingreso ingresoActualizado = ingresoRepository.update(ingreso);
        colaAtencionService.actualizarEnCola(ingresoViejo, ingresoActualizado);
        return ingresoActualizado;
    }
    
    /**
     * Consulta el siguiente paciente a atender sin removerlo de la cola.
     * 
     * @return el ingreso de mayor prioridad como DTO, o null si la cola está vacía
     */
    public IngresoResponse verSiguientePaciente() {
        Ingreso siguiente = colaAtencionService.verSiguiente();
        return siguiente != null ? ingresoMapper.toResponse(siguiente) : null;
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
     * @return lista de todos los ingresos como DTOs
     */
    public List<IngresoResponse> obtenerTodosLosIngresos() {
        List<Ingreso> ingresos = ingresoRepository.findAll();
        return ingresos.stream()
            .map(ingresoMapper::toResponse)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene un ingreso por su ID.
     * 
     * @param id ID del ingreso a buscar
     * @return IngresoResponse con los datos del ingreso
     * @throws IllegalArgumentException si el ingreso no existe
     */
    public IngresoResponse obtenerIngresoPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del ingreso no puede ser nulo o vacío");
        }
        
        Ingreso ingreso = ingresoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró un ingreso con ID: " + id));
        
        return ingresoMapper.toResponse(ingreso);
    }

    /**
     * Actualiza un ingreso existente.
     * Valida que el ingreso exista y que los datos sean válidos.
     * Usa métodos de negocio de la entidad para mantener las invariantes.
     * 
     * @param id ID del ingreso a actualizar
     * @param request Datos actualizados del ingreso
     * @return IngresoResponse con los datos del ingreso actualizado
     * @throws IllegalArgumentException si el ingreso no existe o los datos son inválidos
     */
    public IngresoResponse actualizarIngreso(String id, RegistroIngresoRequest request) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del ingreso no puede ser nulo o vacío");
        }
        
        Ingreso ingresoExistente = ingresoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró un ingreso con ID: " + id));
        
        // Buscar paciente
        Paciente paciente = pacientesRepository.findByCuil(request.getPacienteCuil())
            .orElseThrow(() -> new IllegalArgumentException("No se encontró un paciente con CUIL: " + request.getPacienteCuil()));
        
        // Buscar enfermero
        Usuario enfermero = usuarioRepository.findByCuil(request.getEnfermeroCuil())
            .orElseThrow(() -> new IllegalArgumentException("No se encontró un enfermero con CUIL: " + request.getEnfermeroCuil()));
        
        // Crear value objects
        Temperatura temperatura = new Temperatura(request.getTemperatura());
        TensionArterial tensionArterial = new TensionArterial(
            new Presion(request.getTensionSistolica()),
            new Presion(request.getTensionDiastolica())
        );
        FrecuenciaCardiaca frecuenciaCardiaca = new FrecuenciaCardiaca(request.getFrecuenciaCardiaca());
        FrecuenciaRespiratoria frecuenciaRespiratoria = new FrecuenciaRespiratoria(request.getFrecuenciaRespiratoria());
        
        // Usar métodos de negocio de la entidad para actualizar
        ingresoExistente.actualizarPaciente(paciente);
        ingresoExistente.actualizarEnfermero(enfermero);
        ingresoExistente.actualizarDescripcion(request.getDescripcion());
        ingresoExistente.actualizarVitales(temperatura, tensionArterial, frecuenciaCardiaca, frecuenciaRespiratoria);
        ingresoExistente.actualizarNivelEmergencia(request.getNivelEmergencia());
        
        // Actualizar en repositorio y cola
        Ingreso ingresoGuardado = ingresoRepository.update(ingresoExistente);
        colaAtencionService.actualizarEnCola(ingresoExistente, ingresoGuardado);
        
        return ingresoMapper.toResponse(ingresoGuardado);
    }

    /**
     * Elimina un ingreso del sistema por su ID.
     * 
     * @param id ID del ingreso a eliminar
     * @throws IllegalArgumentException si el ingreso no existe
     */
    public void eliminarIngreso(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del ingreso no puede ser nulo o vacío");
        }
        
        Ingreso ingreso = ingresoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró un ingreso con ID: " + id));
        
        colaAtencionService.removerDeCola(ingreso);
        ingresoRepository.delete(ingreso);
    }
}

