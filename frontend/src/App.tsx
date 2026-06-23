import { useState, useEffect } from "react"
import {
    Button, Typography, Box, Chip, Card, CardContent, Grid, TextField, InputAdornment
} from "@mui/material"
import DirectionsRunIcon from '@mui/icons-material/DirectionsRun';
import SearchIcon from '@mui/icons-material/Search';
import AllenamentoCreateDialog from "./components/AllenamentoCreateDialog"
import type { Allenamento } from "./types"
import { getMieiAllenamenti } from "./services/allenamentoService"

export default function App() {
    const [dialogAperto, setDialogAperto] = useState(false)
    const [allenamenti, setAllenamenti] = useState<Allenamento[]>([])

    // Stati per i filtri
    const [filtroTipo, setFiltroTipo] = useState<string>("Tutti")
    const [ricerca, setRicerca] = useState<string>("") // NUOVO STATO PER LA RICERCA

    // 1. Scarica gli allenamenti al caricamento della pagina
    useEffect(() => {
        async function caricaDati() {
            try {
                const dati = await getMieiAllenamenti()
                setAllenamenti(dati.reverse())
            } catch (error) {
                console.error(error)
            }
        }
        caricaDati()
    }, [])

    // 2. Aggiunge in cima alla lista il nuovo allenamento
    function handleCreato(nuovo: Allenamento) {
        setAllenamenti((prev) => [nuovo, ...prev])
    }

// 3. Logica di filtraggio COMBINATO (Tipo + Testo/Km)
    const tipiDisponibili = ["Tutti", "Z2", "Z3", "Z4", "Ripetute", "Lungo", "Recupero", "Gara"]

    const allenamentiFiltrati = allenamenti.filter(a => {
        // Controllo 1: Il tipo corrisponde?
        const matchTipo = filtroTipo === "Tutti" || a.tipoDiAllenamento === filtroTipo;

        // Controllo 2: Titolo o Km contengono la stringa cercata?
        const termineCercato = ricerca.toLowerCase();
        const matchTesto = a.titolo.toLowerCase().includes(termineCercato) ||
            a.distanzaInKm.toString().includes(termineCercato);

        return matchTipo && matchTesto;
    })

    return (
        <Box sx={{ maxWidth: 800, margin: 'auto', p: 2 }}>

            {/* Intestazione e Bottone Nuovo */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                <Button
                    variant="contained"
                    onClick={() => setDialogAperto(true)}
                    sx={{
                        backgroundColor: '#f97316', /* Arancione solido infallibile */
                        color: 'white',
                        borderRadius: '9999px',     /* Pillola perfetta */
                        padding: '8px 24px',
                        fontWeight: 'bold',
                        textTransform: 'none',
                        boxShadow: '0 4px 10px rgba(249, 115, 22, 0.3)',
                        transition: 'all 0.2s',
                        '&:hover': {
                            backgroundColor: '#ea580c', /* Arancione più scuro al passaggio del mouse */
                            transform: 'translateY(-2px)',
                            boxShadow: '0 6px 15px rgba(249, 115, 22, 0.5)',
                        }
                    }}
                >
                    + Nuovo allenamento
                </Button>
            </Box>

            {/* BARRA DI RICERCA */}
            <TextField
                fullWidth
                variant="outlined"
                placeholder="Cerca allenamento per titolo o per km (es. 10)..."
                value={ricerca}
                onChange={(e) => setRicerca(e.target.value)}
                sx={{
                    mb: 4,
                    input: { color: 'white' }, // Testo digitato bianco
                    '& .MuiOutlinedInput-root': {
                        backgroundColor: '#1e1e1e', // Sfondo leggermente più scuro del pannello
                        borderRadius: '17px', // Bordi più arrotondati
                        '& fieldset': {
                            borderColor: '#444', // Bordo grigio a riposo
                            transition: 'border-color 0.3s ease',
                        },
                        '&:hover fieldset': {
                            borderColor: '#f97316', // Azzurrino al passaggio del mouse
                        },
                        '&.Mui-focused fieldset': {
                            borderColor: '#f97316', // Azzurrino quando è attivo
                            borderWidth: '2px',
                        },
                    },
                }}
                slotProps={{
                    input: {
                        startAdornment: (
                            <InputAdornment position="start">
                                <SearchIcon sx={{ color: '#888' }} />
                            </InputAdornment>
                        ),
                    }
                }}
            />

            {/* I FILTRI A BOTTONE (Chip) */}
            <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mb: 3 }}>
                {tipiDisponibili.map((tipo) => (
                    <Chip
                        key={tipo}
                        label={tipo}
                        clickable
                        onClick={() => setFiltroTipo(tipo)}
                        sx={{
                            // Se è selezionato usa l'arancione, altrimenti sfondo trasparente
                            backgroundColor: filtroTipo === tipo ? '#f97316' : 'transparent',
                            // Se è selezionato testo bianco, altrimenti grigio chiaro
                            color: filtroTipo === tipo ? 'white' : '#94a3b8',
                            border: '1px solid',
                            // Bordo arancione se selezionato, grigio se inattivo
                            borderColor: filtroTipo === tipo ? '#f97316' : '#f97316',
                            fontWeight: filtroTipo === tipo ? 'bold' : 'normal',
                            transition: 'all 0.2s',
                            '&:hover': {
                                // Hover arancione scuro se già selezionato, altrimenti un grigio leggero
                                backgroundColor: filtroTipo === tipo ? '#ea580c' : 'rgba(255, 255, 255, 0.08)'
                            }
                        }}
                    />
                ))}
            </Box>

            {/* La Griglia degli Allenamenti*/}
            <Grid container spacing={2}>
                {allenamentiFiltrati.length === 0 ? (
                    <Typography variant="body1" sx={{ color: 'gray', ml: 2, mt: 2 }}>
                        Nessun allenamento trovato per questi criteri.
                    </Typography>
                ) : (
                    allenamentiFiltrati.map((a) => (
                        <Grid size={{ xs: 12, sm: 6 }} key={a.id}>
                            <Card sx={{ backgroundColor: '#2c2c2c', color: 'white' }}
                                  onClick={() => window.location.href = `/allenamenti/${a.id}`} >
                                <CardContent>
                                    <Typography variant="h6" component="div">
                                        {a.titolo}
                                    </Typography>
                                    <Typography sx={{ color: '#aaaaaa', mb: 1.5 }}>
                                        {a.data} • {a.tipoDiAllenamento}
                                    </Typography>
                                    <Typography variant="body2" sx={{ display: 'flex', alignItems: 'center' }}>
                                        <DirectionsRunIcon sx={{ fontSize: 18, mr: 0.5, color: '#90caf9' }} />
                                        <strong>{a.distanzaInKm} km</strong>
                                        {a.scarpa && ` con ${a.scarpa.marca} ${a.scarpa.modello}`}
                                    </Typography>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))
                )}
            </Grid>

            {/* Il Modale Nascosto */}
            <AllenamentoCreateDialog
                open={dialogAperto}
                onClose={() => setDialogAperto(false)}
                onCreated={handleCreato}
            />
        </Box>
    )
}