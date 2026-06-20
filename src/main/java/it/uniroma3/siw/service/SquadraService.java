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

// IMPORT CORRETTO PER LE TRANSAZIONI SPRING
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true) // Di default, tutti i metodi della classe sono in sola lettura
public class SquadraService {

    private final SquadraRepository squadraRepository;
    private final UtenteRepository utenteRepository;
    private static final Logger logger = LoggerFactory.getLogger(SquadraService.class);

    // Constructor Injection: Sicuro e pulito
    public SquadraService(SquadraRepository squadraRepository, UtenteRepository utenteRepository) {
        this.squadraRepository = squadraRepository;
        this.utenteRepository = utenteRepository;
    }

    // =========================================================================
    // METODI DI LETTURA (Ereditano in automatico @Transactional(readOnly = true))
    // =========================================================================

    public List<Squadra> findAll() {
        return squadraRepository.findAll();
    }

    public Optional<Squadra> findById(Long id) {
        return squadraRepository.findById(id);
    }

    // Metodo di comodità che estrae direttamente l'oggetto o lancia un'eccezione.
    // Usato nel Controller per evitare di fare i check Optional.isPresent() ogni volta.
    public Squadra getDettagliSquadra(Long id) {
        return squadraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Squadra con ID " + id + " non trovata"));
    }

    // Metodo per la paginazione: passa il PageRequest al Repository
    public Page<Utente> getAtletiPaginati(Long squadraId, int pagina, int dimensione) {
        logger.debug("Recupero atleti per la squadra {}, pagina {}, dimensione {}", squadraId, pagina, dimensione);
        return utenteRepository.findBySquadraId(squadraId, PageRequest.of(pagina, dimensione));
    }

    // =========================================================================
    // METODI DI SCRITTURA E CANCELLAZIONE (Devono sovrascrivere con @Transactional)
    // =========================================================================

    @Transactional // Abilita la scrittura nel database e il Rollback in caso di errore
    public Squadra save(Squadra squadra) {
        logger.debug("Salvataggio squadra: {}", squadra.getNome());
        return squadraRepository.save(squadra);
    }

    // Metodo alias nel caso preferissi un nome più esplicito rispetto a "save"
    @Transactional
    public Squadra creaNuovaSquadra(Squadra squadra) {
        return save(squadra);
    }

    @Transactional // Anche la cancellazione è una modifica del DB e richiede il Transactional
    public void deleteById(Long id) {
        logger.debug("Cancellazione squadra con ID: {}", id);
        squadraRepository.deleteById(id);
    }

    @Transactional
    public void iscriviUtenteASquadra(Long id, Utente utenteCorrente) {
        Squadra squadra = getDettagliSquadra(id);
        utenteCorrente.setSquadra(squadra);
        utenteRepository.save(utenteCorrente);
        logger.debug("Utente {} iscritto alla squadra {}", utenteCorrente.getId(), squadra.getId());
    }

    @Transactional
    public void rimuoviUtenteDaSquadra(Utente utenteCorrente) {
        utenteCorrente.setSquadra(null);
        utenteRepository.save(utenteCorrente);
        logger.debug("Utente {} rimosso dalla sua squadra", utenteCorrente.getId());
    }


}