package it.uniroma3.siw.controller;

import it.uniroma3.siw.dto.AllenamentoDTO;
import it.uniroma3.siw.dto.AllenamentoRequest;
import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.service.AllenamentoService;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.UtenteService;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API REST per il frontend React. Parallelo ad AllenamentoController
 * (che resta invariato e continua a servire le view Thymeleaf).
 */
@RestController
@RequestMapping("/api/allenamenti")
public class AllenamentoApiController {

    @Autowired private AllenamentoService allenamentoService;
    @Autowired private CredentialsService credentialsService;
    @Autowired private UtenteService utenteService;

    private static final Logger logger = LoggerFactory.getLogger(AllenamentoApiController.class);

    private static final String[] TIPI = {"Z2", "Z3", "Z4", "Ripetute", "Lungo", "Recupero", "Gara"};

    /* ── Valori possibili per tipoDiAllenamento ──────────────────────── */

    @GetMapping("/tipi")
    public String[] getTipi() {
        return TIPI;
    }

    /* ── UC1: Crea nuovo allenamento ──────────────────────────────────── */

    @PostMapping
    public ResponseEntity<AllenamentoDTO> crea(@Valid @RequestBody AllenamentoRequest request) {
        // forza l'utente 1
        Utente utente = credentialsService.getUtenteCorrente();
//        Utente utente = utenteService.findById(1L)
//                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        Allenamento allenamento = new Allenamento();
        allenamento.setTitolo(request.titolo());
        allenamento.setData(request.data());
        allenamento.setDistanzaInKm(request.distanzaInKm());
        allenamento.setTipoDiAllenamento(request.tipoDiAllenamento());

        Allenamento salvato = allenamentoService.salva(allenamento, utente, request.scarpaId());
        logger.info("L'utente {} {} ha registrato un nuovo allenamento via API: '{}'",
                utente.getNome(), utente.getCognome(), salvato.getTitolo());

        return ResponseEntity.status(HttpStatus.CREATED).body(AllenamentoDTO.from(salvato));
    }

    /* ── Gestione errori di validazione: 400 + mappa campo→messaggio ──── */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errori = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(errore ->
                errori.put(errore.getField(), errore.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errori);
    }

    // allenamenti dell'utente loggato
    @GetMapping("/miei")
    public ResponseEntity<List<AllenamentoDTO>> getMieiAllenamenti() {
        // forza l'utente 1
        Utente utente = credentialsService.getUtenteCorrente();
//        Utente utente = utenteService.findById(1L)
//                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));

        List<Allenamento> allenamenti = allenamentoService.getAllenamentiByAtleta(utente);

        // Convertiamo la lista di Allenamento in una lista di AllenamentoDTO
        List<AllenamentoDTO> dtos = allenamenti.stream()
                .map(AllenamentoDTO::from)
                .toList();

        return ResponseEntity.ok(dtos);
    }
}