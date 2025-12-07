package tfi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración para RestTemplate utilizado para llamadas HTTP a APIs externas.
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * Crea un bean de RestTemplate con configuración de timeouts.
     * 
     * @return RestTemplate configurado
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Timeout de conexión: 5 segundos
        factory.setConnectTimeout(5000);
        // Timeout de lectura: 10 segundos
        factory.setReadTimeout(10000);
        
        return new RestTemplate(factory);
    }
}
