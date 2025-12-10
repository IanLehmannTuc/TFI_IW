package tfi.application.mapper;

import org.springframework.stereotype.Component;
import tfi.application.dto.AtencionResponse;
import tfi.domain.entity.Atencion;

/**
 * Mapper para convertir entidades Atencion a DTOs AtencionResponse.
 * Centraliza la lógica de mapeo para evitar duplicación de código.
 */
@Component
public class AtencionMapper {

    /**
     * Convierte una Atencion a AtencionResponse.
     * 
     * @param atencion La entidad Atencion a convertir
     * @return El DTO AtencionResponse con los datos de la atención
     */
    public AtencionResponse toResponse(Atencion atencion) {
        if (atencion == null) {
            return null;
        }

        return new AtencionResponse(
            atencion.getId(),
            atencion.getIngresoId(),
            atencion.getMedicoId(),
            atencion.getInformeMedico(),
            atencion.getFechaAtencion()
        );
    }
}
