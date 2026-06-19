package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Scarpa;
import it.uniroma3.siw.repository.CommentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentoService {
    private CommentoRepository commentoRepository;
    public CommentoService(CommentoRepository commentoRepository) {
        this.commentoRepository = commentoRepository;
    }

    @Transactional
    public void salva(Commento nuovoCommento) {
        commentoRepository.save(nuovoCommento);
    }

}
