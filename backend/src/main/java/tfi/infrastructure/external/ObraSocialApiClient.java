package tfi.infrastructure.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tfi.application.dto.ObraSocialResponse;
import tfi.application.dto.VerificacionAfiliacionResponse;
import tfi.domain.port.ObraSocialPort;
import tfi.exception.ObraSocialException;

import java.net.URI;
import java.util.List;

/**
 * Cliente HTTP para comunicarse con la API externa de obras sociales.
 * Implementa el puerto ObraSocialPort.
 */
@Component
public class ObraSocialApiClient implements ObraSocialPort {

    private final RestTemplate restTemplate;
    private final String apiBaseUrl;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param restTemplate RestTemplate para realizar llamadas HTTP
     * @param apiBaseUrl URL base de la API de obras sociales (configurada en application.properties)
     */
    public ObraSocialApiClient(RestTemplate restTemplate,
                               @Value("${obras-sociales.api.url:http://localhost:8080}") String apiBaseUrl) {
        this.restTemplate = restTemplate;
        this.apiBaseUrl = apiBaseUrl;
    }

    /**
     * Lista todas las obras sociales disponibles.
     * 
     * @return Lista de obras sociales disponibles
     * @throws ObraSocialException Si la API no está disponible o hay un error en la comunicación
     */
    @Override
    public List<ObraSocialResponse> listarObrasSociales() {
        try {
            URI uri = UriComponentsBuilder
                    .fromHttpUrl(apiBaseUrl)
                    .path("/api/obras-sociales")
                    .build()
                    .toUri();

            ResponseEntity<List<ObraSocialResponse>> response = restTemplate.exchange(
                    uri,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<ObraSocialResponse>>() {}
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ObraSocialException("La API de obras sociales retornó una respuesta inesperada");
            }

        } catch (HttpClientErrorException e) {

            throw new ObraSocialException(
                    String.format("Error al listar obras sociales: %s", e.getMessage()),
                    e
            );
        } catch (HttpServerErrorException e) {

            throw new ObraSocialException(
                    "El servicio de obras sociales no está disponible temporalmente. Por favor, intente más tarde.",
                    e
            );
        } catch (ResourceAccessException e) {

            throw new ObraSocialException(
                    "No se pudo conectar con el servicio de obras sociales. Verifique que el servicio esté disponible.",
                    e
            );
        } catch (Exception e) {

            throw new ObraSocialException(
                    "Error inesperado al listar las obras sociales",
                    e
            );
        }
    }

    /**
     * Verifica si un paciente está afiliado a una obra social específica.
     * 
     * @param obraSocialId ID de la obra social a verificar
     * @param numeroAfiliado Número de afiliado del paciente
     * @return VerificacionAfiliacionResponse con el resultado de la verificación
     * @throws ObraSocialException Si la obra social no existe, la API no está disponible,
     *         o hay un error en la comunicación
     */
    @Override
    public VerificacionAfiliacionResponse verificarAfiliacion(int obraSocialId, String numeroAfiliado) {
        try {

            URI uri = UriComponentsBuilder
                    .fromHttpUrl(apiBaseUrl)
                    .path("/api/obras-sociales/verificar")
                    .queryParam("obra_social_id", obraSocialId)
                    .queryParam("numero_afiliado", numeroAfiliado)
                    .build()
                    .toUri();


            ResponseEntity<VerificacionAfiliacionResponse> response = restTemplate.getForEntity(
                    uri, 
                    VerificacionAfiliacionResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new ObraSocialException("La API de obras sociales retornó una respuesta inesperada");
            }

        } catch (HttpClientErrorException.NotFound e) {

            throw new ObraSocialException(
                    String.format("La obra social con ID %d no existe", obraSocialId),
                    e
            );
        } catch (HttpClientErrorException e) {

            throw new ObraSocialException(
                    String.format("Error al verificar afiliación: %s", e.getMessage()),
                    e
            );
        } catch (HttpServerErrorException e) {

            throw new ObraSocialException(
                    "El servicio de verificación de obras sociales no está disponible temporalmente. Por favor, intente más tarde.",
                    e
            );
        } catch (ResourceAccessException e) {

            throw new ObraSocialException(
                    "No se pudo conectar con el servicio de verificación de obras sociales. Verifique que el servicio esté disponible.",
                    e
            );
        } catch (Exception e) {

            throw new ObraSocialException(
                    "Error inesperado al verificar la afiliación a la obra social",
                    e
            );
        }
    }
}
