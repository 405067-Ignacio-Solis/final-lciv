package com.library.biblioteca.service.Impl;


import com.library.biblioteca.dto.ClienteDTO;
import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.model.Registro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.repository.RegistroRepository;
import com.library.biblioteca.service.BibliotecaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.cglib.core.Local;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class BibliotecaServiceImpl implements BibliotecaService {
    private final RegistroRepository registroRepository;
    private final LibroRepository libroRepository;
    private final RestTemplate restTemplate;

    public BibliotecaServiceImpl(LibroRepository libroRepository, RegistroRepository registroRepository,  RestTemplate restTemplate) {
        this.registroRepository = registroRepository;
        this.libroRepository = libroRepository;
        this.restTemplate = restTemplate;
    }


    @Override
    public Registro alquilarLibros(List<String> isbns) {
        //TODO
        /**
         * Completar el metodo de alquiler
         * Se debe buscar la lista de libros por su codigo de isbn,
         * validar que los libros a alquilar tengan estado DISPONIBLE sino arrojar una exception
         * ya que solo se pueden alquilar libros que esten en dicho estado
         * throw new IllegalStateException("Uno o más libros ya están reservados.")
         * Recuperar un cliente desde la api externa /api/personas/aleatorio y guardar la reserva
         */
        ArrayList<Libro> libros = new ArrayList<>();
        for (String isbn : isbns) {
            Libro libroPorIsbn = libroRepository.findByIsbn(isbn);
            if (libroPorIsbn != null) {
                if (libroPorIsbn.getEstado() == EstadoLibro.DISPONIBLE) {
                    libros.add(libroPorIsbn);
                }
                else {
                    throw new IllegalStateException("Uno o más libros ya están reservados.");
                }
            }
        }
        //crear reserva

        ClienteDTO clienteDTO = getRandomClient();

        Registro registro = new Registro();
        registro.setClienteId(clienteDTO.getId());
        registro.setLibrosReservados(libros);
        registro.setNombreCliente(clienteDTO.getNombre());
        registro.setFechaReserva(LocalDate.now());
        registroRepository.save(registro);
        return registro;
    }

    @Override
    public Registro devolverLibros(Long registroId) {
        //DONE
        /**
         * Completar el metodo de devolucion
         * Se debe buscar la reserva por su id,
         * actualizar la fecha de devolucion y calcular el importe a facturar,
         * actualizar el estado de los libros a DISPONIBLE
         * y guardar el registro con los datos actualizados 
         */

        Registro registro = registroRepository.findById(registroId).orElse((null));
        if (registro == null) {
            throw new EntityNotFoundException("No se encontró la reserva");
        }
        else {
            registro.setFechaDevolucion(LocalDate.now());
            //calcular total
            LocalDate fechaReserva = registro.getFechaReserva();
            LocalDate fechaDevolucion = registro.getFechaDevolucion();
            int cantidadLibros = registro.getLibrosReservados().size();
            registro.setTotal(calcularCostoAlquiler(fechaReserva,fechaDevolucion,cantidadLibros));
            //actualizar estado libros
            List<Libro> libros = registro.getLibrosReservados();
            for (Libro libro : libros) {
                libro.setEstado(EstadoLibro.DISPONIBLE);
            }
            registroRepository.save(registro);
        }
        return registro;
    }

    @Override
    public List<Registro> verTodosLosAlquileres() {
        return registroRepository.findAll();
    }

    // Cálculo de costo de alquiler
    public BigDecimal calcularCostoAlquiler(LocalDate inicio, LocalDate fin, int cantidadLibros) {
        //DONE
        /**
         * Completar el metodo de calculo
         * se calcula el importe a pagar por libro en funcion de la cantidad de dias,
         * es la diferencia entre el alquiler y la devolucion, respetando la siguiente tabla:
         * hasta 2 dias se debe pagar $100 por libro
         * desde 3 dias y hasta 5 dias se debe pagar $150 por libro
         * más de 5 dias se debe pagar $150 por libro + $30 por cada día extra
         */
        int diasAlquiler = daysBetween(inicio,fin);
        BigDecimal costo = BigDecimal.valueOf(0);
        if (diasAlquiler <= 2) {
            costo = BigDecimal.valueOf(cantidadLibros * 100);
        }
        else if (diasAlquiler >= 3 && diasAlquiler < 5) {
            costo = BigDecimal.valueOf(cantidadLibros * 150);
        }
        else if (diasAlquiler > 5 ) {
            int diasExtra = diasAlquiler - 5;
            costo = BigDecimal.valueOf(cantidadLibros * 150);
            costo = costo.add(BigDecimal.valueOf(diasExtra * 30));

        }
        return costo;
    }

    @Override
    public List<Registro> informeSemanal(LocalDate fechaInicio) {
        //TODO
        /**
         * Completar el metodo de reporte semanal
         * se debe retornar la lista de registros de la semana tomando como referencia
         * la fecha de inicio para la busqueda
         */
        LocalDate fechaHasta = fechaInicio.plusDays(7);
        return registroRepository.obtenerRegistrosSemana(fechaInicio, fechaHasta);
    }

    @Override
    public List<Object[]> informeLibrosMasAlquilados() {
        //DONE
        /**
         * Completar el metodo de reporte de libros mas alquilados
         * se debe retornar la lista de libros mas alquilados
         */
        return registroRepository.obtenerLibrosMasAlquilados();

    }

    public int daysBetween(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate);
    }

    public ClienteDTO getRandomClient() {
        //tira connection refused pero estan en la misma network?
        //hay que pegarle al api gateway?
        ResponseEntity<ClienteDTO> clienteResponse = restTemplate.getForEntity("http://localhost:8080/api/personas", ClienteDTO.class);
        return clienteResponse.getBody();
    }
}
