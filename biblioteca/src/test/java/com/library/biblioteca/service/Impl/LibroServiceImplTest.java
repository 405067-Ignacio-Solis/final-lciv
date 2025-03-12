package com.library.biblioteca.service.Impl;

import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LibroServiceImplTest {

    @Mock
    private LibroRepository libroRepository;

    @InjectMocks
    private LibroServiceImpl libroServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegistrarLibro() {
        Libro libro = new Libro();
        libro.setTitulo("Test Libro");

        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        Libro result = libroServiceImpl.registrarLibro(libro);

        assertEquals(EstadoLibro.DISPONIBLE, result.getEstado());
        verify(libroRepository).save(libro);
    }

    @Test
    void testObtenerTodosLosLibros() {
        Libro libro1 = new Libro();
        libro1.setTitulo("Libro 1");

        Libro libro2 = new Libro();
        libro2.setTitulo("Libro 2");

        List<Libro> libros = Arrays.asList(libro1, libro2);

        when(libroRepository.findAll()).thenReturn(libros);

        List<Libro> result = libroServiceImpl.obtenerTodosLosLibros();

        assertEquals(2, result.size());
        assertEquals("Libro 1", result.get(0).getTitulo());
        assertEquals("Libro 2", result.get(1).getTitulo());
    }

    @Test
    void testActualizarLibro() {
        Libro libro = new Libro();
        libro.setTitulo("Updated Libro");

        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        Libro result = libroServiceImpl.actualizarLibro(libro);

        assertEquals("Updated Libro", result.getTitulo());
        verify(libroRepository).save(libro);
    }

    @Test
    void testEliminarLibro() {
        Long id = 1L;

        libroServiceImpl.eliminarLibro(id);

        verify(libroRepository).deleteById(id);
    }
}