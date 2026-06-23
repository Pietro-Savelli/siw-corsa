import { useState } from "react";
// Rimuoviamo il Button e l'icona di Material UI perché usiamo la tua grafica personalizzata
import AllenamentoCreateDialog from "./components/AllenamentoCreateDialog";

export default function RegistraAllenamento() {
    const [open, setOpen] = useState(false); // Inizia chiuso

    return (
        <>
            {/* Usiamo un <div> (o <a>) con le tue classi CSS.
               Aggiungiamo onClick per aprire il modale React.
               Nota: in React "class" diventa "className"
            */}
            <div
                className="action-card action-card--primary"
                onClick={() => setOpen(true)}
                style={{ cursor: 'pointer' }} // fa comparire la manina quando ci passi sopra
            >
                <div className="action-card__glow"></div>
                <div className="action-card__icon">
                    <i className="fa-solid fa-stopwatch"></i>
                </div>
                <h3 className="action-card__title">Registra un nuovo allenamento</h3>
                <p className="action-card__desc">Avvia una nuova sessione, traccia il percorso e salva i dati.</p>
                <span className="action-card__cta">
                    <span>Inizia ora</span>
                    <i className="fa-solid fa-play"></i>
                </span>
            </div>

            {/* Il modale che si apre quando clicchi la card */}
            <AllenamentoCreateDialog
                open={open}
                onClose={() => setOpen(false)}
                onCreated={() => window.location.href = '/profilo'}
            />
        </>
    );
}