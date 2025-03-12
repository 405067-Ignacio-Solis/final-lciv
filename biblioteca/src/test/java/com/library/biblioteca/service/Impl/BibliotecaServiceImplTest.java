package com.library.biblioteca.service.Impl;

import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import com.library.biblioteca.service.Impl.BibliotecaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BibliotecaServiceImplTest {

    @Mock
    private LibroRepository libroRepository;

    @Mock
    private RegistroRepository registroRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BibliotecaServiceImpl bibliotecaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testAlquilarLibros() {
        List<String> isbns = List.of("123", "456");

        Libro libro1 = new Libro();
        libro1.setIsbn("123");
        libro1.setEstado(EstadoLibro.DISPONIBLE);

        Libro libro2 = new Libro();
        libro2.setIsbn("456");
        libro2.setEstado(EstadoLibro.DISPONIBLE);

        when(libroRepository.findByIsbn("123")).thenReturn(libro1);
        when(libroRepository.findByIsbn("456")).thenReturn(libro2);

        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(1L);
        clienteDTO.setDomicilio("Calle Falsa 123");
        clienteDTO.setNombre("Bart Simpson");

        when(restTemplate.getForEntity(anyString(), eq(ClienteDTO.class))).thenReturn(new ResponseEntity<>(clienteDTO, HttpStatus.OK));

        Registro registro = bibliotecaService.alquilarLibros(isbns);

        assertEquals(1L, registro.getClienteId());
        assertEquals(2, registro.getLibrosReservados().size());
        assertEquals("Bart Simpson", registro.getNombreCliente());

        verify(registroRepository).save(any(Registro.class));
    }
//    @Test
//    void testAlquilarLibros() {
//        List<String> isbns = List.of("123", "456");
//
//        Libro libro1 = new Libro();
//        libro1.setIsbn("123");
//        libro1.setEstado(EstadoLibro.DISPONIBLE);
//
//        Libro libro2 = new Libro();
//        libro2.setIsbn("456");
//        libro2.setEstado(EstadoLibro.DISPONIBLE);
//
//        when(libroRepository.findByIsbn("123")).thenReturn(libro1);
//        when(libroRepository.findByIsbn("456")).thenReturn(libro2);
//
//        ClienteDTO clienteDTO = new ClienteDTO();
//        clienteDTO.setId(1L);
//        clienteDTO.setDomicilio("Calle Falsa 123");
//        clienteDTO.setNombre("Bart Simpson");
//
//        Registro registro = bibliotecaService.alquilarLibros(isbns);
//
//        assertEquals(1L, registro.getClienteId());
//        assertEquals(2, registro.getLibrosReservados().size());
//        assertEquals("Bart Simpson", registro.getNombreCliente());
//    }
//
//    @Test
//    void testAlquilarLibrosThrowsException() {
//        List<String> isbns = List.of("123", "456");
//
//        Libro libro1 = new Libro();
//        libro1.setIsbn("123");
//        libro1.setEstado(EstadoLibro.DISPONIBLE);
//
//        Libro libro2 = new Libro();
//        libro2.setIsbn("456");
//        libro2.setEstado(EstadoLibro.RESERVADO);
//
//        when(libroRepository.findByIsbn("123")).thenReturn(libro1);
//        when(libroRepository.findByIsbn("456")).thenReturn(libro2);
//
//        assertThrows(IllegalStateException.class, () -> bibliotecaService.alquilarLibros(isbns));
//    }

    @Test
    void testDevolverLibros() {
        Long registroId = 1L;

        Libro libro1 = new Libro();
        libro1.setIsbn("123");
        libro1.setEstado(EstadoLibro.RESERVADO);

        Libro libro2 = new Libro();
        libro2.setIsbn("456");
        libro2.setEstado(EstadoLibro.RESERVADO);

        List<Libro> libros = List.of(libro1, libro2);

        Registro registro = new Registro();
        registro.setId(registroId);
        registro.setClienteId(1L);
        registro.setLibrosReservados(libros);
        registro.setFechaReserva(LocalDate.now().minusDays(3));

        when(registroRepository.findById(registroId)).thenReturn(java.util.Optional.of(registro));

        Registro resultado = bibliotecaService.devolverLibros(registroId);

        assertEquals(LocalDate.now(), resultado.getFechaDevolucion());
        assertEquals(BigDecimal.valueOf(300), resultado.getTotal());
        assertEquals(EstadoLibro.DISPONIBLE, resultado.getLibrosReservados().get(0).getEstado());
        assertEquals(EstadoLibro.DISPONIBLE, resultado.getLibrosReservados().get(1).getEstado());
    }

    @Test
    void testDaysBetween() {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 10);

        int daysBetween = bibliotecaService.daysBetween(startDate, endDate);

        assertEquals(9, daysBetween);
    }

    @Test
    void testVerTodosLosAlquileres() {
        Registro registro1 = new Registro();
        registro1.setId(1L);
        registro1.setClienteId(1L);
        registro1.setNombreCliente("Bart Simpson");
        registro1.setFechaReserva(LocalDate.now().minusDays(3));

        Registro registro2 = new Registro();
        registro2.setId(2L);
        registro2.setClienteId(2L);
        registro2.setNombreCliente("Lisa Simpson");
        registro2.setFechaReserva(LocalDate.now().minusDays(2));

        List<Registro> registros = List.of(registro1, registro2);

        when(registroRepository.findAll()).thenReturn(registros);

        List<Registro> resultado = bibliotecaService.verTodosLosAlquileres();

        assertEquals(2, resultado.size());
        assertEquals("Bart Simpson", resultado.get(0).getNombreCliente());
        assertEquals("Lisa Simpson", resultado.get(1).getNombreCliente());
    }

    @Test
    void testCalcularCostoAlquiler() {
        LocalDate inicio = LocalDate.of(2023, 1, 1);
        LocalDate fin1 = LocalDate.of(2023, 1, 2);
        LocalDate fin2 = LocalDate.of(2023, 1, 4);
        LocalDate fin3 = LocalDate.of(2023, 1, 10);

        BigDecimal costo1 = bibliotecaService.calcularCostoAlquiler(inicio, fin1, 1);
        BigDecimal costo2 = bibliotecaService.calcularCostoAlquiler(inicio, fin2, 1);
        BigDecimal costo3 = bibliotecaService.calcularCostoAlquiler(inicio, fin3, 1);

        assertEquals(BigDecimal.valueOf(100), costo1);
        assertEquals(BigDecimal.valueOf(150), costo2);
        assertEquals(BigDecimal.valueOf(270), costo3);
    }
}
