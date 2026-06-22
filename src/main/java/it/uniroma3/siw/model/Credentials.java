package it.uniroma3.siw.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
public class Credentials {

    public static final String DEFAULT_ROLE = "DEFAULT";
    public static final String ADMIN_ROLE = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Size(min = 6, message = "La password deve avere almeno 6 caratteri")
    @Column(nullable = false)
    private String password;

    private String role;

    // Credentials possiede la relazione — salva anche Utente in cascata
    @OneToOne(cascade = CascadeType.ALL)
    private Utente utente;

    public Credentials() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Utente getUtente() { return utente; }
    public void setUtente(Utente utente) { this.utente = utente; }
}