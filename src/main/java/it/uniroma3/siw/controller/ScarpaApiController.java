package it.uniroma3.siw.controller;

import it.uniroma3.siw.dto.ScarpaDTO;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.ScarpaService;

import it.uniroma3.siw.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scarpe")
public class ScarpaApiController {

    @Autowired private ScarpaService scarpaService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private UtenteService utenteService;

    @GetMapping
    public List<ScarpaDTO> getScarpe() {
        // forza l'utente 1
        Utente utente = credentialsService.getUtenteCorrente();
//        Utente utente = utenteService.findById(1L)
//                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        return scarpaService.findByAtleta(utente)
                .stream()
                .map(ScarpaDTO::from)
                .toList();
    }
}