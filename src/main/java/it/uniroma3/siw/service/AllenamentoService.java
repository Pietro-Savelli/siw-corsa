package it.uniroma3.siw.service;

import it.uniroma3.siw.model.Allenamento;
import it.uniroma3.siw.model.Scarpa;
import it.uniroma3.siw.model.Utente;
import it.uniroma3.siw.repository.AllenamentoRepository;
import it.uniroma3.siw.repository.ScarpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AllenamentoService {

    @Autowired
    private AllenamentoRepository allenamentoRepository;

    @Autowired
    private ScarpaRepository scarpaRepository;

    //UC1: Crea Allenamento
    @Transactional
    public Allenamento salva(Allenamento allenamento, Utente atleta, Long scarpaId) {
        allenamento.setAtleta(atleta);

        if (scarpaId != null) {
            Optional<Scarpa> scarpa = scarpaRepository.findById(scarpaId);
            scarpa.ifPresent(s -> {
                allenamento.setScarpa(s);
                // Aggiorna i km totali della scarpa
                s.setKmTotali(s.getKmTotali() + allenamento.getDistanzaInKm());
                scarpaRepository.save(s);
            });
        }

        return allenamentoRepository.save(allenamento);
    }

    // UC3: Profilo con lista allenamenti

    public List<Allenamento> getAllenamentiByAtleta(Utente atleta) {
        return allenamentoRepository.findByAtletaOrderByDataDesc(atleta);
    }

    // UC4: Dettaglio allenamento

    public Optional<Allenamento> findById(Long id) {
        return allenamentoRepository.findById(id);
    }

    // UC5: Modifica allenamento

    @Transactional
    public Allenamento aggiorna(Long id, Allenamento datiNuovi, Long nuovaScarpaId) {
        Allenamento esistente = allenamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));

        // Aggiorna i km della vecchia scarpa (sottrae)
        if (esistente.getScarpa() != null) {
            Scarpa vecchia = esistente.getScarpa();
            vecchia.setKmTotali(vecchia.getKmTotali() - esistente.getDistanzaInKm());
            scarpaRepository.save(vecchia);
        }

        esistente.setTitolo(datiNuovi.getTitolo());
        esistente.setData(datiNuovi.getData());
        esistente.setDistanzaInKm(datiNuovi.getDistanzaInKm());
        esistente.setTipoDiAllenamento(datiNuovi.getTipoDiAllenamento());

        // Aggiorna la nuova scarpa (aggiunge)
        if (nuovaScarpaId != null) {
            scarpaRepository.findById(nuovaScarpaId).ifPresent(s -> {
                esistente.setScarpa(s);
                s.setKmTotali(s.getKmTotali() + datiNuovi.getDistanzaInKm());
                scarpaRepository.save(s);
            });
        } else {
            esistente.setScarpa(null);
        }

        return allenamentoRepository.save(esistente);
    }

    // UC6: Elimina allenamento

    @Transactional
    public void elimina(Long id) {
        Allenamento a = allenamentoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Allenamento non trovato: " + id));

        // Sottrae i km dalla scarpa prima di eliminare
        if (a.getScarpa() != null) {
            Scarpa s = a.getScarpa();
            s.setKmTotali(s.getKmTotali() - a.getDistanzaInKm());
            scarpaRepository.save(s);
        }
        allenamentoRepository.delete(a);
    }
}