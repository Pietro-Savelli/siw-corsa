package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Utente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

//public interface AllenamentoRepository extends CrudRepository<Allenamento, Long>, PagingAndSortingRepository<Allenamento, Long>
public interface AllenamentoRepository extends JpaRepository<Allenamento, Long> {

    List<Allenamento> findByAtletaOrderByDataDesc(Utente atleta);

    // Bacheca Generale
    Page<Allenamento> findAllByOrderByDataDesc(Pageable pageable);

    /*Bacheca dei Seguiti
       Query JPQL custom: prende gli allenamenti il cui atleta è
       - l'utente stesso (:utenteId), oppure
       - uno dei seguiti dell'utente (navigazione tramite la
         relazione ManyToMany "seguiti" di Utente).
       Il "DISTINCT" evita duplicati nel caso in cui i join producano
       righe ripetute. Ordinamento per data decrescente, paginato.*/
    @Query("""
        SELECT DISTINCT a FROM Allenamento a
        WHERE a.atleta.id = :utenteId
           OR a.atleta.id IN (
                SELECT s.id FROM Utente u JOIN u.seguiti s WHERE u.id = :utenteId
           )
        ORDER BY a.data DESC
        """)
    Page<Allenamento> findBachecaSeguiti(@Param("utenteId") Long utenteId, Pageable pageable);

    long countByAtletaId(Long utenteId);

    @Query("SELECT COALESCE(SUM(a.distanzaInKm), 0.0) FROM Allenamento a WHERE a.atleta.id = :atletaId")
    long sumDistanzaInKmByAtletaId(@Param("atletaId") Long atletaId);
}