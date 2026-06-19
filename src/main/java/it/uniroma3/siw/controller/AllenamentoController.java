package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Commento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CommentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ScarpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/allenamenti")
public class AllenamentoController {

    @Autowired private AllenamentoService allenamentoService;
    @Autowired private ScarpaService scarpaService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private CommentoService commentoService;

    private Utente getUtenteCorrente() {
        UserDetails ud = HomeController.getUserDetails();
        Credentials creds = credentialsService.findByUsername(ud.getUsername());
        return creds.getUtente();
    }

    private void popolaModel(Model model, Utente utente) {
        model.addAttribute("scarpe", scarpaService.findByAtleta(utente));
        model.addAttribute("tipi", new String[]{"Z2", "Z3", "Z4", "Ripetute", "Lungo", "Recupero", "Gara"});
    }

    /* ── UC1: Form nuovo allenamento ───────────────────────────────── */

    @GetMapping("/nuovo")
    public String formNuovo(Model model) {
        model.addAttribute("allenamento", new Allenamento());
        model.addAttribute("isNuovo", true);
        popolaModel(model, getUtenteCorrente());
        return "allenamenti/formAllenamento";
    }

    /* ── UC1: Salva nuovo allenamento ──────────────────────────────── */

    @PostMapping("/nuovo")
    public String salva(@ModelAttribute Allenamento allenamento,
                        @RequestParam(required = false) Long scarpaId) {
        allenamentoService.salva(allenamento, getUtenteCorrente(), scarpaId);
        return "redirect:/profilo";
    }

    /* ── UC4: Dettaglio allenamento ────────────────────────────────── */

    @GetMapping("/{id}")
    public String dettaglio(@PathVariable Long id, Model model) {
        Allenamento allenamento = allenamentoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
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
        Allenamento allenamento = allenamentoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
        model.addAttribute("allenamento", allenamento);
        model.addAttribute("isNuovo", false);
        popolaModel(model, getUtenteCorrente());
        return "allenamenti/formAllenamento";
    }

    /* ── UC5: Salva modifica ───────────────────────────────────────── */

    @PostMapping("/{id}/modifica")
    public String aggiorna(@PathVariable Long id,
                           @ModelAttribute Allenamento datiNuovi,
                           @RequestParam(required = false) Long scarpaId) {
        allenamentoService.aggiorna(id, datiNuovi, scarpaId);
        return "redirect:/allenamenti/" + id;
    }

    /* ── UC6: Elimina ──────────────────────────────────────────────── */

    @PostMapping("/{id}/elimina")
    public String elimina(@PathVariable Long id) {
        allenamentoService.elimina(id);
        return "redirect:/profilo";
    }

    //Salva commento
    @PostMapping("/{id}/commenti")
    public String salvaCommento(@PathVariable("id") Long id, @RequestParam("testo") String testo) {
        Allenamento allenamento = allenamentoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));
        Commento nuovoCommento = new Commento();
        nuovoCommento.setTesto(testo);
        nuovoCommento.setAllenamento(allenamento);
        nuovoCommento.setAutore(getUtenteCorrente());
        nuovoCommento.setDataOra(LocalDateTime.now());

        commentoService.salva(nuovoCommento);
       return "redirect:/allenamenti/" + id;
       //return "redirect:/bacheca/seguiti"; oppure
    }
}