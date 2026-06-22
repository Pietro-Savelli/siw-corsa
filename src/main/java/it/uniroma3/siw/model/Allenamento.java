package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
public class Allenamento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "il titolo non può essere vuoto")
    private String titolo; // es. "Ripetute sui 10km" o "Lungo z2"
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate data;
    @NotNull(message = "devi inserire la distanza")
    @Min(value = 0, message = "la distanza non può essere negativa")
    private float distanzaInKm;
    private String tipoDiAllenamento;

    @ManyToOne
    private Utente atleta;

    @ManyToOne
    private Scarpa scarpa;

    @OneToMany(mappedBy = "allenamento", cascade = CascadeType.ALL)
    private List<Commento> commenti;

    public Allenamento(String titolo, LocalDate data, float distanzaInKm, String tipoDiAllenamento, Utente atleta, Scarpa scarpa, List<Commento> commenti) {
        this.titolo = titolo;
        this.data = data;
        this.distanzaInKm = distanzaInKm;
        this.tipoDiAllenamento = tipoDiAllenamento;
        this.atleta = atleta;
        this.scarpa = scarpa;
        this.commenti = commenti;
    }

    public Allenamento() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Scarpa getScarpa() {
        return scarpa;
    }

    public void setScarpa(Scarpa scarpa) {
        this.scarpa = scarpa;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public float getDistanzaInKm() {
        return distanzaInKm;
    }

    public void setDistanzaInKm(float distanzaInKm) {
        this.distanzaInKm = distanzaInKm;
    }

    public String getTipoDiAllenamento() {
        return tipoDiAllenamento;
    }

    public void setTipoDiAllenamento(String tipoDiAllenamento) {
        this.tipoDiAllenamento = tipoDiAllenamento;
    }

    public Utente getAtleta() {
        return atleta;
    }

    public void setAtleta(Utente atleta) {
        this.atleta = atleta;
    }

    public List<Commento> getCommenti() {
        return commenti;
    }

    public void setCommenti(List<Commento> commenti) {
        this.commenti = commenti;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Allenamento that = (Allenamento) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}