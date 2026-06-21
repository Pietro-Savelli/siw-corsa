package it.uniroma3.siw.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Accesso negato: non hai i permessi necessari per accedere a questa risorsa.");
    }

}
