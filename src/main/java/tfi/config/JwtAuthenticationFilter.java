package tfi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tfi.model.enums.Autoridad;
import tfi.util.JwtUtil;

import java.io.IOException;

/**
 * Filtro que intercepta cada petición HTTP para validar tokens JWT.
 * Si el token es válido, extrae la información del usuario y la agrega al request.
 * 
 * Hereda de OncePerRequestFilter para garantizar que se ejecute una sola vez por petición.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtUtil jwtUtil;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param jwtUtil Utilidad para manejo de JWT
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * Método principal del filtro que procesa cada petición.
     * Extrae el token JWT del header Authorization, lo valida y agrega
     * la información del usuario al request.
     * 
     * @param request La petición HTTP
     * @param response La respuesta HTTP
     * @param filterChain La cadena de filtros
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        
        // Obtener el token del header Authorization
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extraer token (remover prefijo "Bearer ")
            String token = authHeader.substring(7);
            
            try {
                // Validar token
                if (jwtUtil.validateToken(token)) {
                    // Extraer información del usuario del token
                    String email = jwtUtil.getEmailFromToken(token);
                    Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);
                    
                    // Guardar en el request para que los controllers puedan acceder
                    request.setAttribute("userEmail", email);
                    request.setAttribute("userAutoridad", autoridad);
                }
            } catch (Exception e) {
                // Token inválido, expirado o manipulado
                // Continuar sin autenticar - el controller debe manejar la ausencia de atributos
                // Log opcional: logger.debug("Token JWT inválido: {}", e.getMessage());
            }
        }
        
        // Continuar con la cadena de filtros
        filterChain.doFilter(request, response);
    }

    /**
     * Define qué rutas NO deben ser filtradas.
     * Los endpoints de autenticación (login, registro) son públicos.
     * 
     * @param request La petición HTTP
     * @return true si NO se debe aplicar el filtro
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // No aplicar filtro a endpoints públicos de autenticación
        return path.startsWith("/api/auth/login") || 
               path.startsWith("/api/auth/registro");
    }
}

