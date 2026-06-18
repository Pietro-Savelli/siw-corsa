package it.uniroma3.siw.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class Commento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 500)
    private String testo;

    private LocalDateTime dataOra;

    @ManyToOne
    private Utente autore;

    @ManyToOne
    private Allenamento allenamento;

    // Ricordati di generare Costruttore vuoto, Getter, Setter, equals e hashCode


    public Commento(Allenamento allenamento, Utente autore, LocalDateTime dataOra, String testo) {
        this.allenamento = allenamento;
        this.autore = autore;
        this.dataOra = dataOra;
        this.testo = testo;
    }

    public Commento() {

    }

    public Allenamento getAllenamento() {
        return allenamento;
    }

    public void setAllenamento(Allenamento allenamento) {
        this.allenamento = allenamento;
    }

    public Utente getAutore() {
        return autore;
    }

    public void setAutore(Utente autore) {
        this.autore = autore;
    }

    public LocalDateTime getDataOra() {
        return dataOra;
    }

    public void setDataOra(LocalDateTime dataOra) {
        this.dataOra = dataOra;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Commento commento = (Commento) o;
        return Objects.equals(id, commento.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}