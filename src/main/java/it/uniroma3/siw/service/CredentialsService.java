package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CredentialsService {

    @Autowired private CredentialsRepository credentialsRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public Credentials salva(Credentials credentials) {
        credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(passwordEncoder.encode(credentials.getPassword()));
        return credentialsRepository.save(credentials);
    }

    public Credentials findByUsername(String username) {
        return credentialsRepository.findByUsername(username);
    }
}
