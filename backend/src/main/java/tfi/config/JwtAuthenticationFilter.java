package tfi.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tfi.domain.enums.Autoridad;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
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

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                if (jwtUtil.validateToken(token)) {
                    String id = jwtUtil.getIdFromToken(token);
                    String email = jwtUtil.getEmailFromToken(token);
                    Autoridad autoridad = jwtUtil.getAutoridadFromToken(token);

                    request.setAttribute("userId", id);
                    request.setAttribute("userEmail", email);
                    request.setAttribute("userAutoridad", autoridad);

                    logger.debug("Usuario autenticado: {} (ID: {}) con autoridad: {}", email, id, autoridad);
                } else {
                    logger.warn("Token JWT inválido recibido desde IP: {}", request.getRemoteAddr());
                }
            } catch (Exception e) {
                logger.warn("Error al procesar token JWT desde IP {}: {}", 
                    request.getRemoteAddr(), e.getMessage());
            }
        }

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
        return path.startsWith("/api/auth/login") || 
               path.startsWith("/api/auth/registro");
    }
}

