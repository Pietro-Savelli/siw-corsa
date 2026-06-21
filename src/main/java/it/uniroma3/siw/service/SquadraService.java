package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Squadra;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.SquadraRepository;
import it.uniroma3.siw.repository.UtenteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class SquadraService {

    private final SquadraRepository squadraRepository;
    private final UtenteRepository utenteRepository;
    private static final Logger logger = LoggerFactory.getLogger(SquadraService.class);

    public SquadraService(SquadraRepository squadraRepository, UtenteRepository utenteRepository) {
        this.squadraRepository = squadraRepository;
        this.utenteRepository = utenteRepository;
    }

    // =========================================================================
    // METODI DI LETTURA
    // =========================================================================

    public List<Squadra> findAll() {
        return squadraRepository.findAll();
    }

    public Squadra findById(Long id) {
        return squadraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Squadra non trovata con ID: " + id));
    }

    public Page<Utente> getAtletiPaginati(Long squadraId, int pagina, int dimensione) {
        return utenteRepository.findBySquadraId(squadraId, PageRequest.of(pagina, dimensione));
    }

    // =========================================================================
    // METODI DI SCRITTURA E CANCELLAZIONE
    // =========================================================================

    // UNICO METODO DI SALVATAGGIO (puoi usarlo sia per creare che per modificare)
    @Transactional 
    public Squadra save(Squadra squadra) {
        logger.debug("Salvataggio squadra: {}", squadra.getNome());
        return squadraRepository.save(squadra);
    }


    @Transactional
    public void deleteById(Long id) {
        
        logger.debug("Cancellazione squadra con ID: {}", id);

        Squadra squadraDaEliminare = squadraRepository.findById(id).orElse(null);
        
        if (squadraDaEliminare != null) {
            
            //quando cancello una squadra, devo prima mettere nel campo Squadra di ogni atleta che ne faceva parte il valore null
            //altrimenti il database non mi permette di cancellare per il vincolo di integrità referenziale (ogni atleta deve essere associato ad una squadra, ma se la squadra viene cancellata, l'atleta non può più essere associato ad essa)
            for (Utente atleta : squadraDaEliminare.getAtleti()) {
                atleta.setSquadra(null);
                utenteRepository.save(atleta); 
            }
            squadraRepository.delete(squadraDaEliminare);
        }
    }

    @Transactional
    public void iscriviUtenteASquadra(Long id, Utente utenteCorrente) {
        Squadra squadra = this.findById(id); // Usa il metodo appena ripulito
        utenteCorrente.setSquadra(squadra);
        utenteRepository.save(utenteCorrente);
    }

    @Transactional
    public void rimuoviUtenteDaSquadra(Utente utenteCorrente) {
        utenteCorrente.setSquadra(null);
        utenteRepository.save(utenteCorrente);
    }
}