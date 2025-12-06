package tfi.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.Optional;

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
                dto.getObraSocial().getObraSocial().getId(),
                dto.getObraSocial().getObraSocial().getNombre() != null 
                    ? dto.getObraSocial().getObraSocial().getNombre()
                    : "Obra Social " + dto.getObraSocial().getObraSocial().getId()
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
     * @return Un Optional con el paciente si existe, vacío en caso contrario
     */
    public Optional<Paciente> findByCuil(String cuil) {
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
    
    /**
     * Obtiene todos los pacientes con paginación.
     * 
     * @param pageable información de paginación (página, tamaño, ordenamiento)
     * @return página de pacientes con metadatos de paginación
     */
    public Page<Paciente> findAll(Pageable pageable) {
        return pacientesRepository.findAll(pageable);
    }
    
    /**
     * Actualiza los datos de un paciente existente.
     * Valida que el paciente exista antes de actualizar.
     * 
     * @param cuil El CUIL del paciente a actualizar
     * @param dto Datos actualizados del paciente
     * @return Respuesta con los datos del paciente actualizado
     * @throws PacienteException Si el paciente no existe o los datos son inválidos
     */
    public PacienteResponse actualizar(@NonNull String cuil, @NonNull RegistroPacienteRequest dto) {
        if (cuil == null || cuil.trim().isEmpty()) {
            throw new PacienteException("El CUIL no puede ser nulo o vacío");
        }
        
        // Verificar que el paciente existe
        Paciente pacienteExistente = pacientesRepository.findByCuil(cuil)
            .orElseThrow(() -> new PacienteException("No existe un paciente con el CUIL: " + cuil));
        
        // Validar que el CUIL del DTO coincida con el del path
        if (!cuil.equals(dto.getCuil())) {
            throw new PacienteException("El CUIL en el body debe coincidir con el CUIL en la URL");
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
                dto.getObraSocial().getObraSocial().getId(),
                dto.getObraSocial().getObraSocial().getNombre() != null 
                    ? dto.getObraSocial().getObraSocial().getNombre()
                    : "Obra Social " + dto.getObraSocial().getObraSocial().getId()
            );
            
            afiliado = new Afiliado(obraSocial, dto.getObraSocial().getNumeroAfiliado());
        }
        
        // Crear paciente actualizado manteniendo el ID original
        Paciente pacienteActualizado = new Paciente(
            dto.getCuil(),
            dto.getNombre(),
            dto.getApellido(),
            pacienteExistente.getEmail(), // Mantener el email existente si no se proporciona
            domicilio,
            afiliado
        );
        pacienteActualizado.setId(pacienteExistente.getId());
        
        pacientesRepository.update(pacienteActualizado);
        
        return pacienteMapper.toResponse(pacienteActualizado);
    }
    
    /**
     * Elimina un paciente del sistema por su CUIL.
     * 
     * @param cuil El CUIL del paciente a eliminar
     * @return Respuesta con los datos del paciente eliminado
     * @throws PacienteException Si el paciente no existe
     */
    public PacienteResponse eliminar(@NonNull String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            throw new PacienteException("El CUIL no puede ser nulo o vacío");
        }
        
        Paciente paciente = pacientesRepository.findByCuil(cuil)
            .orElseThrow(() -> new PacienteException("No existe un paciente con el CUIL: " + cuil));
        
        pacientesRepository.delete(paciente);
        
        return pacienteMapper.toResponse(paciente);
    }
}
