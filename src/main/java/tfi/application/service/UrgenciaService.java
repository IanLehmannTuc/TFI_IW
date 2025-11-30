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
import tfi.domain.valueObject.Domicilio;
import tfi.domain.valueObject.FrecuenciaCardiaca;
import tfi.domain.valueObject.FrecuenciaRespiratoria;
import tfi.domain.valueObject.Presion;
import tfi.domain.valueObject.Temperatura;
import tfi.domain.valueObject.TensionArterial;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UrgenciaService {
    private PacientesRepository pacientesRepository;
    private UsuarioRepository usuarioRepository;
    private IngresoRepository ingresoRepository;
    private ColaAtencionService colaAtencionService;
    private IngresoMapper ingresoMapper;

    public UrgenciaService(PacientesRepository pacientesRepository, 
                          UsuarioRepository usuarioRepository, 
                          IngresoRepository ingresoRepository,
                          IngresoMapper ingresoMapper) {
        this.pacientesRepository = pacientesRepository;
        this.usuarioRepository = usuarioRepository;
        this.ingresoRepository = ingresoRepository;
        this.ingresoMapper = ingresoMapper;
        this.colaAtencionService = ColaAtencionService.getInstance();
    }

    /**
     * Registra un nuevo ingreso de paciente a urgencias.
     * Si el paciente no existe, lo crea automáticamente con los datos opcionales proporcionados.
     * Persiste el ingreso en el repositorio y lo agrega a la cola de atención.
     * 
     * @param ingresoDto DTO con los datos del ingreso y datos opcionales del paciente
     * @return el ingreso registrado como IngresoResponse
     */
    public IngresoResponse registrarIngreso(RegistroIngresoRequest ingresoDto) {
        Paciente paciente = pacientesRepository.findByCuil(ingresoDto.getPacienteCuil());
        
        if (paciente == null) {
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
                    // Si el domicilio es inválido, se deja como null
                }
            }
            
            Afiliado afiliado = null;
            if (obraSocialDto != null && obraSocialDto.getObraSocial() != null && 
                obraSocialDto.getObraSocial().getIdObraSocial() != null &&
                obraSocialDto.getNumeroAfiliado() != null && 
                !obraSocialDto.getNumeroAfiliado().trim().isEmpty()) {
                ObraSocial obraSocial = new ObraSocial(
                    obraSocialDto.getObraSocial().getIdObraSocial(),
                    obraSocialDto.getObraSocial().getNombreObraSocial() != null 
                        ? obraSocialDto.getObraSocial().getNombreObraSocial()
                        : "Obra Social " + obraSocialDto.getObraSocial().getIdObraSocial()
                );
                afiliado = new Afiliado(obraSocial, obraSocialDto.getNumeroAfiliado());
            }
            
            if (nombre != null && apellido != null && email != null && domicilio != null && afiliado != null) {
                paciente = new Paciente(cuil, nombre, apellido, email, domicilio, afiliado);
            } else if (nombre != null && apellido != null && domicilio != null && afiliado != null) {
                paciente = new Paciente(cuil, nombre, apellido, null, domicilio, afiliado);
            } else if (nombre != null && apellido != null) {
                paciente = new Paciente(cuil, nombre, apellido);
            } else if (domicilio != null && afiliado != null) {
                paciente = new Paciente(cuil, domicilio, afiliado);
            } else {
                paciente = new Paciente(cuil, "Desconocido", "Desconocido");
            }
            
            pacientesRepository.add(paciente);
        }
        
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