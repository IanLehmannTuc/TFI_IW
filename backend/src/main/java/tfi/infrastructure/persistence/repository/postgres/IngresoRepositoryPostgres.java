package tfi.infrastructure.persistence.repository.postgres;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.*;
import tfi.domain.enums.Autoridad;
import tfi.domain.enums.Estado;
import tfi.domain.enums.NivelEmergencia;
import tfi.domain.repository.IngresoRepository;
import tfi.domain.valueObject.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Ingreso usando PostgreSQL con JDBC puro.
 * No utiliza JPA ni ningún ORM.
 * 
 * Se activa cuando el perfil "postgres" está activo.
 */
@Repository
@Profile("postgres")
public class IngresoRepositoryPostgres implements IngresoRepository {

    private final JdbcTemplate jdbcTemplate;

    public IngresoRepositoryPostgres(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper para convertir ResultSet a Ingreso.
     */
    private class IngresoRowMapper implements RowMapper<Ingreso> {
        @Override
        public Ingreso mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException {
            
            String id = rs.getString("id");
            String descripcion = rs.getString("descripcion");
            Timestamp timestamp = rs.getTimestamp("fecha_hora_ingreso");
            LocalDateTime fechaHoraIngreso = timestamp != null ? timestamp.toLocalDateTime() : null;

            
            Paciente paciente = mapPaciente(rs);

            
            Usuario enfermero = mapUsuario(rs, "enfermero_");

            
            Temperatura temperatura = null;
            Double tempValor = (Double) rs.getObject("temperatura");
            if (tempValor != null) {
                temperatura = new Temperatura(tempValor);
            }

            TensionArterial tensionArterial = null;
            Integer sistolica = (Integer) rs.getObject("presion_sistolica");
            Integer diastolica = (Integer) rs.getObject("presion_diastolica");
            if (sistolica != null && diastolica != null) {
                tensionArterial = new TensionArterial(
                    new Presion(sistolica),
                    new Presion(diastolica)
                );
            }

            FrecuenciaCardiaca frecuenciaCardiaca = null;
            Integer fcValor = (Integer) rs.getObject("frecuencia_cardiaca");
            if (fcValor != null) {
                frecuenciaCardiaca = new FrecuenciaCardiaca(fcValor);
            }

            FrecuenciaRespiratoria frecuenciaRespiratoria = null;
            Integer frValor = (Integer) rs.getObject("frecuencia_respiratoria");
            if (frValor != null) {
                frecuenciaRespiratoria = new FrecuenciaRespiratoria(frValor);
            }

            
            String nivelStr = rs.getString("nivel_emergencia");
            NivelEmergencia nivelEmergencia = NivelEmergencia.valueOf(nivelStr);

            
            String estadoStr = rs.getString("estado");
            Estado estado = Estado.valueOf(estadoStr);

            
            Atencion atencion = null;

            
            Ingreso ingreso = new Ingreso(
                atencion,
                paciente,
                enfermero,
                descripcion,
                fechaHoraIngreso,
                temperatura,
                tensionArterial,
                frecuenciaCardiaca,
                frecuenciaRespiratoria,
                nivelEmergencia
            );

            ingreso.setId(id);
            ingreso.setEstado(estado);

            return ingreso;
        }

        private Paciente mapPaciente(ResultSet rs) throws SQLException {
            String id = rs.getString("paciente_id");
            String cuil = rs.getString("paciente_cuil");
            String nombre = rs.getString("paciente_nombre");
            String apellido = rs.getString("paciente_apellido");
            String email = rs.getString("paciente_email");

            
            Domicilio domicilio = null;
            String calle = rs.getString("paciente_domicilio_calle");
            if (calle != null) {
                int numero = rs.getInt("paciente_domicilio_numero");
                String localidad = rs.getString("paciente_domicilio_localidad");
                domicilio = new Domicilio(calle, numero, localidad);
            }

            
            
            
            Afiliado afiliado = null;
            Integer obraSocialId = (Integer) rs.getObject("paciente_obra_social_id");
            if (obraSocialId != null) {
                
                String nombreObraSocial = "Obra Social " + obraSocialId;
                String numeroAfiliado = rs.getString("paciente_numero_afiliado");
                ObraSocial obraSocial = new ObraSocial(obraSocialId, nombreObraSocial);
                afiliado = new Afiliado(obraSocial, numeroAfiliado);
            }

            Paciente paciente;
            if (nombre == null) {
                paciente = new Paciente(cuil, domicilio, afiliado);
            } else {
                paciente = new Paciente(cuil, nombre, apellido, email, domicilio, afiliado);
            }
            
            paciente.setId(id);
            return paciente;
        }

        /**
         * Mapea un Usuario desde el ResultSet. 
         * Usa un prefijo para distinguir entre diferentes usuarios en el mismo ResultSet.
         * 
         * @param rs ResultSet con los datos
         * @param prefix Prefijo de las columnas (ej: "enfermero_", "doctor_")
         * @return Usuario mapeado
         */
        private Usuario mapUsuario(ResultSet rs, String prefix) throws SQLException {
            String id = rs.getString(prefix + "id");
            String email = rs.getString(prefix + "email");
            String passwordHash = rs.getString(prefix + "password_hash");
            String autoridadStr = rs.getString(prefix + "autoridad");
            String cuil = rs.getString(prefix + "cuil");
            String nombre = rs.getString(prefix + "nombre");
            String apellido = rs.getString(prefix + "apellido");
            String matricula = rs.getString(prefix + "matricula");

            Email emailVO = Email.from(email);
            Autoridad autoridad = Autoridad.valueOf(autoridadStr);
            Cuil cuilVO = new Cuil(cuil);

            Usuario usuario = new Usuario(emailVO, passwordHash, autoridad, cuilVO, nombre, apellido, matricula);
            usuario.setId(id);
            return usuario;
        }
    }

    @Override
    public List<Ingreso> findAll() {
        String sql = buildSelectQuery() + " ORDER BY i.fecha_hora_ingreso DESC";
        return jdbcTemplate.query(sql, new IngresoRowMapper());
    }

    @Override
    public Ingreso add(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }

        if (ingreso.getFechaHoraIngreso() == null) {
            ingreso.setFechaHoraIngreso(LocalDateTime.now());
        }

        
        if (ingreso.getNivelEmergencia() == null) {
            throw new IllegalArgumentException("El nivel de emergencia no puede ser nulo");
        }
        if (ingreso.getEstado() == null) {
            ingreso.setEstado(Estado.PENDIENTE);
        }

        
        String sql = "INSERT INTO ingresos (paciente_id, enfermero_id, " +
                     "descripcion, fecha_hora_ingreso, temperatura, " +
                     "presion_sistolica, presion_diastolica, frecuencia_cardiaca, frecuencia_respiratoria, " +
                     "nivel_emergencia, estado) " +
                     "VALUES (CAST(? AS UUID), CAST(? AS UUID), ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id::text";

        String generatedId = jdbcTemplate.queryForObject(sql, String.class,
            ingreso.getPaciente() != null ? ingreso.getPaciente().getId() : null,
            ingreso.getEnfermero() != null ? ingreso.getEnfermero().getId() : null,
            ingreso.getDescripcion(),
            Timestamp.valueOf(ingreso.getFechaHoraIngreso()),
            ingreso.getTemperatura() != null ? ingreso.getTemperatura().getValor() : null,
            ingreso.getTensionArterial() != null ? ingreso.getTensionArterial().getPresionSistolica().getValor() : null,
            ingreso.getTensionArterial() != null ? ingreso.getTensionArterial().getPresionDiastolica().getValor() : null,
            ingreso.getFrecuenciaCardiaca() != null ? ingreso.getFrecuenciaCardiaca().getValor() : null,
            ingreso.getFrecuenciaRespiratoria() != null ? ingreso.getFrecuenciaRespiratoria().getValor() : null,
            ingreso.getNivelEmergencia().name(),
            ingreso.getEstado().name()
        );

        ingreso.setId(generatedId);
        return ingreso;
    }

