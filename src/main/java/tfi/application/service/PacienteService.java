package tfi.application.service;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import tfi.exception.PacienteException;
import tfi.application.mapper.PacienteMapper;
import tfi.application.dto.PacienteResponse;
import tfi.application.dto.RegistroPacienteRequest;
import tfi.domain.entity.Afiliado;
import tfi.domain.entity.ObraSocial;
import tfi.domain.entity.Paciente;
import tfi.domain.valueObject.Domicilio;
import tfi.domain.repository.PacientesRepository;
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
    public PacienteResponse registrar(@NonNull RegistroPacienteRequest dto) {
        if (pacientesRepository.existsByCuil(dto.getCuil())) {
            throw new PacienteException(MensajesError.CUIL_YA_REGISTRADO);
        }
        
        Domicilio domicilio;
        try {
            domicilio = new Domicilio(
                dto.getDomicilio().getCalle(),
                dto.getDomicilio().getNumero(),
                dto.getDomicilio().getLocalidad()
            );
        } catch (IllegalArgumentException e) {
            throw new PacienteException(e.getMessage());
        }
        
        Afiliado afiliado = null;
        if (dto.getObraSocial() != null) {
            if (dto.getObraSocial().getNumeroAfiliado() == null || 
                dto.getObraSocial().getNumeroAfiliado().trim().isEmpty()) {
                throw new PacienteException("El número de afiliado es obligatorio cuando se especifica obra social");
            }
            
            ObraSocial obraSocial = new ObraSocial(
                dto.getObraSocial().getObraSocial().getIdObraSocial(),
                dto.getObraSocial().getObraSocial().getNombreObraSocial() != null 
                    ? dto.getObraSocial().getObraSocial().getNombreObraSocial()
                    : "Obra Social " + dto.getObraSocial().getObraSocial().getIdObraSocial()
            );
            
            afiliado = new Afiliado(obraSocial, dto.getObraSocial().getNumeroAfiliado());
        }
        
        Paciente paciente = new Paciente(
            dto.getCuil(),
            dto.getNombre(),
            dto.getApellido(),
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
