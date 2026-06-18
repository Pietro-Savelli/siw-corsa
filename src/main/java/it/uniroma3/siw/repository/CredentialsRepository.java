package it.uniroma3.siw.repository;

import it.uniroma3.siw.model.Credentials;
import org.springframework.data.repository.CrudRepository;

public interface CredentialsRepository extends CrudRepository<Credentials, Long> {

    Credentials findByUsername(String username);
}