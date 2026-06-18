package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Scarpa;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.ScarpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ScarpaService {

    @Autowired
    private ScarpaRepository scarpaRepository;


    // UC2: Aggiungi Scarpa

    @Transactional
    public Scarpa salva(Scarpa scarpa, Utente atleta) {
        scarpa.setAtleta(atleta);
        scarpa.setKmTotali(0f); // nuova scarpa parte da 0 km
        return scarpaRepository.save(scarpa);
    }

    public List<Scarpa> findByAtleta(Utente atleta) {
        return scarpaRepository.findByAtleta(atleta);
    }

    public Optional<Scarpa> findById(Long id) {
        return scarpaRepository.findById(id);
    }
}