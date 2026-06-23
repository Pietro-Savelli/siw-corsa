package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Scarpa;
import it.uniroma3.siw.model.Utente;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ScarpaRepository extends CrudRepository<Scarpa, Long> {

    // Le scarpe di un atleta specifico (per il form "Crea Allenamento")
    List<Scarpa> findByAtleta(Utente atleta);

    long countByAtletaId(Long utenteId);
}