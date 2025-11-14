package tfi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tfi.exception.PacienteException;
import tfi.model.dto.PacienteResponse;
import tfi.model.dto.RegistroPacienteRequest;
import tfi.model.entity.Paciente;
import tfi.model.valueObjects.Domicilio;
import tfi.repository.interfaces.PacientesRepository;
import tfi.util.MensajesError;

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

    @BeforeEach
    void setUp() {
        pacienteService = new PacienteService(pacientesRepository);
    }

    // ========== TESTS DE REGISTRO EXITOSO ==========

    @Test
    void registrarDebeCrearPacienteConDatosValidosSinObraSocial() {
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        request.setObraSocial(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        when(pacientesRepository.add(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        PacienteResponse response = pacienteService.registrar(request);
        
        // Assert
        assertNotNull(response);
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
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        // Asegurar que el nombre de obra social esté en el request
        request.getObraSocial().getObraSocial().setNombreObraSocial("OSDE");
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        when(pacientesRepository.add(any(Paciente.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        PacienteResponse response = pacienteService.registrar(request);
        
        // Assert
        assertNotNull(response);
        assertNotNull(response.getObraSocial());
        assertEquals("12345678", response.getObraSocial().getNumeroAfiliado());
        assertEquals(1, response.getObraSocial().getObraSocial().getIdObraSocial());
        assertEquals("OSDE", response.getObraSocial().getObraSocial().getNombreObraSocial());
        
        verify(pacientesRepository).add(any(Paciente.class));
    }

    // ========== TESTS DE VALIDACIONES ==========

    @Test
    void registrarDebeLanzarExcepcionSiCuilYaExiste() {
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(true);
        
        // Act & Assert
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertEquals(MensajesError.CUIL_YA_REGISTRADO, exception.getMessage());
        verify(pacientesRepository, never()).add(any());
    }


    @Test
    void registrarDebeLanzarExcepcionSiDomicilioCalleEsNula() {
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        request.getDomicilio().setCalle(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
        // Act & Assert
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("calle"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiDomicilioNumeroEsCero() {
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        request.getDomicilio().setNumero(0);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
        // Act & Assert
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("número") || exception.getMessage().contains("mayor"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiDomicilioLocalidadEsNula() {
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        request.getDomicilio().setLocalidad(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
        // Act & Assert
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("localidad"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiNumeroAfiliadoEsNulo() {
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        request.getObraSocial().setNumeroAfiliado(null);
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
        // Act & Assert
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("número de afiliado"));
        verify(pacientesRepository, never()).add(any());
    }

    @Test
    void registrarDebeLanzarExcepcionSiNumeroAfiliadoEsVacio() {
        // Arrange
        RegistroPacienteRequest request = crearRequestBasico();
        request.getObraSocial().setNumeroAfiliado("   ");
        
        when(pacientesRepository.existsByCuil(anyString())).thenReturn(false);
        
        // Act & Assert
        PacienteException exception = assertThrows(
            PacienteException.class,
            () -> pacienteService.registrar(request)
        );
        
        assertTrue(exception.getMessage().contains("número de afiliado"));
        verify(pacientesRepository, never()).add(any());
    }

    // ========== TESTS DE BUSQUEDA ==========

    @Test
    void findByCuilDebeRetornarPacienteSiExiste() {
        // Arrange
        String cuil = "20-20304050-5";
        Paciente paciente = crearPacienteBasico();
        
        when(pacientesRepository.findByCuil(cuil)).thenReturn(paciente);
        
        // Act
        Paciente resultado = pacienteService.findByCuil(cuil);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(cuil, resultado.getCuil());
        verify(pacientesRepository).findByCuil(cuil);
    }

    @Test
    void findByCuilDebeRetornarNullSiNoExiste() {
        // Arrange
        String cuil = "20-20304050-5";
        
        when(pacientesRepository.findByCuil(cuil)).thenReturn(null);
        
        // Act
        Paciente resultado = pacienteService.findByCuil(cuil);
        
        // Assert
        assertNull(resultado);
        verify(pacientesRepository).findByCuil(cuil);
    }

    @Test
    void existsByCuilDebeRetornarTrueSiExiste() {
        // Arrange
        String cuil = "20-20304050-5";
        
        when(pacientesRepository.existsByCuil(cuil)).thenReturn(true);
        
        // Act
        boolean resultado = pacienteService.existsByCuil(cuil);
        
        // Assert
        assertTrue(resultado);
        verify(pacientesRepository).existsByCuil(cuil);
    }

    @Test
    void existsByCuilDebeRetornarFalseSiNoExiste() {
        // Arrange
        String cuil = "20-20304050-5";
        
        when(pacientesRepository.existsByCuil(cuil)).thenReturn(false);
        
        // Act
        boolean resultado = pacienteService.existsByCuil(cuil);
        
        // Assert
        assertFalse(resultado);
        verify(pacientesRepository).existsByCuil(cuil);
    }

    // ========== MÉTODOS AUXILIARES ==========

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
        obraSocialRequest.setIdObraSocial(1);
        obraSocialRequest.setNombreObraSocial("OSDE");
        
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

