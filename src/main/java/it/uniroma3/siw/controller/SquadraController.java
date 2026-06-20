package it.uniroma3.siw.controller;

import jakarta.validation.Valid;

import org.slf4j.LoggerFactory;

import java.security.Principal;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import it.uniroma3.siw.model.Credentials;
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
        model.addAttribute("squadre", squadraService.findAll());
        return "squadre/list";
    }

    // ---------------------------------------------------------
    // SHOW: Dettaglio della squadra con paginazione atleti
    // ---------------------------------------------------------
    @GetMapping("/squadre/{id}")
    public String show(@PathVariable Long id, 
                       @RequestParam(defaultValue = "0") int page, 
                       Model model) {
        
        Squadra squadra = squadraService.getDettagliSquadra(id);
        Page<Utente> paginaAtleti = squadraService.getAtletiPaginati(id, page, 10);

        model.addAttribute("squadra", squadra);
        model.addAttribute("paginaAtleti", paginaAtleti);
        
        return "squadre/show"; 
    }

    // ---------------------------------------------------------
    // CREATE FORM: Mostra il modulo di inserimento
    // ---------------------------------------------------------
    @GetMapping("/squadre/new") // Oppure "/admin/squadre/new" se vuoi limitarlo
    public String createForm(Model model) {
        model.addAttribute("squadra", new Squadra());
        return "squadre/form"; 
    }

    // ---------------------------------------------------------
    // SAVE: Salva la squadra nel database
    // ---------------------------------------------------------
    @PostMapping("/squadre") // Oppure "/admin/squadre" 
    public String save(@Valid @ModelAttribute("squadra") Squadra squadra,
                       BindingResult bindingResult, 
                       Model model) {

        if (bindingResult.hasErrors()) {
            return "squadre/form";
        }

        try {
            Squadra nuovaSquadra = squadraService.creaNuovaSquadra(squadra);
            logger.debug("Nuova squadra salvata con ID: {}", nuovaSquadra.getId());
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
    @GetMapping("/squadre/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Squadra squadra = squadraService.getDettagliSquadra(id);
        model.addAttribute("squadra", squadra);
        return "squadre/form";
    }

    // ---------------------------------------------------------
    // DELETE: Elimina una squadra
    // ---------------------------------------------------------
    @PostMapping("/squadre/{id}/delete")
    public String delete(@PathVariable Long id) {
        // Supponendo di aggiungere un metodo deleteById nel service
        // squadraService.deleteById(id);
        return "redirect:/squadre";
    }


    @PostMapping("/squadre/{id}/iscriviti")
    public String iscrivitiASquadra(@PathVariable Long id, Principal principal) {
        
        if (principal == null) {
            return "redirect:/login";
        }

        String username = principal.getName();
        Credentials credentials = credentialsService.findByUsername(username);
        Utente utenteCorrente = credentials.getUtente();
        
        // Controlliamo se l'utente ha già una squadra!
        if (utenteCorrente.getSquadra() != null) {
            // Opzione A: Lo blocchiamo e lo rimandiamo alla lista squadre con un errore nell'URL
            return "redirect:/squadre?errore=gia_iscritto";
        }

        try {
            // Proviamo a iscriverlo
            squadraService.iscriviUtenteASquadra(id, utenteCorrente);
        } catch (IllegalArgumentException e) {
            // Se il Service ci dice che la squadra non esiste (es. ID 9999)
            return "redirect:/squadre?errore=squadra_non_trovata";
        }
        
        return "redirect:/profilo";
    }


    @PostMapping("/squadre/abbandona")
    public String abbandonaSquadra(Principal principal) {
        
        // 1. Controllo sicurezza base
        if (principal == null) {
            return "redirect:/login";
        }

        // 2. Recuperiamo l'utente
        String username = principal.getName();
        Credentials credentials = credentialsService.findByUsername(username);
        Utente utenteCorrente = credentials.getUtente();
        
        // 3. Se l'utente ha effettivamente una squadra, lo facciamo uscire
        if (utenteCorrente.getSquadra() != null) {
            squadraService.rimuoviUtenteDaSquadra(utenteCorrente);
        }
        
        // 4. Lo riportiamo al profilo
        return "redirect:/profilo";
    }

}
