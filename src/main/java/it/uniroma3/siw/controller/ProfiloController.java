package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Scarpa;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ScarpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ProfiloController {

    @Autowired private AllenamentoService allenamentoService;
    @Autowired private ScarpaService scarpaService;
    @Autowired private CredentialsService credentialsService;


    /* ── UC3: Profilo ──────────────────────────────────────────────── */

    @GetMapping("/profilo")
    public String profilo(Model model) {
        UserDetails ud = HomeController.getUserDetails();
        Credentials creds = credentialsService.findByUsername(ud.getUsername());
        Utente utente = creds.getUtente();

        model.addAttribute("userDetails", ud);
        model.addAttribute("utente", utente);
        model.addAttribute("allenamenti", allenamentoService.getAllenamentiByAtleta(utente));
        model.addAttribute("scarpe", scarpaService.findByAtleta(utente));
        return "profili/show";
    }

    /* ── UC2: Form nuova scarpa ────────────────────────────────────── */

    @GetMapping("/scarpe/nuova")
    public String formNuovaScarpa(Model model) {
        model.addAttribute("scarpa", new Scarpa());
        model.addAttribute("userDetails", HomeController.getUserDetails());
        return "scarpe/formScarpa";
    }

    /* ── UC2: Salva scarpa ─────────────────────────────────────────── */

    @PostMapping("/scarpe/nuova")
    public String salvaScarpa(@ModelAttribute Scarpa scarpa) {
        scarpaService.salva(scarpa, credentialsService.getUtenteCorrente());
        return "redirect:/profilo";
    }
}