    @Override
    public Ingreso update(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }

        if (ingreso.getId() == null || ingreso.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ingreso debe tener un ID");
        }

        if (!existsById(ingreso.getId())) {
            throw new IllegalStateException("No existe un ingreso con el ID: " + ingreso.getId());
        }

        String sql = "UPDATE ingresos SET paciente_id = CAST(? AS UUID), enfermero_id = CAST(? AS UUID), " +
                     "descripcion = ?, fecha_hora_ingreso = ?, temperatura = ?, " +
                     "presion_sistolica = ?, presion_diastolica = ?, frecuencia_cardiaca = ?, " +
                     "frecuencia_respiratoria = ?, nivel_emergencia = ?, estado = ? " +
                     "WHERE id = CAST(? AS UUID)";

        jdbcTemplate.update(sql,
            ingreso.getPaciente() != null ? ingreso.getPaciente().getId() : null,
            ingreso.getEnfermero() != null ? ingreso.getEnfermero().getId() : null,
            ingreso.getDescripcion(),
            Timestamp.valueOf(ingreso.getFechaHoraIngreso()),
            ingreso.getTemperatura() != null ? ingreso.getTemperatura().getValor() : null,
            ingreso.getTensionArterial() != null ? ingreso.getTensionArterial().getPresionSistolica().getValor() : null,
            ingreso.getTensionArterial() != null ? ingreso.getTensionArterial().getPresionDiastolica().getValor() : null,
            ingreso.getFrecuenciaCardiaca() != null ? ingreso.getFrecuenciaCardiaca().getValor() : null,
            ingreso.getFrecuenciaRespiratoria() != null ? ingreso.getFrecuenciaRespiratoria().getValor() : null,
            ingreso.getNivelEmergencia().name(),
            ingreso.getEstado().name(),
            ingreso.getId()
        );

