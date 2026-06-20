package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    private String nome;

    @NotBlank
    private String cognome;

    @NotBlank
    @Email
    private String email;

    // Un utente può avere molte scarpe nel suo parco scarpe
    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL)
    private List<Scarpa> scarpe;

    // Un utente ha molti allenamenti
    @OneToMany(mappedBy = "atleta", cascade = CascadeType.ALL)
    private List<Allenamento> allenamenti;

    @OneToMany(mappedBy = "autore", cascade = CascadeType.ALL)
    private List<Commento> commenti;

    @ManyToOne
    private Squadra squadra;

    @ManyToMany
    @JoinTable(
            name = "follower",
            joinColumns = @JoinColumn(name = "utente_id"),           // io
            inverseJoinColumns = @JoinColumn(name = "seguito_id")    // chi seguo
    )
    private Set<Utente> seguiti = new HashSet<>();

    public Utente(List<Allenamento> allenamenti, List<Scarpa> scarpe, String email, String cognome, String nome, List<Commento> commenti) {
        this.allenamenti = allenamenti;
        this.scarpe = scarpe;
        this.email = email;
        this.cognome = cognome;
        this.nome = nome;
        this.commenti = commenti;
    }

    public Utente() {

    }

    public List<Allenamento> getAllenamenti() {
        return allenamenti;
    }

    public void setAllenamenti(List<Allenamento> allenamenti) {
        this.allenamenti = allenamenti;
    }

    public List<Scarpa> getScarpe() {
        return scarpe;
    }

    public void setScarpe(List<Scarpa> scarpe) {
        this.scarpe = scarpe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Commento> getCommenti() {
        return commenti;
    }

    public void setCommenti(List<Commento> commenti) {
        this.commenti = commenti;
    }

    public Set<Utente> getSeguiti() {
        return seguiti;
    }

    public void setSeguiti(Set<Utente> seguiti) {
        this.seguiti = seguiti;
    }

    public Squadra getSquadra() {
        return squadra;
    }

    public void setSquadra(Squadra squadra) {
        this.squadra = squadra;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Utente utente = (Utente) o;
        return Objects.equals(id, utente.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}