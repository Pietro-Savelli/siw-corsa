package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class BachecaController {

    @Autowired private AllenamentoService allenamentoService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private UtenteService utenteService;

    @GetMapping("/bacheca/index")
    public String scegliBacheca(Model model) {
        model.addAttribute("userDetails", HomeController.getUserDetails());
        return "bacheca/index";
    }

    // Recupera l'Utente loggato, o null se l'accesso è anonimo
    private Utente getUtenteCorrenteOrNull() {
        UserDetails ud = HomeController.getUserDetails();
        if (ud == null) return null;
        Credentials creds = credentialsService.findByUsername(ud.getUsername());
        return creds.getUtente();
    }

    // Bacheca Generale
    @GetMapping("/bacheca/generale")
    public String bachecaGenerale(@RequestParam(defaultValue = "0") int pagina, Model model) {

        Page<Allenamento> paginaAllenamenti = allenamentoService.getBachecaGenerale(pagina);

        Utente utenteCorrente = getUtenteCorrenteOrNull();
        model.addAttribute("userDetails", HomeController.getUserDetails());

        // Map<Long, Boolean>: per ogni allenamento in pagina, true se l'utente
        // loggato può commentarlo (è il proprietario OPPURE segue l'autore)
        Map<Long, Boolean> permessiCommento = new HashMap<>();

        if (utenteCorrente != null) {
            for (Allenamento a : paginaAllenamenti) {
                boolean isProprietario = a.getAtleta().getId().equals(utenteCorrente.getId());
                boolean loSegue = isProprietario
                        || utenteService.segue(utenteCorrente.getId(), a.getAtleta().getId());
                permessiCommento.put(a.getId(), loSegue);
            }
        }

        model.addAttribute("pagina", paginaAllenamenti);
        model.addAttribute("permessiCommento", permessiCommento);
        return "bacheca/generale";
    }

    // Bacheca dei Seguiti accessibile solo se loggato
    @GetMapping("/bacheca/seguiti")
    public String bachecaSeguiti(@RequestParam(defaultValue = "0") int pagina, Model model) {

        Utente utenteCorrente = getUtenteCorrenteOrNull();
        if (utenteCorrente == null) {
            return "redirect:/login";
        }

        Page<Allenamento> paginaAllenamenti =
                allenamentoService.getBachecaSeguiti(utenteCorrente.getId(), pagina);

        model.addAttribute("userDetails", HomeController.getUserDetails());
        model.addAttribute("pagina", paginaAllenamenti);
        return "bacheca/seguiti";
    }

}