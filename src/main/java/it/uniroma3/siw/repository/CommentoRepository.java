package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Commento;
import org.springframework.data.repository.CrudRepository;

public interface CommentoRepository extends CrudRepository<Commento, Long> {
}