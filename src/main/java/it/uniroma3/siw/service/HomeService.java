package it.uniroma3.siw.service;

import it.uniroma3.siw.dto.StatisticheHome;
import it.uniroma3.siw.repository.AllenamentoRepository;
import it.uniroma3.siw.repository.ScarpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    @Autowired
    private AllenamentoRepository allenamentoRepository;

    @Autowired
    private ScarpaRepository scarpaRepository;

    public StatisticheHome statistiche(Long utenteId) {
        long km = allenamentoRepository.sumDistanzaInKmByAtletaId(utenteId);
        long allenamenti = allenamentoRepository.countByAtletaId(utenteId);
        long scarpe = scarpaRepository.countByAtletaId(utenteId);

        return new StatisticheHome(km, allenamenti, scarpe);
    }
}