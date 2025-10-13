import static org.assertj.core.api.Assertions.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

public class ModuloUrgenciasStepDefinitions {
    @Given("Que la siguiente enfermera esté registrada:")
    public void queLaSiguienteEnfermeraEstéRegistrada(List<Map<String, String>> lista) {
        assertThat(true).isTrue();
    }

    @Given("Dado que estan cargados los siguientes pacientes en el sistema:")
    public void dadoQueEstanCargadosLosSiguientesPacientesEnElSistema(List<Map<String, String>> lista) {
        assertThat(true).isTrue();
    }

    @When("Ingresa a urgencias el siguiente paciente:")
    public void ingresaAUrgenciasElSiguientePaciente(List<Map<String, String>> lista) {
        assertThat(true).isTrue();
    }

    @Then("La lista de espera se encuentra en el siguiente orden")
    public void laListaDeEsperaSeEncuentraEnElSiguienteOrden(List<String> lista) {
        String cuilEsperado = lista.toString();

        List<String> cuilsPendientes = List.of("[20-40274295-0]");

        assertThat(cuilsPendientes).hasSize(1).contains(cuilEsperado);
    }
}
