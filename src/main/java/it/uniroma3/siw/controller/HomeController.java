package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.HomeService;
import it.uniroma3.siw.service.UtenteService;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private HomeService homeService;

    @GetMapping("/")
    public String home(Model model) {
        UserDetails userDetails = getUserDetails();
        model.addAttribute("userDetails", userDetails);

        // Se loggato, passa anche l'Utente per mostrare nome/cognome
        if (userDetails != null) {
            Credentials creds = credentialsService.findByUsername(userDetails.getUsername());
            if (creds != null) {
                model.addAttribute("utente", creds.getUtente());
                model.addAttribute("statistiche", homeService.statistiche(creds.getUtente().getId()));
            }
        }

        return "index";
    }


    public static UserDetails getUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()
                && !(auth.getPrincipal() instanceof String)) {
            return (UserDetails) auth.getPrincipal();
        }
        return null;
    }

}