package it.uniroma3.siw.controller;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthenticationController {

    @Autowired
    private CredentialsService credentialsService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "authentication/login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new Utente());
        model.addAttribute("credentials", new Credentials());
        return "authentication/register";  // templates/authentication/register.html
    }

    @PostMapping("/register")
    public String registerUtente(
            @Valid @ModelAttribute("user") Utente utente,
            BindingResult userBindingResult,
            @Valid @ModelAttribute("credentials") Credentials credentials,
            BindingResult credentialsBindingResult) {

        if (!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            credentials.setUtente(utente);
            credentialsService.salva(credentials);
            return "redirect:/login";
        }
        return "authentication/register";
    }
}