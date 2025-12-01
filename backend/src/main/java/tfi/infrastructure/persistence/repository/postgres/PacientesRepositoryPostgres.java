package tfi.infrastructure.persistence.repository.postgres;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.Afiliado;
import tfi.domain.entity.ObraSocial;
import tfi.domain.entity.Paciente;
import tfi.domain.repository.PacientesRepository;
import tfi.domain.valueObject.Domicilio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Paciente usando PostgreSQL con JDBC puro.
 * No utiliza JPA ni ningún ORM.
 * 
 * Se activa cuando el perfil "postgres" está activo.
 */
@Repository
@Profile("postgres")
public class PacientesRepositoryPostgres implements PacientesRepository {

    private final JdbcTemplate jdbcTemplate;

    public PacientesRepositoryPostgres(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper para convertir ResultSet a Paciente.
     */
    private static class PacienteRowMapper implements RowMapper<Paciente> {
        @Override
        public Paciente mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String cuil = rs.getString("cuil");
            String nombre = rs.getString("nombre");
            String apellido = rs.getString("apellido");
            String email = rs.getString("email");

            // Domicilio
            Domicilio domicilio = null;
            String calle = rs.getString("domicilio_calle");
            if (calle != null) {
                int numero = rs.getInt("domicilio_numero");
                String localidad = rs.getString("domicilio_localidad");
                domicilio = new Domicilio(calle, numero, localidad);
            }

            // Obra Social
            Afiliado afiliado = null;
            Integer obraSocialId = (Integer) rs.getObject("obra_social_id");
            if (obraSocialId != null) {
                String nombreObraSocial = rs.getString("nombre_obra_social");
                String numeroAfiliado = rs.getString("numero_afiliado");
                ObraSocial obraSocial = new ObraSocial(obraSocialId, nombreObraSocial);
                afiliado = new Afiliado(obraSocial, numeroAfiliado);
            }

            Paciente paciente;
            // Si nombre es null, crear paciente con constructor básico
            if (nombre == null) {
                paciente = new Paciente(cuil, domicilio, afiliado);
            } else {
                paciente = new Paciente(cuil, nombre, apellido, email, domicilio, afiliado);
            }
            
            paciente.setId(id);
            return paciente;
        }
    }

    @Override
    public List<Paciente> findAll() {
        String sql = "SELECT p.id, p.cuil, p.nombre, p.apellido, p.email, " +
                     "p.domicilio_calle, p.domicilio_numero, p.domicilio_localidad, " +
                     "p.obra_social_id, p.numero_afiliado, os.nombre AS nombre_obra_social " +
                     "FROM pacientes p " +
                     "LEFT JOIN obras_sociales os ON p.obra_social_id = os.id";
        return jdbcTemplate.query(sql, new PacienteRowMapper());
    }

    @Override
    public Optional<Paciente> findByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT p.id, p.cuil, p.nombre, p.apellido, p.email, " +
                     "p.domicilio_calle, p.domicilio_numero, p.domicilio_localidad, " +
                     "p.obra_social_id, p.numero_afiliado, os.nombre AS nombre_obra_social " +
                     "FROM pacientes p " +
                     "LEFT JOIN obras_sociales os ON p.obra_social_id = os.id " +
                     "WHERE p.cuil = ?";
        
