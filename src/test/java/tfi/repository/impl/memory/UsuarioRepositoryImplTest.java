package tfi.repository.impl.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tfi.model.entity.Usuario;
import tfi.model.enums.Autoridad;
import tfi.model.valueObjects.Email;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para UsuarioRepositoryImpl.
 * Verifica operaciones CRUD en memoria.
 */
class UsuarioRepositoryImplTest {

    private UsuarioRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new UsuarioRepositoryImpl();
    }

    @Test
    void debeAgregarUsuarioCorrectamente() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        // Act
        Usuario resultado = repository.add(usuario);
        
        // Assert
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        assertTrue(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void debeEncontrarUsuarioPorEmail() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        // Act
        Optional<Usuario> resultado = repository.findByEmail("medico@hospital.com");
        
        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
    }

    @Test
    void findByEmailDebeSerCaseInsensitive() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        // Act
        Optional<Usuario> resultado1 = repository.findByEmail("MEDICO@HOSPITAL.COM");
        Optional<Usuario> resultado2 = repository.findByEmail("MeDiCo@HoSpItAl.CoM");
        
        // Assert
        assertTrue(resultado1.isPresent());
        assertTrue(resultado2.isPresent());
        assertEquals(usuario, resultado1.get());
        assertEquals(usuario, resultado2.get());
    }

    @Test
    void findByEmailDebeRetornarVacioSiNoExiste() {
        // Act
        Optional<Usuario> resultado = repository.findByEmail("noexiste@hospital.com");
        
        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByEmailDebeRetornarVacioParaEmailNull() {
        // Act
        Optional<Usuario> resultado = repository.findByEmail(null);
        
        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByEmailDebeRetornarVacioParaEmailVacio() {
        // Act
        Optional<Usuario> resultado = repository.findByEmail("");
        
        // Assert
        assertTrue(resultado.isEmpty());
    }

    @Test
    void existsByEmailDebeRetornarTrueSiExiste() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        // Act & Assert
        assertTrue(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void existsByEmailDebeRetornarFalseSiNoExiste() {
        // Act & Assert
        assertFalse(repository.existsByEmail("noexiste@hospital.com"));
    }

    @Test
    void existsByEmailDebeSerCaseInsensitive() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        // Act & Assert
        assertTrue(repository.existsByEmail("MEDICO@HOSPITAL.COM"));
        assertTrue(repository.existsByEmail("MeDiCo@HoSpItAl.CoM"));
    }

    @Test
    void noDebePermitirEmailsDuplicados() {
        // Arrange
        Usuario usuario1 = new Usuario(
            Email.from("medico@hospital.com"),
            "hash1",
            Autoridad.MEDICO
        );
        Usuario usuario2 = new Usuario(
            Email.from("medico@hospital.com"),
            "hash2",
            Autoridad.ENFERMERA
        );
        repository.add(usuario1);
        
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(usuario2)
        );
        
        assertTrue(exception.getMessage().contains("Ya existe un usuario con el email"));
    }

    @Test
    void noDebePermitirEmailsDuplicadosCaseInsensitive() {
        // Arrange
        Usuario usuario1 = new Usuario(
            Email.from("medico@hospital.com"),
            "hash1",
            Autoridad.MEDICO
        );
        Usuario usuario2 = new Usuario(
            Email.from("MEDICO@HOSPITAL.COM"),
            "hash2",
            Autoridad.ENFERMERA
        );
        repository.add(usuario1);
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(usuario2)
        );
    }

    @Test
    void addDebeLanzarExcepcionSiUsuarioEsNull() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(null)
        );
        
        assertEquals("El usuario no puede ser nulo", exception.getMessage());
    }

    @Test
    void findAllDebeRetornarListaVaciaSiNoHayUsuarios() {
        // Act
        List<Usuario> usuarios = repository.findAll();
        
        // Assert
        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    @Test
    void findAllDebeRetornarTodosLosUsuarios() {
        // Arrange
        Usuario usuario1 = new Usuario(Email.from("medico1@hospital.com"), "hash1", Autoridad.MEDICO);
        Usuario usuario2 = new Usuario(Email.from("medico2@hospital.com"), "hash2", Autoridad.MEDICO);
        Usuario usuario3 = new Usuario(Email.from("enfermera@hospital.com"), "hash3", Autoridad.ENFERMERA);
        
        repository.add(usuario1);
        repository.add(usuario2);
        repository.add(usuario3);
        
        // Act
        List<Usuario> usuarios = repository.findAll();
        
        // Assert
        assertEquals(3, usuarios.size());
        assertTrue(usuarios.contains(usuario1));
        assertTrue(usuarios.contains(usuario2));
        assertTrue(usuarios.contains(usuario3));
    }

    @Test
    void debeActualizarUsuarioExistente() {
        // Arrange
        Email email = Email.from("medico@hospital.com");
        Usuario usuario = new Usuario(email, "hashOriginal", Autoridad.MEDICO);
        repository.add(usuario);
        
        Usuario usuarioActualizado = new Usuario(email, "hashNuevo", Autoridad.MEDICO);
        
        // Act
        Usuario resultado = repository.update(usuarioActualizado);
        
        // Assert
        assertEquals(usuarioActualizado, resultado);
        Optional<Usuario> encontrado = repository.findByEmail("medico@hospital.com");
        assertTrue(encontrado.isPresent());
        assertEquals("hashNuevo", encontrado.get().getPasswordHash());
    }

    @Test
    void updateDebeLanzarExcepcionSiUsuarioNoExiste() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("noexiste@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.update(usuario)
        );
    }

    @Test
    void updateDebeLanzarExcepcionSiUsuarioEsNull() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.update(null)
        );
    }

    @Test
    void debeEliminarUsuarioPorEmail() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        // Act
        Usuario eliminado = repository.delete("medico@hospital.com");
        
        // Assert
        assertEquals(usuario, eliminado);
        assertFalse(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void deleteDebeLanzarExcepcionSiEmailNoExiste() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.delete("noexiste@hospital.com")
        );
    }

    @Test
    void deleteDebeLanzarExcepcionSiEmailEsNull() {
        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.delete(null)
        );
    }

    @Test
    void deleteDebeSerCaseInsensitive() {
        // Arrange
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        // Act
        Usuario eliminado = repository.delete("MEDICO@HOSPITAL.COM");
        
        // Assert
        assertNotNull(eliminado);
        assertFalse(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void deleteAllDebeEliminarTodosLosUsuarios() {
        // Arrange
        repository.add(new Usuario(Email.from("medico1@hospital.com"), "hash1", Autoridad.MEDICO));
        repository.add(new Usuario(Email.from("medico2@hospital.com"), "hash2", Autoridad.MEDICO));
        repository.add(new Usuario(Email.from("enfermera@hospital.com"), "hash3", Autoridad.ENFERMERA));
        
        // Act
        repository.deleteAll();
        
        // Assert
        List<Usuario> usuarios = repository.findAll();
        assertTrue(usuarios.isEmpty());
    }
}

