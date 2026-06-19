package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Utente;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UtenteRepository extends CrudRepository<Utente, Long> {

    Optional<Utente> findByEmail(String email);

    boolean existsByIdAndSeguitiId(Long utenteId, Long seguitoId);

    // Lista di chi un utente segue
    @Query("""
        SELECT s FROM Utente u JOIN u.seguiti s WHERE u.id = :utenteId
        ORDER BY s.nome, s.cognome
        """)
    List<Utente> findSeguiti(@Param("utenteId") Long utenteId);

    // Lista di chi segue un utente
    @Query("""
        SELECT u FROM Utente u JOIN u.seguiti s WHERE s.id = :utenteId
        ORDER BY u.nome, u.cognome
        """)
    List<Utente> findFollower(@Param("utenteId") Long utenteId);
}
