package tfi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tfi.exception.PacienteException;
import tfi.model.dto.PacienteResponse;
import tfi.model.dto.RegistroPacienteRequest;
import tfi.model.entity.Paciente;
import tfi.model.enums.Autoridad;
import tfi.model.valueObjects.Domicilio;
import tfi.service.PacienteService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests unitarios para PacienteController.
 * Verifica endpoints de registro y búsqueda de pacientes.
 */
@ExtendWith(MockitoExtension.class)
class PacienteControllerTest {

    @Mock
    private PacienteService pacienteService;

    @Mock
    private HttpServletRequest httpRequest;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        PacienteController controller = new PacienteController(pacienteService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new tfi.exception.GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void registrarDebeRetornar201ConPacienteCreado() throws Exception {
        RegistroPacienteRequest request = crearRequestBasico();
            PacienteResponse response = crearResponseBasico();
        
        when(pacienteService.registrar(any(RegistroPacienteRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userEmail", "enfermera@hospital.com")
                .requestAttr("userAutoridad", Autoridad.ENFERMERA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cuil").value("20-20304050-5"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.domicilio.calle").value("Av. Corrientes"))
                .andExpect(jsonPath("$.domicilio.numero").value(1234))
                .andExpect(jsonPath("$.domicilio.localidad").value("Buenos Aires"));
        
        verify(pacienteService).registrar(any(RegistroPacienteRequest.class));
    }

    @Test
    void registrarDebeRetornar400SiCuilYaExiste() throws Exception {
        RegistroPacienteRequest request = crearRequestBasico();
        
        when(pacienteService.registrar(any(RegistroPacienteRequest.class)))
                .thenThrow(new PacienteException("Ya existe un paciente con el CUIL especificado"));
        
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userEmail", "enfermera@hospital.com")
                .requestAttr("userAutoridad", Autoridad.ENFERMERA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Ya existe un paciente con el CUIL especificado"));
    }


    @Test
    void registrarDebeRetornar400SiDatosInvalidos() throws Exception {
        RegistroPacienteRequest request = new RegistroPacienteRequest();
        
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userEmail", "enfermera@hospital.com")
                .requestAttr("userAutoridad", Autoridad.ENFERMERA))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registrarDebeRetornar403SiUsuarioNoEsEnfermera() throws Exception {
        RegistroPacienteRequest request = crearRequestBasico();
        
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .requestAttr("userEmail", "medico@hospital.com")
                .requestAttr("userAutoridad", Autoridad.MEDICO))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensaje").exists());
        
        verify(pacienteService, never()).registrar(any(RegistroPacienteRequest.class));
    }

    @Test
    void registrarDebeRetornar401SiUsuarioNoEstaAutenticado() throws Exception {
        RegistroPacienteRequest request = crearRequestBasico();
        
        mockMvc.perform(post("/api/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").exists());
        
        verify(pacienteService, never()).registrar(any(RegistroPacienteRequest.class));
    }

    @Test
    void buscarPorCuilDebeRetornar200ConPaciente() throws Exception {
        String cuil = "20-20304050-5";
        Paciente paciente = crearPacienteBasico();
        
        when(pacienteService.findByCuil(cuil)).thenReturn(paciente);
        
        mockMvc.perform(get("/api/pacientes/{cuil}", cuil)
                .requestAttr("userEmail", "enfermera@hospital.com")
                .requestAttr("userAutoridad", Autoridad.ENFERMERA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cuil").value(cuil))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"));
        
        verify(pacienteService).findByCuil(cuil);
    }

    @Test
    void buscarPorCuilDebeRetornar404SiNoExiste() throws Exception {
        String cuil = "20-20304050-5";
        
        when(pacienteService.findByCuil(cuil)).thenReturn(null);
        
        mockMvc.perform(get("/api/pacientes/{cuil}", cuil)
                .requestAttr("userEmail", "enfermera@hospital.com")
                .requestAttr("userAutoridad", Autoridad.ENFERMERA))
                .andExpect(status().isNotFound());
        
        verify(pacienteService).findByCuil(cuil);
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
        obraSocialRequest.setIdObraSocial(1);
        obraSocialRequest.setNombreObraSocial("OSDE");
        
        RegistroPacienteRequest.AfiliadoRequest afiliado = new RegistroPacienteRequest.AfiliadoRequest();
        afiliado.setObraSocial(obraSocialRequest);
        afiliado.setNumeroAfiliado("12345678");
        request.setObraSocial(afiliado);
        
        return request;
    }

    private PacienteResponse crearResponseBasico() {
        PacienteResponse response = new PacienteResponse();
        response.setCuil("20-20304050-5");
        response.setNombre("Juan");
        response.setApellido("Pérez");
        
        PacienteResponse.DomicilioResponse domicilio = new PacienteResponse.DomicilioResponse();
        domicilio.setCalle("Av. Corrientes");
        domicilio.setNumero(1234);
        domicilio.setLocalidad("Buenos Aires");
        response.setDomicilio(domicilio);
        
        return response;
    }

    private Paciente crearPacienteBasico() {
        Domicilio domicilio = new Domicilio("Av. Corrientes", 1234, "Buenos Aires");
        return new Paciente("20-20304050-5", "Juan", "Pérez", null, domicilio, null);
    }
}

