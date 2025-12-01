package tfi.infrastructure.persistence.repository.postgres;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import tfi.domain.entity.Usuario;
import tfi.domain.enums.Autoridad;
import tfi.domain.repository.UsuarioRepository;
import tfi.domain.valueObject.Cuil;
import tfi.domain.valueObject.Email;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de Usuario usando PostgreSQL con JDBC puro.
 * No utiliza JPA ni ningún ORM.
 * 
 * Se activa cuando el perfil "postgres" está activo.
 */
@Repository
@Profile("postgres")
public class UsuarioRepositoryPostgres implements UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepositoryPostgres(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * RowMapper para convertir ResultSet a Usuario.
     */
    private static class UsuarioRowMapper implements RowMapper<Usuario> {
        @Override
        public Usuario mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id");
            String emailStr = rs.getString("email");
            String passwordHash = rs.getString("password_hash");
            String autoridadStr = rs.getString("autoridad");
            String cuilStr = rs.getString("cuil");
            String nombre = rs.getString("nombre");
            String apellido = rs.getString("apellido");
            String matricula = rs.getString("matricula");

            Email email = Email.from(emailStr);
            Autoridad autoridad = Autoridad.valueOf(autoridadStr);
            Cuil cuil = new Cuil(cuilStr);

            Usuario usuario = new Usuario(email, passwordHash, autoridad, cuil, nombre, apellido, matricula);
            usuario.setId(id);
            return usuario;
        }
    }

    @Override
    public Usuario add(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getEmail() == null) {
            throw new IllegalArgumentException("El email del usuario no puede ser nulo");
        }
        if (usuario.getCuilVO() == null) {
            throw new IllegalArgumentException("El CUIL del usuario no puede ser nulo");
        }

        String emailKey = usuario.getEmail().getValue().toLowerCase();

        if (existsByEmail(emailKey)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail().getValue());
        }
        if (existsByCuil(usuario.getCuil())) {
            throw new IllegalArgumentException("Ya existe un usuario con el CUIL: " + usuario.getCuil());
        }
        if (existsByMatricula(usuario.getMatricula())) {
            throw new IllegalArgumentException("Ya existe un usuario con la matrícula: " + usuario.getMatricula());
        }

        String sql = "INSERT INTO usuarios (email, password_hash, autoridad, cuil, nombre, apellido, matricula) " +
                     "VALUES (?, ?, ?::text, ?, ?, ?, ?) RETURNING id";
        
        String generatedId = jdbcTemplate.queryForObject(sql, String.class,
            emailKey,
            usuario.getPasswordHash(),
            usuario.getAutoridad().name(),
            usuario.getCuil(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getMatricula()
        );
        
        usuario.setId(generatedId);
        return usuario;
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }

        String emailKey = email.toLowerCase();
        String sql = "SELECT id, email, password_hash, autoridad, cuil, nombre, apellido, matricula " +
                     "FROM usuarios WHERE email = ?";
        
        List<Usuario> results = jdbcTemplate.query(sql, new UsuarioRowMapper(), emailKey);
        
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Usuario> findByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT id, email, password_hash, autoridad, cuil, nombre, apellido, matricula " +
                     "FROM usuarios WHERE cuil = ?";
        
        List<Usuario> results = jdbcTemplate.query(sql, new UsuarioRowMapper(), cuil);
        
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public Optional<Usuario> findByMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = "SELECT id, email, password_hash, autoridad, cuil, nombre, apellido, matricula " +
                     "FROM usuarios WHERE matricula = ?";
        
        List<Usuario> results = jdbcTemplate.query(sql, new UsuarioRowMapper(), matricula);
        
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        String emailKey = email.toLowerCase();
        String sql = "SELECT COUNT(*) FROM usuarios WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, emailKey);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByCuil(String cuil) {
        if (cuil == null || cuil.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM usuarios WHERE cuil = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, cuil);
        return count != null && count > 0;
    }

    @Override
    public boolean existsByMatricula(String matricula) {
        if (matricula == null || matricula.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM usuarios WHERE matricula = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, matricula);
        return count != null && count > 0;
    }

    @Override
    public List<Usuario> findAll() {
        String sql = "SELECT id, email, password_hash, autoridad, cuil, nombre, apellido, matricula FROM usuarios";
        return jdbcTemplate.query(sql, new UsuarioRowMapper());
    }

    @Override
    public List<Usuario> findByAutoridad(Autoridad autoridad) {
        if (autoridad == null) {
            throw new IllegalArgumentException("La autoridad no puede ser nula");
        }

        String sql = "SELECT id, email, password_hash, autoridad, cuil, nombre, apellido, matricula " +
                     "FROM usuarios WHERE autoridad = ?::text";
        return jdbcTemplate.query(sql, new UsuarioRowMapper(), autoridad.name());
    }

    @Override
    public Usuario update(Usuario usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        if (usuario.getEmail() == null) {
            throw new IllegalArgumentException("El email del usuario no puede ser nulo");
        }

        String emailKey = usuario.getEmail().getValue().toLowerCase();

        if (!existsByEmail(emailKey)) {
            throw new IllegalArgumentException("No existe un usuario con el email: " + usuario.getEmail().getValue());
        }

        String sql = "UPDATE usuarios SET password_hash = ?, autoridad = ?::text, cuil = ?, " +
                     "nombre = ?, apellido = ?, matricula = ? WHERE email = ?";
        
        jdbcTemplate.update(sql,
            usuario.getPasswordHash(),
            usuario.getAutoridad().name(),
            usuario.getCuil(),
            usuario.getNombre(),
            usuario.getApellido(),
            usuario.getMatricula(),
            emailKey
        );

        return usuario;
    }

    @Override
    public Usuario delete(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede ser nulo o vacío");
        }

        String emailKey = email.toLowerCase();

        // Primero obtenemos el usuario
        Optional<Usuario> usuario = findByEmail(emailKey);
        if (usuario.isEmpty()) {
            throw new IllegalArgumentException("No existe un usuario con el email: " + email);
        }

        String sql = "DELETE FROM usuarios WHERE email = ?";
        jdbcTemplate.update(sql, emailKey);

        return usuario.get();
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM usuarios";
        jdbcTemplate.update(sql);
    }
}

