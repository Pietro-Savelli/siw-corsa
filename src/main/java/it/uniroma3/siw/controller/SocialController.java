package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ScarpaService;
import it.uniroma3.siw.service.UtenteService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class SocialController {

    @Autowired private UtenteService utenteService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private AllenamentoService allenamentoService;
    @Autowired private ScarpaService scarpaService;

    private static final Logger logger = LoggerFactory.getLogger(SocialController.class);


    @GetMapping("/utenti/{id}")
    public String visualizzaUtente(@PathVariable Long id, Model model) {
        logger.debug("Visualizzazione profilo utente ID: {}", id);
        Utente utenteVisitato = utenteService.findById(id).orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + id));

        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        model.addAttribute("userDetails", HomeController.getUserDetails());
        model.addAttribute("utenteVisitato", utenteVisitato);

        boolean isMioProfilo = utenteCorrente != null && utenteCorrente.getId().equals(id);
        boolean loSeguo = !isMioProfilo && utenteCorrente != null
                && utenteService.segue(utenteCorrente.getId(), id);

        model.addAttribute("isMioProfilo", isMioProfilo);
        model.addAttribute("loSeguo", loSeguo);
        model.addAttribute("numSeguiti", utenteService.getSeguiti(id).size());
        model.addAttribute("numFollower", utenteService.getFollower(id).size());
        model.addAttribute("allenamenti", allenamentoService.getAllenamentiByAtleta(utenteVisitato));

        return "utenti/show";
    }

    // Lista di chi seguo
    @GetMapping("/profilo/seguiti")
    public String listaSeguiti(Model model) {
        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        if (utenteCorrente == null) {
            return "redirect:/login";
        }
        List<Utente> seguiti = utenteService.getSeguiti(utenteCorrente.getId());

        model.addAttribute("userDetails", HomeController.getUserDetails());
        model.addAttribute("utenti", seguiti);
        model.addAttribute("titoloLista", "Persone che segui");
        return "utenti/seguiti";
    }

    // Lista di chi mi segue
    @GetMapping("/profilo/follower")
    public String listaFollower(Model model) {
        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        if (utenteCorrente == null) {
            return "redirect:/login";
        }
        List<Utente> follower = utenteService.getFollower(utenteCorrente.getId());

        model.addAttribute("userDetails", HomeController.getUserDetails());
        model.addAttribute("utenti", follower);
        model.addAttribute("titoloLista", "Persone che ti seguono");
        return "utenti/follower";
    }

    /* ── Elenco di tutti gli utenti registrati, per trovare chi seguire ── */
    @GetMapping("/utenti")
    public String elencoUtenti(Model model) {
        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        model.addAttribute("userDetails", HomeController.getUserDetails());
        model.addAttribute("utenti", utenteService.findAll());
        model.addAttribute("utenteCorrente", utenteCorrente);
        return "utenti/list";
    }

    // Azioni Follow / Unfollow
    @PostMapping("/utenti/{id}/segui")
    public String segui(@PathVariable Long id,
                        @RequestParam(defaultValue = "/utenti") String redirectTo) {
        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        if (utenteCorrente != null) {
            utenteService.segui(utenteCorrente.getId(), id);
            logger.info("L'utente {} {} ha iniziato a seguire l'utente ID: {}", utenteCorrente.getNome(), utenteCorrente.getCognome(), id);
        }
        return "redirect:" + redirectTo;
    }

    @PostMapping("/utenti/{id}/smetti-di-seguire")
    public String smettiDiSeguire(@PathVariable Long id,
                                  @RequestParam(defaultValue = "/utenti") String redirectTo) {
        Utente utenteCorrente = credentialsService.getUtenteCorrente();
        if (utenteCorrente != null) {
            utenteService.smettiDiSeguire(utenteCorrente.getId(), id);
            logger.info("L'utente {} {} ha smesso di seguire l'utente ID: {}", utenteCorrente.getNome(), utenteCorrente.getCognome(), id);
        }
        return "redirect:" + redirectTo;
    }
}