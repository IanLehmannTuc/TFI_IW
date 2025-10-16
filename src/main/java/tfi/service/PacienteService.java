package tfi.service;

import org.springframework.stereotype.Service;
import tfi.model.entity.Paciente;
import tfi.repository.interfaces.PacientesRepository;

import java.util.Optional;

@Service
public class PacienteService {
    
    private final PacientesRepository pacientesRepository;

    public PacienteService(PacientesRepository pacientesRepository) {
        this.pacientesRepository = pacientesRepository;
    }

    public Paciente findByCuil(String cuil) {
        return pacientesRepository.findByCuil(cuil);
    }
    
    public boolean existsByCuil(String cuil) {
        return pacientesRepository.existsByCuil(cuil);
    }
}
