package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ScarpaService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/allenamenti")
public class AllenamentoController {

    @Autowired private AllenamentoService allenamentoService;
    @Autowired private ScarpaService scarpaService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private CommentoService commentoService;

    private static final Logger logger = LoggerFactory.getLogger(AllenamentoController.class);

    private void popolaModel(Model model, Utente utente) {
        model.addAttribute("scarpe", scarpaService.findByAtleta(utente));
        model.addAttribute("tipi", new String[]{"Z2", "Z3", "Z4", "Ripetute", "Lungo", "Recupero", "Gara"});
    }

    /* ── UC1: Form nuovo allenamento ───────────────────────────────── */

    @GetMapping("/nuovo")
    public String formNuovo(Model model) {
        logger.debug("Accesso al form di creazione nuovo allenamento");
        model.addAttribute("allenamento", new Allenamento());
        model.addAttribute("isNuovo", true);
        popolaModel(model, credentialsService.getUtenteCorrente());
        return "allenamenti/formAllenamento";
    }

    /* ── UC1: Salva nuovo allenamento ──────────────────────────────── */

    @PostMapping("/nuovo")
    public String salva(@Valid @ModelAttribute("allenamento") Allenamento allenamento,
                        BindingResult bindingResult,
                        @RequestParam(required = false) Long scarpaId,
                        Model model) { 
        
        if (bindingResult.hasErrors()) {
            logger.warn("Errori di validazione nel salvataggio dell'allenamento");
            model.addAttribute("isNuovo", true);
            popolaModel(model, credentialsService.getUtenteCorrente()); // Ripopola i menu a tendina
            return "allenamenti/formAllenamento"; // Torna al form mostrando gli errori
        }

        Utente utente = credentialsService.getUtenteCorrente();
        allenamentoService.salva(allenamento, utente, scarpaId);
        logger.info("L'utente {} {} ha registrato un nuovo allenamento: '{}'", utente.getNome(), utente.getCognome(), allenamento.getTitolo());
        return "redirect:/profilo";
    }


    /* ── UC4: Dettaglio allenamento ────────────────────────────────── */

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        logger.debug("Accesso ai dettagli dell'allenamento ID: {}", id);
        Allenamento allenamento = allenamentoService.findById(id).orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
        model.addAttribute("allenamento", allenamento);
        model.addAttribute("userDetails", HomeController.getUserDetails());
        return "allenamenti/show";
    }
    
    @GetMapping("/{id}/globale")
    public String dettaglioGlobale(@PathVariable Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
        model.addAttribute("allenamento", allenamento);
        model.addAttribute("userDetails", HomeController.getUserDetails());
        return "allenamenti/showGlobale";
    }

    @GetMapping("/{id}/seguito")
    public String dettaglioSeguiti(@PathVariable Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
        model.addAttribute("allenamento", allenamento);
        model.addAttribute("userDetails", HomeController.getUserDetails());
        return "allenamenti/showSeguito";
    }

    /* ── UC5: Form modifica ────────────────────────────────────────── */

    @GetMapping("/{id}/modifica")
    public String formModifica(@PathVariable Long id, Model model) {
        logger.debug("Accesso al form di modifica per l'allenamento ID: {}", id);
        Allenamento allenamento = allenamentoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
        model.addAttribute("allenamento", allenamento);
        model.addAttribute("isNuovo", false);
        popolaModel(model, credentialsService.getUtenteCorrente());
        return "allenamenti/formAllenamento";
    }

    /* ── UC5: Salva modifica ───────────────────────────────────────── */

    @PostMapping("/{id}/modifica")
    public String aggiorna(@PathVariable Long id,
                           @Valid @ModelAttribute("allenamento") Allenamento datiNuovi,
                           BindingResult bindingResult,
                           @RequestParam(required = false) Long scarpaId,
                           Model model) {
        
    
        if (bindingResult.hasErrors()) {
            model.addAttribute("isNuovo", false);
            popolaModel(model, credentialsService.getUtenteCorrente());
            return "allenamenti/formAllenamento";
        }

        allenamentoService.aggiorna(id, datiNuovi, scarpaId);
        logger.info("Aggiornato allenamento ID: {}", id);
        return "redirect:/allenamenti/" + id;
    }


    /* ── UC6: Elimina ──────────────────────────────────────────────── */

    @PostMapping("/{id}/elimina")
    public String elimina(@PathVariable Long id) {
        allenamentoService.elimina(id);
        logger.info("Eliminato allenamento ID: {}", id);
        return "redirect:/profilo";
    }

    //Salva commento
    @PostMapping("/{id}/commenti")
    public String salvaCommento(@PathVariable("id") Long id, @RequestParam("testo") String testo) {
        Allenamento allenamento = allenamentoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
        Utente autore = credentialsService.getUtenteCorrente();
        Commento nuovoCommento = new Commento();
        nuovoCommento.setTesto(testo);
        nuovoCommento.setAllenamento(allenamento);
        nuovoCommento.setAutore(credentialsService.getUtenteCorrente());
        nuovoCommento.setDataOra(LocalDateTime.now());

        commentoService.salva(nuovoCommento);
        logger.info("L'utente {} {} ha commentato l'allenamento ID: {}", autore.getNome(), autore.getCognome(), id);
       return "redirect:/allenamenti/" + id;
       //return "redirect:/bacheca/seguiti"; oppure
    }
}