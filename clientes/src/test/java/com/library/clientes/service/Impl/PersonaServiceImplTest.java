package com.library.clientes.service.Impl;

import com.library.clientes.model.Persona;
import com.library.clientes.repository.PersonaRepository;
import com.library.clientes.service.PersonaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class PersonaServiceImplTest {

    @Mock
    private PersonaRepository personaRepository;

    @InjectMocks
    private PersonaServiceImpl personaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCrearPersona() {
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setNombre("Juan perez");

        when(personaRepository.save(any(Persona.class))).thenReturn(persona);

        Persona result = personaService.crearPersona(persona);

        assertEquals(persona.getId(), result.getId());
        assertEquals(persona.getNombre(), result.getNombre());
    }

    @Test
    void testObtenerPersonaPorId() {
        Persona persona = new Persona();
        persona.setId(1L);
        persona.setNombre("Juan perez");

        when(personaRepository.findById(anyLong())).thenReturn(Optional.of(persona));

        Optional<Persona> result = personaService.obtenerPersonaPorId(1L);

        assertTrue(result.isPresent());
        assertEquals(persona.getId(), result.get().getId());
        assertEquals(persona.getNombre(), result.get().getNombre());
    }

    @Test
    void testObtenerTodasLasPersonas() {
        List<Persona> personas = List.of(
                new Persona(1L, "Juan Perez", "Domicilio1"),
                new Persona(2L, "Maria Gomez", "Domicilio2")
        );

        when(personaRepository.findAll()).thenReturn(personas);

        List<Persona> result = personaService.obtenerTodasLasPersonas();

        assertEquals(2, result.size());
        assertEquals("Juan Perez", result.get(0).getNombre());
        assertEquals("Maria Gomez", result.get(1).getNombre());
    }

    @Test
    void testActualizarPersona() {
        Persona personaExistente = new Persona();
        personaExistente.setId(1L);
        personaExistente.setNombre("Juan Perez");

        Persona personaActualizada = new Persona();
        personaActualizada.setNombre("Juan Perez Actualizado");

        when(personaRepository.findById(anyLong())).thenReturn(Optional.of(personaExistente));
        when(personaRepository.save(any(Persona.class))).thenReturn(personaActualizada);

        Persona result = personaService.actualizarPersona(1L, personaActualizada);

        assertEquals(personaExistente.getId(), result.getId());
        assertEquals("Juan Perez Actualizado", result.getNombre());
    }

    @Test
    void testObtenerPersonaAlAzar() {
        List<Persona> personas = List.of(
                new Persona(1L, "Juan Perez", "Domicilio1"),
                new Persona(2L, "Maria Gomez", "Domicilio2")
        );

        when(personaRepository.findAll()).thenReturn(personas);

        Persona result = personaService.obtenerPersonaAlAzar();

        assertTrue(personas.contains(result));
    }
}