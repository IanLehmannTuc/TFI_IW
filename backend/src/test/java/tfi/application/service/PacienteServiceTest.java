package tfi.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tfi.exception.PacienteException;
import tfi.application.dto.PacienteResponse;
import tfi.application.dto.RegistroPacienteRequest;
import tfi.domain.entity.Paciente;
import tfi.domain.valueObject.Domicilio;
import tfi.domain.repository.PacientesRepository;
import tfi.util.MensajesError;
import tfi.application.mapper.PacienteMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para PacienteService.
 * Verifica registro de pacientes y manejo de errores.
 */
@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacientesRepository pacientesRepository;

    private PacienteService pacienteService;
    private PacienteMapper pacienteMapper;

    @BeforeEach
    void setUp() {
        pacienteMapper = new PacienteMapper();
        pacienteService = new PacienteService(pacientesRepository, pacienteMapper);
    }

    @Test
    void registrarDebeCrearPacienteConDatosValidosSinObraSocial() {
        RegistroPacienteRequest request = crearRequestBasico();
        request.setObraSocial(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        when(pacientesRepository.add(any(Paciente.class))).thenAnswer(invocation -> {
            Paciente p = invocation.getArgument(0);
            p.setId("test-uuid-1234"); // Simular ID generado por BD
            return p;
        });
        
        PacienteResponse response = pacienteService.registrar(request);
        
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("test-uuid-1234", response.getId());
        assertEquals("20-20304050-5", response.getCuil());
        assertEquals("Juan", response.getNombre());
        assertEquals("Pérez", response.getApellido());
        assertNotNull(response.getDomicilio());
        assertEquals("Av. Corrientes", response.getDomicilio().getCalle());
        assertEquals(1234, response.getDomicilio().getNumero());
        assertEquals("Buenos Aires", response.getDomicilio().getLocalidad());
        assertNull(response.getObraSocial());
        
        verify(pacientesRepository).existsByCuil("20-20304050-5");
        verify(pacientesRepository).add(any(Paciente.class));
    }

    @Test
    void registrarDebeCrearPacienteConObraSocialValida() {
        RegistroPacienteRequest request = crearRequestBasico();
        request.getObraSocial().getObraSocial().setNombre("OSDE");
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        when(pacientesRepository.add(any(Paciente.class))).thenAnswer(invocation -> {
            Paciente p = invocation.getArgument(0);
            p.setId("test-uuid-5678"); // Simular ID generado por BD
            return p;
        });
        
        PacienteResponse response = pacienteService.registrar(request);
        
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("test-uuid-5678", response.getId());
        assertNotNull(response.getObraSocial());
        assertEquals("12345678", response.getObraSocial().getNumeroAfiliado());
        assertEquals(1, response.getObraSocial().getObraSocial().getId());
        assertEquals("OSDE", response.getObraSocial().getObraSocial().getNombre());
        
        verify(pacientesRepository).add(any(Paciente.class));
    }

    @Test
    void registrarDebeLanzarExcepcionSiCuilYaExiste() {
        RegistroPacienteRequest request = crearRequestBasico();
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(true);
        
 
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertEquals(MensajesError.CUIL_YA_REGISTRADO, exception.getMessage());
        verify(pacientesRepository, never()).add(any());
    }


    @Test
    void registrarDebeLanzarExcepcionSiDomicilioCalleEsNula() {
        RegistroPacienteRequest request = crearRequestBasico();
        request.getDomicilio().setCalle(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
 
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("calle"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiDomicilioNumeroEsCero() {
        RegistroPacienteRequest request = crearRequestBasico();
        request.getDomicilio().setNumero(0);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
 
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("número") || exception.getMessage().contains("mayor"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiDomicilioLocalidadEsNula() {
        RegistroPacienteRequest request = crearRequestBasico();
        request.getDomicilio().setLocalidad(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
 
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("localidad"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiNumeroAfiliadoEsNulo() {
        RegistroPacienteRequest request = crearRequestBasico();
        request.getObraSocial().setNumeroAfiliado(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
 
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("número de afiliado"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiNumeroAfiliadoEsVacio() {
        RegistroPacienteRequest request = crearRequestBasico();
        request.getObraSocial().setNumeroAfiliado("   ");
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
 
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("número de afiliado"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void findByCuilDebeRetornarPacienteSiExiste() {
        String cuil = "20-20304050-5";
        Paciente paciente = crearPacienteBasico();
        
        when(pacientesRepository.findByCuil(cuil)).thenReturn(java.util.Optional.of(paciente));
        
        java.util.Optional<Paciente> resultado = pacienteService.findByCuil(cuil);
        
        assertTrue(resultado.isPresent());
        assertEquals(cuil, resultado.get().getCuil());
        verify(pacientesRepository).findByCuil(cuil);
    }

    @Test
    void findByCuilDebeRetornarOptionalVacioSiNoExiste() {
        String cuil = "20-20304050-5";
        
        when(pacientesRepository.findByCuil(cuil)).thenReturn(java.util.Optional.empty());
        
        java.util.Optional<Paciente> resultado = pacienteService.findByCuil(cuil);
        
        assertFalse(resultado.isPresent());
        verify(pacientesRepository).findByCuil(cuil);
    }

    @Test
    void existsByCuilDebeRetornarTrueSiExiste() {
        String cuil = "20-20304050-5";
        
        when(pacientesRepository.existsByCuil(cuil)).thenReturn(true);
        
        boolean resultado = pacienteService.existsByCuil(cuil);
        
        assertTrue(resultado);
        verify(pacientesRepository).existsByCuil(cuil);
    }

    @Test
    void existsByCuilDebeRetornarFalseSiNoExiste() {
        String cuil = "20-20304050-5";
        
        when(pacientesRepository.existsByCuil(cuil)).thenReturn(false);
        
        boolean resultado = pacienteService.existsByCuil(cuil);
        
        assertFalse(resultado);
        verify(pacientesRepository).existsByCuil(cuil);
    }

    private RegistroPacienteRequest crearRequestBasico() {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        request.setCuil("20-20304050-5");
        request.setNombre("Juan");
        request.setApellido("Pérez");
        
        RegistroPacienteRequest.DomicilioRequest domicilio = new RegistroPacienteRequest.DomicilioRequest();
        domicilio.setCalle("Av. Corrientes");
        domicilio.setNumero(1234);
        domicilio.setLocalidad("Buenos Aires");
        request.setDomicilio(domicilio);
        
        RegistroPacienteRequest.ObraSocialRequest obraSocialRequest = new RegistroPacienteRequest.ObraSocialRequest();
        obraSocialRequest.setId(1);
        obraSocialRequest.setNombre("OSDE");
        
        RegistroPacienteRequest.AfiliadoRequest afiliado = new RegistroPacienteRequest.AfiliadoRequest();
        afiliado.setObraSocial(obraSocialRequest);
        afiliado.setNumeroAfiliado("12345678");
        request.setObraSocial(afiliado);
        
        return request;
    }

    private Paciente crearPacienteBasico() {
        Domicilio domicilio = new Domicilio("Av. Corrientes", 1234, "Buenos Aires");
        return new Paciente("20-20304050-5", "Juan", "Pérez", null, domicilio, null);
    }
}

