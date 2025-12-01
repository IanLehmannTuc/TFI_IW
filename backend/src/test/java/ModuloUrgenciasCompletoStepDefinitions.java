import static org.assertj.core.api.Assertions.*;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.datatable.DataTable;

import tfi.domain.entity.Usuario;
import tfi.domain.entity.Paciente;
import tfi.domain.entity.Ingreso;
import tfi.domain.enums.Autoridad;
import tfi.domain.enums.NivelEmergencia;
import tfi.domain.valueObject.Cuil;
import tfi.domain.valueObject.Email;
import tfi.domain.valueObject.Temperatura;
import tfi.domain.valueObject.TensionArterial;
import tfi.domain.valueObject.FrecuenciaCardiaca;
import tfi.domain.valueObject.FrecuenciaRespiratoria;
import tfi.domain.valueObject.Presion;

import tfi.infrastructure.persistence.repository.memory.UsuarioRepositoryImpl;
import tfi.infrastructure.persistence.repository.memory.PacientesRepositoryImpl;
import tfi.infrastructure.persistence.repository.memory.IngresoRepositoryImpl;
import tfi.domain.repository.UsuarioRepository;
import tfi.domain.repository.PacientesRepository;
import tfi.domain.repository.IngresoRepository;

import tfi.application.service.PacienteService;
import tfi.application.service.ColaAtencionService;
import tfi.application.service.UrgenciaService;
import tfi.application.mapper.PacienteMapper;
import tfi.application.mapper.IngresoMapper;
import tfi.application.dto.RegistroIngresoRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ModuloUrgenciasCompletoStepDefinitions {

    private UsuarioRepository repoUsuarios;
    private PacientesRepository repoPacientes;
    private IngresoRepository repoIngresos;
    private UrgenciaService urgenciaService;
    private PacienteService pacienteService;
    
    private Usuario enfermero;
    private Ingreso ingreso;
    private String ultimoError;

    @Before
    public void setup() {
        this.repoUsuarios = new UsuarioRepositoryImpl();
        this.repoPacientes = new PacientesRepositoryImpl();
        this.repoIngresos = new IngresoRepositoryImpl();

        this.urgenciaService = new UrgenciaService(repoPacientes, repoUsuarios, repoIngresos, new IngresoMapper());
        this.pacienteService = new PacienteService(repoPacientes, new PacienteMapper());
    }

    @After
    public void teardown() {
        ColaAtencionService.resetInstance();
    }

    @Given("Que el siguiente enfermero esté registrado:")
    public void queElSiguienteEnfermeroEstéRegistrado(DataTable dataTable) {
        Map<String, String> enfermeroData = dataTable.asMaps(String.class, String.class).get(0);
        String cuil = enfermeroData.get("CUIL");
        String nombre = enfermeroData.get("Nombre");
        String apellido = enfermeroData.get("Apellido");
        String email = enfermeroData.get("Email");
        String matricula = enfermeroData.get("Matricula");
        
        Usuario enfermero = new Usuario(
            Email.from(email),
            "hash_dummy", 
            Autoridad.ENFERMERO,
            new Cuil(cuil),
            nombre,
            apellido,
            matricula
        );
        
        repoUsuarios.add(enfermero);
        this.enfermero = enfermero;
    }

    @Given("Que estan cargados los siguientes pacientes en el sistema:")
    public void queEstanCargadosLosSiguientesPacientesEnElSistema(DataTable dataTable) {
        List<Map<String, String>> pacientesData = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> pacienteData : pacientesData) {
            String cuil = pacienteData.get("CUIL");
            String nombre = pacienteData.get("Nombre");
            String apellido = pacienteData.get("Apellido");
            
            
            Paciente paciente = new Paciente(cuil, nombre, apellido);
            repoPacientes.add(paciente);
        }
    }

    @Given("Que no existe un paciente registrado con CUIL {string}")
    public void queNoExisteUnPacienteRegistradoConCUIL(String cuil) {
        repoPacientes.findByCuil(cuil).ifPresent(paciente -> 
            repoPacientes.delete(paciente)
        );
    }

    @Given("Hay un paciente con CUIL:{string}, Nombre:{string}, Apellido:{string} en la cola de atención con nivel de emergencia {string}")
    public void hayUnPacienteConCUILNombreApellidoEnLaColaDeAtencionConNivelDeEmergencia(String cuil, String nombre, String apellido, String nivelEmergencia) {

        if (!repoPacientes.findByCuil(cuil).isPresent()) {
            Paciente nuevoPaciente = new Paciente(cuil, nombre, apellido);
            repoPacientes.add(nuevoPaciente);
        }
        String nivelStr = nivelEmergencia.toUpperCase().replace(" ", "_");
        Ingreso ingreso = new Ingreso(
            repoPacientes.findByCuil(cuil).get(),
            enfermero,
            "Informe",
            new Temperatura(0.0),
            new TensionArterial(new Presion(0), new Presion(0)),
            new FrecuenciaCardiaca(0),
            new FrecuenciaRespiratoria(0),
            NivelEmergencia.valueOf(nivelStr)
        );
        ColaAtencionService colaAtencionService = ColaAtencionService.getInstance();
        colaAtencionService.agregarACola(ingreso);
    }

    @When("Ingresa a urgencias el siguiente paciente:")
    public void ingresaAurgenciasElSiguientePaciente(DataTable dataTable) {
        try {
            Map<String, String> pacienteData = dataTable.asMaps(String.class, String.class).get(0);
            String cuil = pacienteData.get("CUIL");
            String nivelStr = pacienteData.get("Nivel de Emergencia").toUpperCase().replace(" ", "_");

            Paciente paciente = pacienteService.findByCuil(cuil).orElse(null);
            if (paciente == null) {
                String nombre = pacienteData.getOrDefault("Nombre", "Sin Nombre");
                String apellido = pacienteData.getOrDefault("Apellido", "Sin Apellido");
                paciente = new Paciente(cuil, nombre, apellido);
            } 
            
            String descripcion = pacienteData.get("Informe");
            NivelEmergencia nivelEmergencia = NivelEmergencia.valueOf(nivelStr);
            Temperatura temperatura = new Temperatura(
                Double.parseDouble(pacienteData.get("Temperatura"))
            );
            String[] tensionPartes = pacienteData.get("Tension Arterial").split("/");
            TensionArterial tensionArterial = new TensionArterial(
                new Presion(Integer.parseInt(tensionPartes[0].trim())),
                new Presion(Integer.parseInt(tensionPartes[1].trim()))
            );
            FrecuenciaCardiaca frecuenciaCardiaca = new FrecuenciaCardiaca(
                Integer.parseInt(pacienteData.get("Frecuencia Cardiaca"))
            );
            FrecuenciaRespiratoria frecuenciaRespiratoria = new FrecuenciaRespiratoria(
                Integer.parseInt(pacienteData.get("Frecuencia Respiratoria"))
            );
            
            // Crear RegistroIngresoRequest para registrar el ingreso
            RegistroIngresoRequest request = new RegistroIngresoRequest();
            request.setPacienteCuil(paciente.getCuil());
            request.setEnfermeroCuil(this.enfermero.getCuil());
            request.setDescripcion(descripcion);
            request.setTemperatura(temperatura.getValor());
            request.setTensionSistolica(tensionArterial.getPresionSistolica().getValor());
            request.setTensionDiastolica(tensionArterial.getPresionDiastolica().getValor());
            request.setFrecuenciaCardiaca(frecuenciaCardiaca.getValor());
            request.setFrecuenciaRespiratoria(frecuenciaRespiratoria.getValor());
            request.setNivelEmergencia(nivelEmergencia);
            
            var ingresoResponse = this.urgenciaService.registrarIngreso(request);
            // Buscar el ingreso registrado en el repositorio
            this.ingreso = this.repoIngresos.findById(ingresoResponse.getId()).orElse(null);
        } catch (RuntimeException e) {
            ultimoError = e.getMessage();
        } catch (Exception e) {
            ultimoError = e.getMessage();
        }
    }

    @When("Ingresa a urgencias el siguiente paciente omitiendo datos obligatorios:")
    public void ingresaAurgenciasElSiguientePacienteOmitiendoDatosObligatorios(DataTable dataTable) {
        try {
            Map<String, String> pacienteData = dataTable.asMaps(String.class, String.class).get(0);
            String cuil = pacienteData.get("CUIL");
            
            Paciente paciente = pacienteService.findByCuil(cuil).orElse(null);
            
            String informe = pacienteData.get("Informe");
            if (informe == null || informe.trim().isEmpty()) {
                throw new IllegalArgumentException("El informe es obligatorio");
            }
            
            String nivelEmergenciaStr = pacienteData.get("Nivel de Emergencia");
            String temperatura = pacienteData.get("Temperatura");
            String frecuenciaCardiaca = pacienteData.get("Frecuencia Cardiaca");
            String frecuenciaRespiratoria = pacienteData.get("Frecuencia Respiratoria");
            String tensionArterial = pacienteData.get("Tension Arterial");

            String[] tensionPartes = tensionArterial.split("/");
            TensionArterial tension = new TensionArterial(
                new Presion(Integer.parseInt(tensionPartes[0].trim())),
                new Presion(Integer.parseInt(tensionPartes[1].trim()))
            );

            this.ingreso = new Ingreso(
                null,
                paciente,
                this.enfermero,
                informe,
                LocalDateTime.now(),
                new Temperatura(Double.parseDouble(temperatura)),
                tension,
                new FrecuenciaCardiaca(Integer.parseInt(frecuenciaCardiaca)),
                new FrecuenciaRespiratoria(Integer.parseInt(frecuenciaRespiratoria)),
                NivelEmergencia.valueOf(nivelEmergenciaStr.toUpperCase().replace(" ", "_"))
            );

        } catch (IllegalArgumentException e) {
            ultimoError = e.getMessage();
        } catch (Exception e) {
            ultimoError = e.getMessage();
        }
    }

    @Then("El ingreso se registra correctamente y el paciente entra en la cola de atención")
    public void elIngresoSeRegistraCorrectamente() {
        assertThat(this.ingreso)
            .as("El ingreso debe haberse creado")
            .isNotNull();
        
        assertThat(this.ingreso.getId())
            .as("El ingreso debe tener un ID asignado (fue guardado)")
            .isNotNull();
        
        List<Ingreso> cola = this.urgenciaService.obtenerColaDeAtencion();
        assertThat(cola)
            .as("La cola de atención debe contener el ingreso")
            .contains(this.ingreso);
    }

    @Then("Se emite un mensaje de error indicando que el campo faltante es obligatorio")
    public void seEmiteUnMensajeDeErrorIndicandoQueElCampoFaltanteEsObligatorio() {
        assertThat(ultimoError)
            .as("Debe haberse generado un error")
            .isNotNull();
        
        assertThat(ultimoError)
            .as("El mensaje de error debe mencionar que el campo es obligatorio")
            .contains("obligatorio");
    }
    
    @Then("Se emite un mensaje de error indicando que el informe es obligatorio")
    public void seEmiteUnMensajeDeErrorIndicandoQueElInformeEsObligatorio() {
        assertThat(ultimoError)
            .as("Debe haberse generado un error")
            .isNotNull();
        
        assertThat(ultimoError)
            .as("El mensaje de error debe mencionar que el informe es obligatorio")
            .containsAnyOf("informe", "obligatorio");
    }
    
    @Then("Se emite un mensaje de error indicando que la frecuencia cardíaca no puede ser negativa")
    public void seEmiteUnMensajeDeErrorIndicandoQueLaFrecuenciaCardiacaNoPuedeSerNegativa() {
        assertThat(ultimoError)
            .as("Debe haberse generado un error")
            .isNotNull();
        
        assertThat(ultimoError)
            .as("El mensaje de error debe mencionar que la frecuencia cardíaca no puede ser negativa")
            .containsAnyOf("frecuencia cardíaca", "negativa", "negativo");
    }
    
    @Then("Se emite un mensaje de error indicando que la frecuencia respiratoria no puede ser negativa")
    public void seEmiteUnMensajeDeErrorIndicandoQueLaFrecuenciaRespiratoriaNoPuedeSerNegativa() {
        assertThat(ultimoError)
            .as("Debe haberse generado un error")
            .isNotNull();
        
        assertThat(ultimoError)
            .as("El mensaje de error debe mencionar que la frecuencia respiratoria no puede ser negativa")
            .containsAnyOf("frecuencia respiratoria", "negativa", "negativo");
    }
    
    @Then("Se emite un mensaje de error indicando que la tensión arterial sistólica no puede ser negativa")
    public void seEmiteUnMensajeDeErrorIndicandoQueLaTensionArterialSistolicaNoPuedeSerNegativa() {
        assertThat(ultimoError)
            .as("Debe haberse generado un error")
            .isNotNull();
        
        assertThat(ultimoError)
            .as("El mensaje de error debe mencionar que la presión/tensión no puede ser negativa")
            .containsAnyOf("presión", "tensión", "negativa", "negativo");
    }
    
    @Then("Se emite un mensaje de error indicando que la tensión arterial diastólica no puede ser negativa")
    public void seEmiteUnMensajeDeErrorIndicandoQueLaTensionArterialDiastolicaNoPuedeSerNegativa() {
        assertThat(ultimoError)
            .as("Debe haberse generado un error")
            .isNotNull();
        
        assertThat(ultimoError)
            .as("El mensaje de error debe mencionar que la presión/tensión no puede ser negativa")
            .containsAnyOf("presión", "tensión", "negativa", "negativo");
    }
    
    @Then("La cola de atención se encuentra en el siguiente orden")
    public void laColaDeAtencionSeEncuentraEnElSiguienteOrden(DataTable dataTable) {
        List<Ingreso> colaAtencion = this.urgenciaService.obtenerColaDeAtencion();
        List<String> cuilesEsperados = dataTable.asList(String.class);
        
        List<String> cuilesEnCola = colaAtencion.stream()
            .map(ingreso -> ingreso.getPaciente().getCuil())
            .toList();  
        
        assertThat(cuilesEnCola)
            .as("La cola debe tener exactamente los CUILs esperados en el mismo orden")
            .containsExactlyElementsOf(cuilesEsperados);
    }

    @Then("Se crea el paciente con CUIL {string}")
    public void seCreaElPacienteConCUIL(String cuil) {
        boolean pacienteExists = repoPacientes.existsByCuil(cuil);
        assertThat(pacienteExists)
            .as("El paciente con CUIL %s se registró", cuil)
            .isTrue();
    }
}
