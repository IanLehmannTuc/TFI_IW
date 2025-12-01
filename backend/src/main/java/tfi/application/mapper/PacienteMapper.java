package tfi.application.mapper;

import org.springframework.stereotype.Component;
import tfi.application.dto.PacienteResponse;
import tfi.domain.entity.Paciente;

/**
 * Mapper para convertir entidades Paciente a DTOs PacienteResponse.
 * Centraliza la lógica de mapeo para evitar duplicación de código.
 */
@Component
public class PacienteMapper {
    
    /**
     * Convierte un Paciente a PacienteResponse.
     * 
     * @param paciente La entidad Paciente a convertir
     * @return El DTO PacienteResponse con los datos del paciente
     */
    public PacienteResponse toResponse(Paciente paciente) {
        if (paciente == null) {
            return null;
        }
        
        PacienteResponse.DomicilioResponse domicilioResponse = new PacienteResponse.DomicilioResponse(
            paciente.getDomicilio().getCalle(),
            paciente.getDomicilio().getNumero(),
            paciente.getDomicilio().getLocalidad()
        );
        
        PacienteResponse.AfiliadoResponse afiliadoResponse = null;
        if (paciente.getObraSocial() != null) {
            PacienteResponse.ObraSocialResponse obraSocialResponse = new PacienteResponse.ObraSocialResponse(
                paciente.getObraSocial().getObraSocial().getId(),
                paciente.getObraSocial().getObraSocial().getNombre()
            );
            afiliadoResponse = new PacienteResponse.AfiliadoResponse(
                obraSocialResponse,
                paciente.getObraSocial().getNumeroAfiliado()
            );
        }
        
        return new PacienteResponse(
            paciente.getId(),
            paciente.getCuil(),
            paciente.getNombre(),
            paciente.getApellido(),
            domicilioResponse,
            afiliadoResponse
        );
    }
}

