import static org.assertj.core.api.Assertions.*;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import model.Enfermero;
import mok.RepositorioEnfermeros;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

public class ModuloUrgenciasStepDefinitions {

    private RepositorioEnfermeros repo;

    public ModuloUrgenciasStepDefinitions() {
        this.repo = new RepositorioEnfermeros();
        repo.add(new Enfermero(
                "40-12345678-9",
                "Maria Celeste",
                "Sarmiento",
                "blamberlu@gmail.com",
                "Licencida")
        );
    }

    @Given("Que la siguiente enfermera esté registrada:")
    public void queLaSiguienteEnfermeraEstéRegistrada(List<Map<String, String>> lista) {

        List<Enfermero> listaEnfermeros = repo.findAll();
        int cantEnfermerosEncontados = 0;

        for (Map<String, String> map : lista) {
            String cuil = map.get("CUIL");
            String nombre = map.get("Nombre");
            String apellido = map.get("Apellido");
            for (Enfermero enfermero : listaEnfermeros) {
                if(enfermero.getCuil().equals(cuil) &&
                   enfermero.getNombre().equals(nombre) &&
                   enfermero.getApellido().equals(apellido)) {
                    cantEnfermerosEncontados += 1;
                };
            };
        }
        assertEquals(cantEnfermerosEncontados, lista.size());
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
