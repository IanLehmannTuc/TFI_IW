package tfi.application.service;

import org.springframework.stereotype.Service;
import tfi.application.dto.ObraSocialResponse;
import tfi.domain.port.ObraSocialPort;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de cache para nombres de obras sociales.
 * Reduce las llamadas a la API externa cacheando los nombres de obras sociales en memoria.
 * 
 * El cache tiene un TTL (Time To Live) de 1 hora por defecto.
 */
@Service
public class ObraSocialCacheService {

    private final ObraSocialPort obraSocialPort;
    private final Map<Integer, CacheEntry> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    private static final long DEFAULT_TTL_MINUTES = 60;

    /**
     * Entrada del cache con timestamp de expiración
     */
    private static class CacheEntry {
        private final String nombre;
        private final long expirationTime;

        public CacheEntry(String nombre, long ttlMinutes) {
            this.nombre = nombre;
            this.expirationTime = System.currentTimeMillis() + (ttlMinutes * 60 * 1000);
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }

        public String getNombre() {
            return nombre;
        }
    }

    public ObraSocialCacheService(ObraSocialPort obraSocialPort) {
        this.obraSocialPort = obraSocialPort;


        scheduler.scheduleAtFixedRate(this::cleanExpiredEntries, 10, 10, TimeUnit.MINUTES);
    }

    /**
     * Obtiene el nombre de una obra social por su ID.
     * Primero busca en el cache, si no está o está expirado, consulta la API externa.
     * 
     * @param obraSocialId ID de la obra social
     * @return Nombre de la obra social, o "Obra Social {id}" si no se puede obtener
     */
    public String getNombreObraSocial(Integer obraSocialId) {
        if (obraSocialId == null) {
            return null;
        }


        CacheEntry entry = cache.get(obraSocialId);
        if (entry != null && !entry.isExpired()) {
            return entry.getNombre();
        }


        try {


            var obrasSociales = obraSocialPort.listarObrasSociales();


            for (ObraSocialResponse os : obrasSociales) {
                cache.put(os.getId(), new CacheEntry(os.getNombre(), DEFAULT_TTL_MINUTES));
            }


            for (ObraSocialResponse os : obrasSociales) {
                if (os.getId().equals(obraSocialId)) {
                    return os.getNombre();
                }
            }


            String nombreDefault = "Obra Social " + obraSocialId;
            cache.put(obraSocialId, new CacheEntry(nombreDefault, DEFAULT_TTL_MINUTES));
            return nombreDefault;

        } catch (Exception e) {

            return "Obra Social " + obraSocialId;
        }
    }

    /**
     * Precarga todas las obras sociales en el cache.
     * Útil para inicializar el cache al arrancar la aplicación.
     */
    public void precargarCache() {
        try {
            var obrasSociales = obraSocialPort.listarObrasSociales();
            for (ObraSocialResponse os : obrasSociales) {
                cache.put(os.getId(), new CacheEntry(os.getNombre(), DEFAULT_TTL_MINUTES));
            }
        } catch (Exception e) {

        }
    }

    /**
     * Limpia las entradas expiradas del cache.
     */
    private void cleanExpiredEntries() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    /**
     * Limpia todo el cache manualmente.
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * Obtiene el tamaño actual del cache.
     */
    public int getCacheSize() {
        return cache.size();
    }
}
