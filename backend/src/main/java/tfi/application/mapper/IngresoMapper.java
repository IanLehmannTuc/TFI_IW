package tfi.application.mapper;

import org.springframework.stereotype.Component;
import tfi.application.dto.IngresoResponse;
import tfi.domain.entity.Ingreso;

/**
 * Mapper para convertir entidades Ingreso a DTOs IngresoResponse.
 * Centraliza la lógica de mapeo para evitar duplicación de código.
 */
@Component
public class IngresoMapper {

    /**
     * Convierte un Ingreso a IngresoResponse.
     * 
     * @param ingreso La entidad Ingreso a convertir
     * @return El DTO IngresoResponse con los datos del ingreso
     */
    public IngresoResponse toResponse(Ingreso ingreso) {
        if (ingreso == null) {
            return null;
        }

        return new IngresoResponse(
            ingreso.getId(),
            ingreso.getPaciente() != null ? ingreso.getPaciente().getCuil() : null,
            ingreso.getPaciente() != null ? ingreso.getPaciente().getNombre() : null,
            ingreso.getPaciente() != null ? ingreso.getPaciente().getApellido() : null,
            ingreso.getEnfermero() != null ? ingreso.getEnfermero().getCuil() : null,
            ingreso.getEnfermero() != null ? ingreso.getEnfermero().getMatricula() : null,
            ingreso.getDescripcion(),
            ingreso.getFechaHoraIngreso(),
            ingreso.getTemperatura() != null ? ingreso.getTemperatura().getValor() : null,
            ingreso.getTensionArterial() != null ? ingreso.getTensionArterial().getPresionSistolica().getValor() : null,
            ingreso.getTensionArterial() != null ? ingreso.getTensionArterial().getPresionDiastolica().getValor() : null,
            ingreso.getFrecuenciaCardiaca() != null ? ingreso.getFrecuenciaCardiaca().getValor() : null,
            ingreso.getFrecuenciaRespiratoria() != null ? ingreso.getFrecuenciaRespiratoria().getValor() : null,
            ingreso.getNivelEmergencia(),
            ingreso.getEstado()
        );
    }
}

