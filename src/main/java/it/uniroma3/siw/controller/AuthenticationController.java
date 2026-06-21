package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    /*logger.info(): Usato per le azioni importanti (es. "Utente registrato", "Allenamento creato", "Ha iniziato a seguire"). Ti fa capire cosa stanno facendo gli utenti.

    logger.debug(): Usato per tracciare la navigazione (es. "L'utente ha aperto la pagina X"). Di default non "sporca" la console, ma è utilissimo se devi fare indagini.

    logger.warn() / logger.error(): Usato quando qualcosa va storto (es. validazione fallita, utente non trovato).*/ 

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/login")
    public String showLoginForm() {
        logger.debug("Accesso alla pagina del login");
        return "authentication/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new Utente());
        model.addAttribute("credentials", new Credentials());
        logger.debug("Accesso alla pagina di registrazione");
        return "authentication/register";  // templates/authentication/register.html
    }

    @PostMapping("/register")
    public String registerUtente(
            @Valid @ModelAttribute("user") Utente utente,
            BindingResult userBindingResult,
            @Valid @ModelAttribute("credentials") Credentials credentials,
            BindingResult credentialsBindingResult) {

        logger.info("Tentativo di registrazione dell'utente: {}", credentials.getUsername());

        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            credentials.setUtente(utente);
            credentialsService.salva(credentials);
            logger.info("Registrazione completata per l'utente: {}", credentials.getUsername());
            return "redirect:/login";
        }

        logger.warn("Errore nella registrazione dell'utente: {}", credentials.getUsername());
        return "authentication/register";
    }
}