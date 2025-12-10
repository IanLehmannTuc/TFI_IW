package tfi.application.mapper;

import org.springframework.stereotype.Component;
import tfi.application.dto.PacienteResponse;
import tfi.application.service.ObraSocialCacheService;
import tfi.domain.entity.Paciente;

/**
 * Mapper para convertir entidades Paciente a DTOs PacienteResponse.
 * Centraliza la lógica de mapeo para evitar duplicación de código.
 * 
 * Enriquece los nombres de obras sociales usando el cache para evitar llamadas innecesarias a la API externa.
 */
@Component
public class PacienteMapper {

    private final ObraSocialCacheService obraSocialCacheService;

    public PacienteMapper(ObraSocialCacheService obraSocialCacheService) {
        this.obraSocialCacheService = obraSocialCacheService;
    }

    /**
     * Convierte un Paciente a PacienteResponse.
     * Enriquece el nombre de la obra social desde el cache si es necesario.
     * 
     * @param paciente La entidad Paciente a convertir
     * @return El DTO PacienteResponse con los datos del paciente
     */
    public PacienteResponse toResponse(Paciente paciente) {
        if (paciente == null) {
            return null;
        }

        PacienteResponse.DomicilioResponse domicilioResponse = null;
        if (paciente.getDomicilio() != null) {
            domicilioResponse = new PacienteResponse.DomicilioResponse(
                paciente.getDomicilio().getCalle(),
                paciente.getDomicilio().getNumero(),
                paciente.getDomicilio().getLocalidad()
            );
        }

        PacienteResponse.AfiliadoResponse afiliadoResponse = null;
        if (paciente.getObraSocial() != null && paciente.getObraSocial().getObraSocial() != null) {
            Integer obraSocialId = paciente.getObraSocial().getObraSocial().getId();
            String nombreObraSocial = paciente.getObraSocial().getObraSocial().getNombre();



            if (nombreObraSocial != null && nombreObraSocial.matches("^Obra Social \\d+$")) {
                nombreObraSocial = obraSocialCacheService.getNombreObraSocial(obraSocialId);
            } else if (nombreObraSocial == null || nombreObraSocial.trim().isEmpty()) {

                nombreObraSocial = obraSocialCacheService.getNombreObraSocial(obraSocialId);
            }

            PacienteResponse.ObraSocialResponse obraSocialResponse = new PacienteResponse.ObraSocialResponse(
                obraSocialId,
                nombreObraSocial
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

