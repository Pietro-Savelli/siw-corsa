package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Utente;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AllenamentoRepository extends CrudRepository<Allenamento, Long> {

    // Tutti gli allenamenti di un utente, dal più recente
    List<Allenamento> findByAtletaOrderByDataDesc(Utente atleta);
}