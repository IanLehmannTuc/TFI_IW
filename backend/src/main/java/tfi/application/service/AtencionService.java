package tfi.application.service;

import org.springframework.stereotype.Service;
import tfi.domain.entity.Atencion;
import tfi.domain.entity.Ingreso;
import tfi.domain.enums.Estado;
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

    public AtencionService(AtencionRepository atencionRepository, IngresoRepository ingresoRepository) {
        this.atencionRepository = atencionRepository;
        this.ingresoRepository = ingresoRepository;
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
     * @return La atención registrada
     * @throws AtencionException si hay algún error de validación
     */
    public Atencion registrarAtencion(String ingresoId, String medicoId, String informe) {
        
        if (informe == null || informe.trim().isEmpty()) {
            throw new AtencionException("El informe del paciente es obligatorio");
        }

        
        Optional<Ingreso> ingresoOpt = ingresoRepository.findById(ingresoId);
        if (ingresoOpt.isEmpty()) {
            throw new AtencionException("No se encontró el ingreso con ID: " + ingresoId);
        }

        Ingreso ingreso = ingresoOpt.get();

        
        if (ingreso.getEstado() != Estado.EN_PROCESO) {
            throw new AtencionException("El ingreso debe estar en estado EN_PROCESO para registrar una atención. Estado actual: " + ingreso.getEstado());
        }

        
        Optional<Atencion> atencionExistente = atencionRepository.findByIngresoId(ingresoId);
        if (atencionExistente.isPresent()) {
            throw new AtencionException("Ya existe una atención registrada para este ingreso");
        }

        
        Atencion atencion = new Atencion(ingresoId, medicoId, informe);
        Atencion atencionGuardada = atencionRepository.add(atencion);

        // Usar método de negocio en lugar de setter directo
        ingreso.finalizar(atencionGuardada);
        ingresoRepository.update(ingreso);

        return atencionGuardada;
    }

    /**
     * Obtiene la atención asociada a un ingreso específico.
     * 
     * @param ingresoId ID del ingreso
     * @return Optional con la atención si existe, vacío si no
     */
    public Optional<Atencion> obtenerAtencionPorIngresoId(String ingresoId) {
        return atencionRepository.findByIngresoId(ingresoId);
    }

    /**
     * Obtiene una atención por su ID.
     * 
     * @param id ID de la atención
     * @return Optional con la atención si existe, vacío si no
     */
    public Optional<Atencion> obtenerAtencionPorId(String id) {
        return atencionRepository.findById(id);
    }
}
