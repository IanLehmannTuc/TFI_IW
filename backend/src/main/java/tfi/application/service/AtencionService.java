package tfi.application.service;

import org.springframework.stereotype.Service;
import tfi.application.dto.AtencionResponse;
import tfi.application.mapper.AtencionMapper;
import tfi.domain.entity.Atencion;
import tfi.domain.entity.Ingreso;
import tfi.domain.repository.AtencionRepository;
import tfi.domain.repository.IngresoRepository;
import tfi.exception.AtencionException;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para gestionar atenciones médicas de ingresos en urgencias.
 * Implementa la lógica de negocio para registrar y consultar atenciones.
 */
@Service
public class AtencionService {

    private final AtencionRepository atencionRepository;
    private final IngresoRepository ingresoRepository;
    private final AtencionMapper atencionMapper;

    public AtencionService(AtencionRepository atencionRepository, 
                          IngresoRepository ingresoRepository,
                          AtencionMapper atencionMapper) {
        this.atencionRepository = atencionRepository;
        this.ingresoRepository = ingresoRepository;
        this.atencionMapper = atencionMapper;
    }

    /**
     * Registra una nueva atención médica para un ingreso.
     * Valida que:
     * - El informe no esté vacío
     * - El ingreso exista
     * - El ingreso esté en estado EN_PROCESO
     * - No exista ya una atención para ese ingreso
     * 
     * Al registrar la atención:
     * - Crea la atención con médico, informe y fecha actual
     * - Actualiza el ingreso para que apunte a la atención
     * - Cambia el estado del ingreso a FINALIZADO
     * 
     * @param ingresoId ID del ingreso a atender
     * @param medicoId ID del médico que realiza la atención
     * @param informe Informe médico de la atención
     * @return La atención registrada como DTO
     * @throws AtencionException si hay algún error de validación
     */
    public AtencionResponse registrarAtencion(String ingresoId, String medicoId, String informe) {

        if (informe == null || informe.trim().isEmpty()) {
            throw new AtencionException("El informe del paciente es obligatorio");
        }


        Optional<Ingreso> ingresoOpt = ingresoRepository.findById(ingresoId);
        if (ingresoOpt.isEmpty()) {
            throw new AtencionException("No se encontró el ingreso con ID: " + ingresoId);
        }

        Ingreso ingreso = ingresoOpt.get();


        try {
            ingreso.puedeRecibirAtencion();
        } catch (IllegalStateException e) {
            throw new AtencionException(e.getMessage());
        }


        Optional<Atencion> atencionExistente = atencionRepository.findByIngresoId(ingresoId);
        if (atencionExistente.isPresent()) {
            throw new AtencionException("Ya existe una atención registrada para este ingreso");
        }


        Atencion atencion = new Atencion(ingresoId, medicoId, informe);
        Atencion atencionGuardada = atencionRepository.add(atencion);


        ingreso.finalizar(atencionGuardada);
        ingresoRepository.update(ingreso);

        return atencionMapper.toResponse(atencionGuardada);
    }

    /**
     * Obtiene la atención asociada a un ingreso específico.
     * 
     * @param ingresoId ID del ingreso
     * @return AtencionResponse con la atención si existe
     * @throws AtencionException si no se encuentra la atención
     */
    public AtencionResponse obtenerAtencionPorIngresoId(String ingresoId) {
        if (ingresoId == null || ingresoId.trim().isEmpty()) {
            throw new AtencionException("El ID del ingreso no puede ser nulo o vacío");
        }

        Atencion atencion = atencionRepository.findByIngresoId(ingresoId)
            .orElseThrow(() -> new AtencionException("No se encontró una atención para el ingreso con ID: " + ingresoId));

        return atencionMapper.toResponse(atencion);
    }

    /**
     * Obtiene una atención por su ID.
     * 
     * @param id ID de la atención
     * @return AtencionResponse con la atención si existe
     * @throws AtencionException si no se encuentra la atención
     */
    public AtencionResponse obtenerAtencionPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new AtencionException("El ID de la atención no puede ser nulo o vacío");
        }

        Atencion atencion = atencionRepository.findById(id)
            .orElseThrow(() -> new AtencionException("No se encontró la atención con ID: " + id));

        return atencionMapper.toResponse(atencion);
    }
}
