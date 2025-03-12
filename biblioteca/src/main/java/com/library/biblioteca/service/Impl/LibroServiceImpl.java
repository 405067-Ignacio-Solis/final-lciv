package com.library.biblioteca.service.Impl;

import com.library.biblioteca.enums.EstadoLibro;
import com.library.biblioteca.model.Libro;
import com.library.biblioteca.repository.LibroRepository;
import com.library.biblioteca.service.LibroService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroServiceImpl implements LibroService {

    private final LibroRepository libroRepository;

    public LibroServiceImpl(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @Override
    public Libro registrarLibro(Libro libro) {
        //DONE
        /**
         * Completar el metodo de registro
         * el estado inicial del libro debe ser DISPONIBLE
         */
        libro.setEstado(EstadoLibro.DISPONIBLE);
        libroRepository.save(libro);
        return libro;
    }

    @Override
    public List<Libro> obtenerTodosLosLibros() {
        //DONE
        return libroRepository.findAll();
        
    }

    @Override
    public void eliminarLibro(Long id) {
        //DONE
        libroRepository.deleteById(id);
    }

    @Override
    public Libro actualizarLibro(Libro libro) {
        //DONE
        libroRepository.save(libro);
        return libro;
    }
}
