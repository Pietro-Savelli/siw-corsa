package it.uniroma3.siw.dto;

import it.uniroma3.siw.model.Scarpa;

public record ScarpaDTO(Long id, String marca, String modello, float kmTotali) {

    public static ScarpaDTO from(Scarpa scarpa) {
        return new ScarpaDTO(
                scarpa.getId(),
                scarpa.getMarca(),
                scarpa.getModello(),
                scarpa.getKmTotali()
        );
    }
}