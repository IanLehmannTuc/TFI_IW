package tfi.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tfi.application.service.ObraSocialCacheService;

/**
 * Inicializador que precarga el cache de obras sociales al arrancar la aplicación.
 */
@Component
public class CacheInitializer implements CommandLineRunner {
    
    private final ObraSocialCacheService obraSocialCacheService;
    
    public CacheInitializer(ObraSocialCacheService obraSocialCacheService) {
        this.obraSocialCacheService = obraSocialCacheService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        // Precargar el cache de obras sociales al iniciar la aplicación
        obraSocialCacheService.precargarCache();
    }
}
