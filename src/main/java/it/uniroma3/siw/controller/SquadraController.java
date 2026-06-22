package it.uniroma3.siw.controller;

import jakarta.validation.Valid;

import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Squadra;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.SquadraService;

@Controller
public class SquadraController {

    private final SquadraService squadraService;
    private final CredentialsService credentialsService;
    private static final Logger logger = LoggerFactory.getLogger(SquadraController.class);

    public SquadraController(SquadraService squadraService, CredentialsService credentialsService) {
        this.squadraService = squadraService;
        this.credentialsService = credentialsService;
    }

    // ---------------------------------------------------------
    // LIST: Mostra l'elenco di tutte le squadre
    // ---------------------------------------------------------
    @GetMapping("/squadre")
    public String list(Model model) {
        logger.debug("Visualizzazione lista di tutte le squadre");
        model.addAttribute("squadre", squadraService.findAll());
        model.addAttribute("utenteCorrente", credentialsService.getUtenteCorrente());
        return "squadre/list";
    }

    // ---------------------------------------------------------
    // SHOW: Dettaglio della squadra con paginazione atleti
    // ---------------------------------------------------------
    @GetMapping("/squadre/{id}")
    public String show(@PathVariable Long id, 
                       @RequestParam(defaultValue = "0") int page, 
                       Model model) {
        
        logger.debug("Visualizzazione dettaglio squadra ID: {}", id);
        Squadra squadra = squadraService.findById(id);
        Page<Utente> paginaAtleti = squadraService.getAtletiPaginati(id, page, 10);

        model.addAttribute("squadra", squadra);
        model.addAttribute("paginaAtleti", paginaAtleti);
        model.addAttribute("utenteCorrente", credentialsService.getUtenteCorrente());
        return "squadre/show"; 
    }

    // ---------------------------------------------------------
    // CREATE FORM: Mostra il modulo di inserimento
    // ---------------------------------------------------------
    @GetMapping("/admin/squadre/new") 
    public String createForm(Model model) {
        logger.debug("Admin accede al modulo di creazione nuova squadra");
        model.addAttribute("squadra", new Squadra());
        return "squadre/form"; 
    }

    // ---------------------------------------------------------
    // SAVE: Salva la squadra nel database
    // ---------------------------------------------------------
    @PostMapping("/admin/squadre") 
    public String save(@Valid @ModelAttribute("squadra") Squadra squadra,
                       BindingResult bindingResult, 
                       Model model) {

        if (bindingResult.hasErrors()) {
            logger.warn("Errore di validazione durante il salvataggio della squadra: {}", squadra.getNome());
            return "squadre/form";
        }

        try {
            Squadra nuovaSquadra = squadraService.save(squadra);
            logger.debug("admin ha salvato con successo la Nuova squadra con ID: {}", nuovaSquadra.getId());
            return "redirect:/squadre/" + nuovaSquadra.getId();
        } catch (Exception e) {
            logger.error("Errore nel salvataggio della squadra", e);
            bindingResult.reject("squadra.errore", "Si è verificato un errore durante il salvataggio.");
            return "squadre/form";
        }
    }

    // ---------------------------------------------------------
    // EDIT FORM: Mostra il modulo di modifica per una squadra esistente
    // ---------------------------------------------------------
    @GetMapping("/admin/squadre/{id}/edit") //attenzione
    public String editForm(@PathVariable Long id, Model model) {
        logger.debug("Admin accede al form di modifica per la squadra ID: {}", id);
        Squadra squadra = squadraService.findById(id);
        model.addAttribute("squadra", squadra);
        return "squadre/form";
    }

    // ---------------------------------------------------------
    // DELETE: Elimina una squadra
    // ---------------------------------------------------------
   @PostMapping("/admin/squadre/{id}/delete")
    public String delete(@PathVariable Long id) {
        squadraService.deleteById(id);
        logger.info("Squadra ID: {} eliminata con successo", id);
        return "redirect:/squadre";
    }


    @PostMapping("/squadre/{id}/iscriviti")
    public String iscrivitiASquadra(@PathVariable Long id) {
        
        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        
        if (utenteCorrente == null) {
            return "redirect:/login";
        }
        
        if (utenteCorrente.getSquadra() != null) {
            logger.warn("Utente ID: {} ha tentato di iscriversi a un'altra squadra pur essendone già in una", utenteCorrente.getId());
            return "redirect:/squadre?errore=gia_iscritto";
        }

        try {
            logger.info("L'utente {} {} si è iscritto alla squadra ID: {}", utenteCorrente.getNome(), utenteCorrente.getCognome(), id);
            squadraService.iscriviUtenteASquadra(id, utenteCorrente);
        } catch (IllegalArgumentException e) {
            logger.error("Squadra non trovata durante l'iscrizione");
            return "redirect:/squadre?errore=squadra_non_trovata";
        }
        
        return "redirect:/profilo";
    }


    @PostMapping("/squadre/abbandona")
    public String abbandonaSquadra() { // Niente più Principal qui!
        
        // 1. Recuperiamo l'utente dal Service
        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        
        // 2. Controllo di sicurezza
        if (utenteCorrente == null) {
            return "redirect:/login";
        }
        
        // 3. Se l'utente ha effettivamente una squadra, lo facciamo uscire
        if (utenteCorrente.getSquadra() != null) {
            Long idSquadra = utenteCorrente.getSquadra().getId();
            squadraService.rimuoviUtenteDaSquadra(utenteCorrente);
            logger.info("L'utente {} {} ha abbandonato la squadra ID: {}", utenteCorrente.getNome(), utenteCorrente.getCognome(), idSquadra);
        }
        
        // 4. Lo riportiamo al profilo
        return "redirect:/profilo";
    }
}
