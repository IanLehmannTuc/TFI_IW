package tfi.infrastructure.persistence.repository.memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tfi.domain.entity.Usuario;
import tfi.domain.enums.Autoridad;
import tfi.domain.valueObject.Email;

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
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
        Usuario resultado = repository.add(usuario);
        
        assertNotNull(resultado);
        assertEquals(usuario, resultado);
        assertTrue(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void debeEncontrarUsuarioPorEmail() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        Optional<Usuario> resultado = repository.findByEmail("medico@hospital.com");
        
        assertTrue(resultado.isPresent());
        assertEquals(usuario, resultado.get());
    }

    @Test
    void findByEmailDebeSerCaseInsensitive() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        Optional<Usuario> resultado1 = repository.findByEmail("MEDICO@HOSPITAL.COM");
        Optional<Usuario> resultado2 = repository.findByEmail("MeDiCo@HoSpItAl.CoM");
        
        assertTrue(resultado1.isPresent());
        assertTrue(resultado2.isPresent());
        assertEquals(usuario, resultado1.get());
        assertEquals(usuario, resultado2.get());
    }

    @Test
    void findByEmailDebeRetornarVacioSiNoExiste() {
        Optional<Usuario> resultado = repository.findByEmail("noexiste@hospital.com");
        
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByEmailDebeRetornarVacioParaEmailNull() {
        Optional<Usuario> resultado = repository.findByEmail(null);
        
        assertTrue(resultado.isEmpty());
    }

    @Test
    void findByEmailDebeRetornarVacioParaEmailVacio() {
        Optional<Usuario> resultado = repository.findByEmail("");
        
        assertTrue(resultado.isEmpty());
    }

    @Test
    void existsByEmailDebeRetornarTrueSiExiste() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        assertTrue(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void existsByEmailDebeRetornarFalseSiNoExiste() {
        assertFalse(repository.existsByEmail("noexiste@hospital.com"));
    }

    @Test
    void existsByEmailDebeSerCaseInsensitive() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
 
        assertTrue(repository.existsByEmail("MEDICO@HOSPITAL.COM"));
        assertTrue(repository.existsByEmail("MeDiCo@HoSpItAl.CoM"));
    }

    @Test
    void noDebePermitirEmailsDuplicados() {
        Usuario usuario1 = new Usuario(
            Email.from("medico@hospital.com"),
            "hash1",
            Autoridad.MEDICO
        );
        Usuario usuario2 = new Usuario(
            Email.from("medico@hospital.com"),
            "hash2",
            Autoridad.ENFERMERO
        );
        repository.add(usuario1);
        
 
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(usuario2)
        );
        
        assertTrue(exception.getMessage().contains("Ya existe un usuario con el email"));
    }

    @Test
    void noDebePermitirEmailsDuplicadosCaseInsensitive() {
        Usuario usuario1 = new Usuario(
            Email.from("medico@hospital.com"),
            "hash1",
            Autoridad.MEDICO
        );
        Usuario usuario2 = new Usuario(
            Email.from("MEDICO@HOSPITAL.COM"),
            "hash2",
            Autoridad.ENFERMERO
        );
        repository.add(usuario1);
        
 
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(usuario2)
        );
    }

    @Test
    void addDebeLanzarExcepcionSiUsuarioEsNull() {
 
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> repository.add(null)
        );
        
        assertEquals("El usuario no puede ser nulo", exception.getMessage());
    }

    @Test
    void findAllDebeRetornarListaVaciaSiNoHayUsuarios() {
        List<Usuario> usuarios = repository.findAll();
        
        assertNotNull(usuarios);
        assertTrue(usuarios.isEmpty());
    }

    @Test
    void findAllDebeRetornarTodosLosUsuarios() {
        Usuario usuario1 = new Usuario(Email.from("medico1@hospital.com"), "hash1", Autoridad.MEDICO);
        Usuario usuario2 = new Usuario(Email.from("medico2@hospital.com"), "hash2", Autoridad.MEDICO);
        Usuario usuario3 = new Usuario(Email.from("enfermera@hospital.com"), "hash3", Autoridad.ENFERMERO);
        
        repository.add(usuario1);
        repository.add(usuario2);
        repository.add(usuario3);
        
        List<Usuario> usuarios = repository.findAll();
        
        assertEquals(3, usuarios.size());
        assertTrue(usuarios.contains(usuario1));
        assertTrue(usuarios.contains(usuario2));
        assertTrue(usuarios.contains(usuario3));
    }

    @Test
    void debeActualizarUsuarioExistente() {
        Email email = Email.from("medico@hospital.com");
        Usuario usuario = new Usuario(email, "hashOriginal", Autoridad.MEDICO);
        repository.add(usuario);
        
        Usuario usuarioActualizado = new Usuario(email, "hashNuevo", Autoridad.MEDICO);
        
        Usuario resultado = repository.update(usuarioActualizado);
        
        assertEquals(usuarioActualizado, resultado);
        Optional<Usuario> encontrado = repository.findByEmail("medico@hospital.com");
        assertTrue(encontrado.isPresent());
        assertEquals("hashNuevo", encontrado.get().getPasswordHash());
    }

    @Test
    void updateDebeLanzarExcepcionSiUsuarioNoExiste() {
        Usuario usuario = new Usuario(
            Email.from("noexiste@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        
 
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.update(usuario)
        );
    }

    @Test
    void updateDebeLanzarExcepcionSiUsuarioEsNull() {
 
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.update(null)
        );
    }

    @Test
    void debeEliminarUsuarioPorEmail() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        Usuario eliminado = repository.delete("medico@hospital.com");
        
        assertEquals(usuario, eliminado);
        assertFalse(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void deleteDebeLanzarExcepcionSiEmailNoExiste() {
 
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.delete("noexiste@hospital.com")
        );
    }

    @Test
    void deleteDebeLanzarExcepcionSiEmailEsNull() {
 
        assertThrows(
            IllegalArgumentException.class,
            () -> repository.delete(null)
        );
    }

    @Test
    void deleteDebeSerCaseInsensitive() {
        Usuario usuario = new Usuario(
            Email.from("medico@hospital.com"),
            "hash",
            Autoridad.MEDICO
        );
        repository.add(usuario);
        
        Usuario eliminado = repository.delete("MEDICO@HOSPITAL.COM");
        
        assertNotNull(eliminado);
        assertFalse(repository.existsByEmail("medico@hospital.com"));
    }

    @Test
    void deleteAllDebeEliminarTodosLosUsuarios() {
        repository.add(new Usuario(Email.from("medico1@hospital.com"), "hash1", Autoridad.MEDICO));
        repository.add(new Usuario(Email.from("medico2@hospital.com"), "hash2", Autoridad.MEDICO));
        repository.add(new Usuario(Email.from("enfermera@hospital.com"), "hash3", Autoridad.ENFERMERO));
        
        repository.deleteAll();
        
        List<Usuario> usuarios = repository.findAll();
        assertTrue(usuarios.isEmpty());
    }
}

