package it.uniroma3.siw.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AllenamentoRequest(
        @NotBlank(message = "il titolo non può essere vuoto")
        String titolo,

        LocalDate data,

        @NotNull(message = "devi inserire la distanza")
        @Min(value = 0, message = "la distanza non può essere negativa")
        Float distanzaInKm,

        String tipoDiAllenamento,

        Long scarpaId
) {
}