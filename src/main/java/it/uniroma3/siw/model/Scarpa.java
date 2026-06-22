package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

@Entity
public class Scarpa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String marca;
    @NotBlank(message = "il modello della scarpa deve essere inserito")
    private String modello;
    private float kmTotali; // Si aggiornerà man mano che si collega con allenamenti
    private float kmMassimi; // Da confrontare cone kmTotali

    // Ogni scarpa appartiene a un solo atleta
    @ManyToOne
    private Utente atleta;

    // Genera qui Costruttore vuoto, Getter, Setter, equals e hashCode


    public Scarpa(String marca, String modello, float kmTotali, float kmMassimi, Utente atleta) {
        this.marca = marca;
        this.modello = modello;
        this.kmTotali = kmTotali;
        this.kmMassimi = kmMassimi;
        this.atleta = atleta;
    }

    public Scarpa() {

    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModello() {
        return modello;
    }

    public void setModello(String modello) {
        this.modello = modello;
    }

    public float getKmTotali() {
        return kmTotali;
    }

    public void setKmTotali(float kmTotali) {
        this.kmTotali = kmTotali;
    }

    public float getKmMassimi() {
        return kmMassimi;
    }

    public void setKmMassimi(float kmMassimi) {
        this.kmMassimi = kmMassimi;
    }

    public Utente getAtleta() {
        return atleta;
    }

    public void setAtleta(Utente atleta) {
        this.atleta = atleta;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Scarpa scarpa = (Scarpa) o;
        return Objects.equals(id, scarpa.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}