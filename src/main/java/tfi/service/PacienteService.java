package tfi.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import tfi.exception.PacienteException;
import tfi.model.mapper.PacienteMapper;
import tfi.model.dto.PacienteResponse;
import tfi.model.dto.RegistroPacienteRequest;
import tfi.model.entity.Afiliado;
import tfi.model.entity.ObraSocial;
import tfi.model.entity.Paciente;
import tfi.model.valueObjects.Domicilio;
import tfi.repository.interfaces.PacientesRepository;
import tfi.util.MensajesError;

/**
 * Servicio para gestionar operaciones de registro y consulta de pacientes.
 * Implementa la lógica de negocio siguiendo principios de arquitectura limpia.
 */
@Service
public class PacienteService {
    
    private final PacientesRepository pacientesRepository;
    private final PacienteMapper pacienteMapper;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param pacientesRepository Repositorio de pacientes
     * @param pacienteMapper Mapper para convertir Paciente a PacienteResponse
     */
    public PacienteService(@NonNull PacientesRepository pacientesRepository,
                          @NonNull PacienteMapper pacienteMapper) {
        this.pacientesRepository = pacientesRepository;
        this.pacienteMapper = pacienteMapper;
    }

    /**
     * Registra un nuevo paciente en el sistema.
     * Valida que el CUIL no esté duplicado.
     * 
     * NOTA: La validación de obra social se realizará más adelante mediante una API externa.
     * Por ahora, la obra social es solo un atributo simple.
     * 
     * @param request Datos de registro del paciente
     * @return Respuesta con los datos del paciente registrado
     * @throws PacienteException Si los datos son inválidos o el CUIL ya existe
     * @throws IllegalArgumentException Si el request es null
     */
    public PacienteResponse registrar(@NonNull RegistroPacienteRequest request) {
        if (pacientesRepository.existsByCuil(request.getCuil())) {
            throw new PacienteException(MensajesError.CUIL_YA_REGISTRADO);
        }
        
        Domicilio domicilio;
        try {
            domicilio = new Domicilio(
                request.getDomicilio().getCalle(),
                request.getDomicilio().getNumero(),
                request.getDomicilio().getLocalidad()
            );
        } catch (IllegalArgumentException e) {
            throw new PacienteException(e.getMessage());
        }
        
        Afiliado afiliado = null;
        if (request.getObraSocial() != null) {
            if (request.getObraSocial().getNumeroAfiliado() == null || 
                request.getObraSocial().getNumeroAfiliado().trim().isEmpty()) {
                throw new PacienteException("El número de afiliado es obligatorio cuando se especifica obra social");
            }
            
            ObraSocial obraSocial = new ObraSocial(
                request.getObraSocial().getObraSocial().getIdObraSocial(),
                request.getObraSocial().getObraSocial().getNombreObraSocial() != null 
                    ? request.getObraSocial().getObraSocial().getNombreObraSocial()
                    : "Obra Social " + request.getObraSocial().getObraSocial().getIdObraSocial()
            );
            
            afiliado = new Afiliado(obraSocial, request.getObraSocial().getNumeroAfiliado());
        }
        
        Paciente paciente = new Paciente(
            request.getCuil(),
            request.getNombre(),
            request.getApellido(),
            null,
            domicilio,
            afiliado
        );
        
        pacientesRepository.add(paciente);
        
        return pacienteMapper.toResponse(paciente);
    }

    /**
     * Busca un paciente por su CUIL.
     * 
     * @param cuil El CUIL del paciente
     * @return El paciente si existe, null en caso contrario
     */
    public Paciente findByCuil(String cuil) {
        return pacientesRepository.findByCuil(cuil);
    }
    
    /**
     * Verifica si existe un paciente con el CUIL especificado.
     * 
     * @param cuil El CUIL a verificar
     * @return true si existe, false en caso contrario
     */
    public boolean existsByCuil(String cuil) {
        return pacientesRepository.existsByCuil(cuil);
    }
}
