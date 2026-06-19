package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.UtenteRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    //follower
    public boolean segue(Long followerId, Long seguitoId) {
        return utenteRepository.existsByIdAndSeguitiId(followerId, seguitoId);
    }

    @Transactional
    public void segui(Long followerId, Long seguitoId) {
        if (followerId.equals(seguitoId)) {
            return;
        }
        Utente follower = utenteRepository.findById(followerId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + followerId));
        Utente seguito = utenteRepository.findById(seguitoId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + seguitoId));

        follower.getSeguiti().add(seguito);
        utenteRepository.save(follower);
    }

    @Transactional
    public void smettiDiSeguire(Long followerId, Long seguitoId) {
        Utente follower = utenteRepository.findById(followerId).get();
        Utente seguito = utenteRepository.findById(seguitoId)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + seguitoId));

        follower.getSeguiti().remove(seguito);
        utenteRepository.save(follower);
    }

    // Lista di chi l'utente segue
    public List<Utente> getSeguiti(Long utenteId) {
        return utenteRepository.findSeguiti(utenteId);
    }

    // Lista di chi segue l'utente
    public List<Utente> getFollower(Long utenteId) {
        return utenteRepository.findFollower(utenteId);
    }

    public @Nullable Object findAll() {
        return utenteRepository.findAll();
    }
}