        return ingreso;
    }

    @Override
    public Ingreso delete(Ingreso ingreso) {
        if (ingreso == null) {
            throw new IllegalArgumentException("El ingreso no puede ser nulo");
        }

        if (ingreso.getId() == null || ingreso.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ingreso debe tener un ID");
        }

        
        Optional<Ingreso> existing = findById(ingreso.getId());
        if (existing.isEmpty()) {
            throw new IllegalStateException("No existe un ingreso con el ID: " + ingreso.getId());
        }

        String sql = "DELETE FROM ingresos WHERE id = CAST(? AS UUID)";
        jdbcTemplate.update(sql, ingreso.getId());

        return existing.get();
    }

    @Override
    public Optional<Ingreso> findById(String ingresoId) {
        if (ingresoId == null || ingresoId.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = buildSelectQuery() + " WHERE i.id = CAST(? AS UUID)";
        List<Ingreso> results = jdbcTemplate.query(sql, new IngresoRowMapper(), ingresoId);

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Verifica si existe un ingreso con el ID especificado.
     */
    private boolean existsById(String ingresoId) {
        String sql = "SELECT COUNT(*) FROM ingresos WHERE id = CAST(? AS UUID)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, ingresoId);
        return count != null && count > 0;
    }

    /**
     * Construye la consulta SELECT base con todos los JOINs necesarios.
     */
    private String buildSelectQuery() {
        return "SELECT " +
               "i.id, i.descripcion, i.fecha_hora_ingreso, " +
               "i.temperatura, i.presion_sistolica, i.presion_diastolica, " +
               "i.frecuencia_cardiaca, i.frecuencia_respiratoria, " +
               "i.nivel_emergencia, i.estado, " +
               
               "p.id AS paciente_id, p.cuil AS paciente_cuil, p.nombre AS paciente_nombre, " +
               "p.apellido AS paciente_apellido, p.email AS paciente_email, " +
               "p.domicilio_calle AS paciente_domicilio_calle, " +
               "p.domicilio_numero AS paciente_domicilio_numero, " +
               "p.domicilio_localidad AS paciente_domicilio_localidad, " +
               "p.obra_social_id AS paciente_obra_social_id, " +
               "p.numero_afiliado AS paciente_numero_afiliado, " +
               
               "e.id AS enfermero_id, e.email AS enfermero_email, e.password_hash AS enfermero_password_hash, " +
               "e.autoridad AS enfermero_autoridad, e.cuil AS enfermero_cuil, " +
               "e.nombre AS enfermero_nombre, e.apellido AS enfermero_apellido, " +
               "e.matricula AS enfermero_matricula " +
               "FROM ingresos i " +
               "INNER JOIN pacientes p ON i.paciente_id = p.id " +
               "INNER JOIN usuarios e ON i.enfermero_id = e.id";
    }
}