        List<Paciente> results = jdbcTemplate.query(sql, new PacienteRowMapper(), cuil);
        
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public boolean existsByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM pacientes WHERE cuil = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cuil);
        return count != null && count > 0;
    }

    @Override
    public Paciente add(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (paciente.getCuil() == null || paciente.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del paciente no puede ser nulo o vacío");
        }
        if (existsByCuil(paciente.getCuil())) {
            throw new IllegalStateException("Ya existe un paciente con el CUIL: " + paciente.getCuil());
        }

        // Obtener o crear obra social si es necesario
        Integer obraSocialId = null;
        if (paciente.getObraSocial() != null) {
            obraSocialId = obtenerOCrearObraSocial(paciente.getObraSocial().getObraSocial());
        }

        // El ID se genera automáticamente en la base de datos
        String sql = "INSERT INTO pacientes (cuil, nombre, apellido, email, " +
                     "domicilio_calle, domicilio_numero, domicilio_localidad, " +
                     "obra_social_id, numero_afiliado) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        
        String generatedId = jdbcTemplate.queryForObject(sql, String.class,
            paciente.getCuil(),
            paciente.getNombre(),
            paciente.getApellido(),
            paciente.getEmail(),
            paciente.getDomicilio() != null ? paciente.getDomicilio().getCalle() : null,
            paciente.getDomicilio() != null ? paciente.getDomicilio().getNumero() : null,
            paciente.getDomicilio() != null ? paciente.getDomicilio().getLocalidad() : null,
            obraSocialId,
            paciente.getObraSocial() != null ? paciente.getObraSocial().getNumeroAfiliado() : null
        );
        
        paciente.setId(generatedId);
        return paciente;
    }

    @Override
    public Paciente update(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (paciente.getCuil() == null || paciente.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del paciente no puede ser nulo o vacío");
        }
        if (!existsByCuil(paciente.getCuil())) {
            throw new IllegalStateException("No existe un paciente con el CUIL: " + paciente.getCuil());
        }

        // Obtener o crear obra social si es necesario
        Integer obraSocialId = null;
        if (paciente.getObraSocial() != null) {
            obraSocialId = obtenerOCrearObraSocial(paciente.getObraSocial().getObraSocial());
        }

        String sql = "UPDATE pacientes SET nombre = ?, apellido = ?, email = ?, " +
                     "domicilio_calle = ?, domicilio_numero = ?, domicilio_localidad = ?, " +
                     "obra_social_id = ?, numero_afiliado = ? " +
                     "WHERE cuil = ?";
        
        jdbcTemplate.update(sql,
            paciente.getNombre(),
            paciente.getApellido(),
            paciente.getEmail(),
            paciente.getDomicilio() != null ? paciente.getDomicilio().getCalle() : null,
            paciente.getDomicilio() != null ? paciente.getDomicilio().getNumero() : null,
            paciente.getDomicilio() != null ? paciente.getDomicilio().getLocalidad() : null,
            obraSocialId,
            paciente.getObraSocial() != null ? paciente.getObraSocial().getNumeroAfiliado() : null,
            paciente.getCuil()
        );

        return paciente;
    }

    @Override
    public Paciente delete(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        if (paciente.getCuil() == null || paciente.getCuil().trim().isEmpty()) {
            throw new IllegalArgumentException("El CUIL del paciente no puede ser nulo o vacío");
        }

        // Primero verificamos que existe y lo obtenemos
        Paciente existing = findByCuil(paciente.getCuil())
            .orElseThrow(() -> new IllegalStateException("No existe un paciente con el CUIL: " + paciente.getCuil()));

        String sql = "DELETE FROM pacientes WHERE cuil = ?";
        jdbcTemplate.update(sql, paciente.getCuil());

        return existing;
    }

    /**
     * Obtiene el ID de una obra social o la crea si no existe.
     */
    private Integer obtenerOCrearObraSocial(ObraSocial obraSocial) {
        if (obraSocial == null) {
            return null;
        }

        // Primero intentamos obtenerla por nombre
        String sqlSelect = "SELECT id FROM obras_sociales WHERE nombre = ?";
        List<Integer> results = jdbcTemplate.query(sqlSelect, 
            (rs, rowNum) -> rs.getInt("id"),
            obraSocial.getNombre()
        );

        if (!results.isEmpty()) {
            return results.get(0);
        }

        // Si no existe, la creamos
        String sqlInsert = "INSERT INTO obras_sociales (nombre) VALUES (?) RETURNING id";
        return jdbcTemplate.queryForObject(sqlInsert, Integer.class, obraSocial.getNombre());
    }
}

