package it.uniroma3.siw.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Squadra {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Il nome della squadra è obbligatorio")
    @Size(min = 3, max = 50, message = "Il nome della squadra deve essere lungo tra {min} e {max} caratteri")
    @Column(unique = true, nullable = false)
    private String nome;

    @Size(max = 100, message = "La città deve essere lunga al massimo {max} caratteri")
    private String citta;

    @OneToMany(mappedBy = "squadra", fetch = FetchType.LAZY)    //metto lezy per consentire la paginazione degli atleti
    private List<Utente> atleti;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCitta() {
        return citta;
    }

    public void setCitta(String citta) {
        this.citta = citta;
    }

    public List<Utente> getAtleti() {
        return atleti;
    }

    public void setAtleti(List<Utente> atleti) {
        this.atleti = atleti;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Squadra other = (Squadra) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
    
}
