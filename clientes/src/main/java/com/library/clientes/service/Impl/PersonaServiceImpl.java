package com.library.clientes.service.Impl;

import com.library.clientes.model.Persona;
import com.library.clientes.repository.PersonaRepository;
import com.library.clientes.service.PersonaService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PersonaServiceImpl implements PersonaService {

    private final PersonaRepository personaRepository;

    public PersonaServiceImpl(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    @Override
    public Persona crearPersona(Persona persona) {
        return personaRepository.save(persona);
    }

    @Override
    public Optional<Persona> obtenerPersonaPorId(Long id) {
        return personaRepository.findById(id);
    }

    @Override
    public Persona obtenerPersonaAlAzar() {
        // DONE
        /*
         * Completar el metodo para retornar una persona de forma aleatoria
         */
        List<Persona> listPersonas = obtenerTodasLasPersonas();
        Random random = new Random();
        return listPersonas.get(random.nextInt(listPersonas.size()));
    }

    @Override
    public List<Persona> obtenerTodasLasPersonas() {
        return personaRepository.findAll();
    }

    @Override
    public Persona actualizarPersona(Long id, Persona personaActualizada) {
        Persona persona = personaRepository.findById(id).orElseThrow(() -> new RuntimeException("Persona no encontrada"));
        personaActualizada.setId(persona.getId());
        return personaRepository.save(personaActualizada);
    }

    @Override
    public void eliminarPersona(Long id) {
        personaRepository.deleteById(id);
    }
}
