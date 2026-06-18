package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    public Optional<Utente> findById(Long id) {
        return utenteRepository.findById(id);
    }

    public Utente salva(Utente utente) {
        return utenteRepository.save(utente);
    }


    //found by email
    public Optional<Utente> findByEmail(String email) {
        return utenteRepository.findByEmail(email);
    }
}