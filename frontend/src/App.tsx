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
                <Button variant="contained" color="primary" onClick={() => setDialogAperto(true)}>
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
                            borderColor: '#90caf9', // Azzurrino al passaggio del mouse
                        },
                        '&.Mui-focused fieldset': {
                            borderColor: '#90caf9', // Azzurrino quando è attivo
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
                        color={filtroTipo === tipo ? "primary" : "default"}
                        onClick={() => setFiltroTipo(tipo)}
                        sx={{ color: filtroTipo === tipo ? 'white' : 'gray' }}
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