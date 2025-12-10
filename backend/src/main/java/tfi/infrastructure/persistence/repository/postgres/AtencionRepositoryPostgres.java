package tfi.infrastructure.persistence.repository.postgres;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.Atencion;
import tfi.domain.repository.AtencionRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Atencion usando PostgreSQL con JDBC puro.
 * Se activa cuando el perfil "postgres" está activo.
 */
@Repository
@Profile("postgres")
public class AtencionRepositoryPostgres implements AtencionRepository {

    private final JdbcTemplate jdbcTemplate;

    public AtencionRepositoryPostgres(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper para convertir ResultSet a Atencion.
     */
    private static class AtencionRowMapper implements RowMapper<Atencion> {
        @Override
        public Atencion mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String ingresoId = rs.getString("ingreso_id");
            String medicoId = rs.getString("medico_id");
            String informeMedico = rs.getString("informe_medico");
            Timestamp timestamp = rs.getTimestamp("fecha_atencion");
            LocalDateTime fechaAtencion = timestamp != null ? timestamp.toLocalDateTime() : null;

            return new Atencion(id, ingresoId, medicoId, informeMedico, fechaAtencion);
        }
    }

    @Override
    public Optional<Atencion> findById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT id::text, ingreso_id::text, medico_id::text, informe_medico, fecha_atencion " +
                     "FROM atenciones WHERE id = CAST(? AS UUID)";
        
        List<Atencion> results = jdbcTemplate.query(sql, new AtencionRowMapper(), id);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Atencion> findByIngresoId(String ingresoId) {
        if (ingresoId == null || ingresoId.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT id::text, ingreso_id::text, medico_id::text, informe_medico, fecha_atencion " +
                     "FROM atenciones WHERE ingreso_id = CAST(? AS UUID)";
        
        List<Atencion> results = jdbcTemplate.query(sql, new AtencionRowMapper(), ingresoId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Atencion add(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("La atención no puede ser nula");
        }

        if (atencion.getIngresoId() == null || atencion.getIngresoId().trim().isEmpty()) {
            throw new IllegalArgumentException("La atención debe tener un ID de ingreso");
        }

        if (atencion.getMedicoId() == null || atencion.getMedicoId().trim().isEmpty()) {
            throw new IllegalArgumentException("La atención debe tener un ID de médico");
        }

        if (atencion.getInformeMedico() == null || atencion.getInformeMedico().trim().isEmpty()) {
            throw new IllegalArgumentException("La atención debe tener un informe médico");
        }

        
        if (findByIngresoId(atencion.getIngresoId()).isPresent()) {
            throw new IllegalStateException("Ya existe una atención registrada para este ingreso");
        }

        LocalDateTime fechaAtencion = atencion.getFechaAtencion();
        if (fechaAtencion == null) {
            fechaAtencion = LocalDateTime.now();
        }

        
        String sql = "INSERT INTO atenciones (ingreso_id, medico_id, informe_medico, fecha_atencion) " +
                     "VALUES (CAST(? AS UUID), CAST(? AS UUID), ?, ?) RETURNING id::text";

        String generatedId = jdbcTemplate.queryForObject(sql, String.class,
            atencion.getIngresoId(),
            atencion.getMedicoId(),
            atencion.getInformeMedico(),
            Timestamp.valueOf(fechaAtencion)
        );

        atencion.setId(generatedId);
        atencion.setFechaAtencion(fechaAtencion);
        return atencion;
    }

    @Override
    public Atencion update(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("La atención no puede ser nula");
        }

        if (atencion.getId() == null || atencion.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("La atención debe tener un ID para ser actualizada");
        }

        if (!existsById(atencion.getId())) {
            throw new IllegalStateException("No existe una atención con el ID: " + atencion.getId());
        }

        String sql = "UPDATE atenciones SET ingreso_id = CAST(? AS UUID), medico_id = CAST(? AS UUID), " +
                     "informe_medico = ?, fecha_atencion = ? WHERE id = CAST(? AS UUID)";

        jdbcTemplate.update(sql,
            atencion.getIngresoId(),
            atencion.getMedicoId(),
            atencion.getInformeMedico(),
            Timestamp.valueOf(atencion.getFechaAtencion()),
            atencion.getId()
        );

        return atencion;
    }

    @Override
    public List<Atencion> findAll() {
        String sql = "SELECT id::text, ingreso_id::text, medico_id::text, informe_medico, fecha_atencion " +
                     "FROM atenciones ORDER BY fecha_atencion DESC";
        return jdbcTemplate.query(sql, new AtencionRowMapper());
    }

    @Override
    public Atencion delete(Atencion atencion) {
        if (atencion == null) {
            throw new IllegalArgumentException("La atención no puede ser nula");
        }

        if (atencion.getId() == null || atencion.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("La atención debe tener un ID para ser eliminada");
        }

        
        Optional<Atencion> existing = findById(atencion.getId());
        if (existing.isEmpty()) {
            throw new IllegalStateException("No existe una atención con el ID: " + atencion.getId());
        }

        String sql = "DELETE FROM atenciones WHERE id = CAST(? AS UUID)";
        jdbcTemplate.update(sql, atencion.getId());

        return existing.get();
    }

    /**
     * Verifica si existe una atención con el ID especificado.
     */
    private boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM atenciones WHERE id = CAST(? AS UUID)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
}
