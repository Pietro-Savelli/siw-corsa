package it.uniroma3.siw.dto;

import it.uniroma3.siw.model.Allenamento;

import java.time.LocalDate;

public record AllenamentoDTO(
        Long id,
        String titolo,
        LocalDate data,
        float distanzaInKm,
        String tipoDiAllenamento,
        ScarpaDTO scarpa
) {

    public static AllenamentoDTO from(Allenamento allenamento) {
        return new AllenamentoDTO(
                allenamento.getId(),
                allenamento.getTitolo(),
                allenamento.getData(),
                allenamento.getDistanzaInKm(),
                allenamento.getTipoDiAllenamento(),
                allenamento.getScarpa() != null ? ScarpaDTO.from(allenamento.getScarpa()) : null
        );
    }
}