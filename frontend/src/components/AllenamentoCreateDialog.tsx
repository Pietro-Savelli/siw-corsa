import { useState, useEffect } from "react"
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
    Alert,
    FormControl,
    FormLabel,
    RadioGroup,
    FormControlLabel,
    Radio,
    Select,
    MenuItem,
    InputLabel,
    CircularProgress,
} from "@mui/material"
import { DatePicker } from "@mui/x-date-pickers/DatePicker"
import dayjs, { Dayjs } from "dayjs"

import type { Allenamento, Scarpa } from "../types"
import { getTipiAllenamento, creaAllenamento } from "../services/allenamentoService"
import { getScarpe } from "../services/scarpaService"
import axios from "axios"

interface AllenamentoCreateDialogProps {
    open: boolean
    onClose: () => void
    onCreated: (allenamento: Allenamento) => void
}

// Stato del form prima della validazione: i campi numerici/data sono
// stringhe o null perché provengono da input controllati, non ancora
// convertiti nel tipo che il backend si aspetta.
interface FormState {
    titolo: string
    data: Dayjs | null
    distanzaInKm: string
    tipoDiAllenamento: string
    scarpaId: number | ""
}

const FORM_INIZIALE: FormState = {
    titolo: "",
    data: dayjs(),
    distanzaInKm: "",
    tipoDiAllenamento: "",
    scarpaId: "",
}

export default function AllenamentoCreateDialog({
                                                    open,
                                                    onClose,
                                                    onCreated,
                                                }: AllenamentoCreateDialogProps) {
    const [form, setForm] = useState<FormState>(FORM_INIZIALE)
    const [tipi, setTipi] = useState<string[]>([])
    const [scarpe, setScarpe] = useState<Scarpa[]>([])

    // Errori di validazione, chiave = nome campo (coerente con la mappa
    // restituita da handleValidation nel backend), più un errore generico
    // per fallimenti non di validazione (es. rete, 500).
    const [erroriCampo, setErroriCampo] = useState<Record<string, string>>({})
    const [erroreGenerico, setErroreGenerico] = useState<string | null>(null)

    const [inviando, setInviando] = useState(false)

    // Caricati una volta sola al mount, non ad ogni apertura del dialog —
    // stesso pattern di MovieCreateDialog con gli artisti.
    useEffect(() => {
        async function caricaDatiSupporto() {
            try {
                const [tipiCaricati, scarpeCaricate] = await Promise.all([
                    getTipiAllenamento(),
                    getScarpe(),
                ])
                setTipi(tipiCaricati)
                setScarpe(scarpeCaricate)
            } catch {
                setErroreGenerico("Non è stato possibile caricare i dati del form")
            }
        }
        caricaDatiSupporto()
    }, [])

    function resetStato() {
        setForm(FORM_INIZIALE)
        setErroriCampo({})
        setErroreGenerico(null)
    }

    function handleClose() {
        if (inviando) return // non si chiude durante una richiesta in corso
        resetStato()
        onClose()
    }

    // Validazione solo al submit — prima non si mostra nessun errore,
    // coerente con la regola "no errori prima del primo tentativo".
    function valida(): Record<string, string> {
        const errori: Record<string, string> = {}

        if (!form.titolo.trim()) {
            errori.titolo = "il titolo non può essere vuoto"
        }

        const distanza = Number(form.distanzaInKm)
        if (form.distanzaInKm.trim() === "" || Number.isNaN(distanza)) {
            errori.distanzaInKm = "devi inserire la distanza"
        } else if (distanza < 0) {
            errori.distanzaInKm = "la distanza non può essere negativa"
        }

        return errori
    }

    async function handleSubmit() {
        const erroriValidazione = valida()
        if (Object.keys(erroriValidazione).length > 0) {
            setErroriCampo(erroriValidazione)
            return
        }

        setErroriCampo({})
        setErroreGenerico(null)
        setInviando(true)

        try {
            const creato = await creaAllenamento({
                titolo: form.titolo.trim(),
                data: (form.data ?? dayjs()).format("YYYY-MM-DD"),
                distanzaInKm: Number(form.distanzaInKm),
                tipoDiAllenamento: form.tipoDiAllenamento,
                scarpaId: form.scarpaId === "" ? null : form.scarpaId,
            })
            onCreated(creato)
            resetStato()
            onClose()
        } catch (error) {
            // 400 dal backend → mappa campo/messaggio dal nostro
            // @ExceptionHandler(MethodArgumentNotValidException.class)
            if (axios.isAxiosError(error) && error.response?.status === 400) {
                setErroriCampo(error.response.data as Record<string, string>)
            } else {
                setErroreGenerico("Si è verificato un errore durante il salvataggio. Riprova.")
            }
        } finally {
            setInviando(false)
        }
    }

    return (
        <Dialog
            open={open}
            onClose={handleClose}
            fullWidth
            maxWidth="sm"
            slotProps={{
                paper: {
                    sx: {
                        backgroundColor: '#1e293b',
                        color: 'white',
                        border: '1px solid #334155'
                    }
                }
            }}
        >
            <DialogTitle>Registra nuovo allenamento</DialogTitle>

            <DialogContent sx={{ display: "flex", flexDirection: "column", gap: 2, pt: 1 }}>
                {erroreGenerico && <Alert severity="error">{erroreGenerico}</Alert>}

                <TextField
                    label="Titolo"
                    placeholder="es. Ripetute 10km"
                    value={form.titolo}
                    onChange={(e) => setForm((prev) => ({ ...prev, titolo: e.target.value }))}
                    error={!!erroriCampo.titolo}
                    helperText={erroriCampo.titolo}
                    disabled={inviando}
                    fullWidth
                    sx={{
                        '& .MuiInputLabel-root': { color: '#94a3b8' },
                        '& .MuiInputLabel-root.Mui-focused': { color: '#f97316' },
                        '& .MuiOutlinedInput-root': {
                            color: 'white',
                            '& fieldset': { borderColor: '#94a3b8' },
                            '&:hover fieldset': { borderColor: '#f97316' },
                            '&.Mui-focused fieldset': { borderColor: '#f97316' },
                        }
                    }}
                />

                <DatePicker
                    label="Data"
                    value={form.data}
                    onChange={(nuovaData) => setForm((prev) => ({ ...prev, data: nuovaData }))}
                    disabled={inviando}
                    slotProps={{
                        textField: {
                            fullWidth: true,
                            error: !!erroriCampo.data,
                            helperText: erroriCampo.data,
                            sx: {
                                '& .MuiInputLabel-root': { color: '#94a3b8' },
                                '& .MuiInputLabel-root.Mui-focused': { color: '#f97316' },
                                '& .MuiOutlinedInput-root': {
                                    color: 'white',
                                    '& fieldset': { borderColor: '#334155' },
                                    '&:hover fieldset': { borderColor: '#f97316' },
                                    '&.Mui-focused fieldset': { borderColor: '#f97316' },
                                },
                                '& .MuiSvgIcon-root': { color: '#94a3b8' }, // L'icona del calendario
                            }
                        }
                    }}
                />

                <TextField
                    label="Distanza (km)"
                    type="number"
                    value={form.distanzaInKm}
                    onChange={(e) => setForm((prev) => ({ ...prev, distanzaInKm: e.target.value }))}
                    error={!!erroriCampo.distanzaInKm}
                    helperText={erroriCampo.distanzaInKm}
                    disabled={inviando}
                    slotProps={{ htmlInput: { step: 0.1, min: 0 } }}
                    fullWidth
                    sx={{
                        '& .MuiInputLabel-root': { color: '#94a3b8' },
                        '& .MuiInputLabel-root.Mui-focused': { color: '#f97316' },
                        '& .MuiOutlinedInput-root': {
                            color: 'white',
                            '& fieldset': { borderColor: '#94a3b8' },
                            '&:hover fieldset': { borderColor: '#f97316' },
                            '&.Mui-focused fieldset': { borderColor: '#f97316' },
                        }
                    }}
                />

                <FormControl disabled={inviando}>
                    <FormLabel>Tipo di allenamento</FormLabel>
                    <RadioGroup
                        row
                        value={form.tipoDiAllenamento}
                        onChange={(e) =>
                            setForm((prev) => ({ ...prev, tipoDiAllenamento: e.target.value }))
                        }
                    >
                        {tipi.map((tipo) => (
                            <FormControlLabel key={tipo} value={tipo} control={<Radio sx={{ color: '#94a3b8', '&.Mui-checked': { color: '#f97316' } }} />}
                                              label={tipo} />
                        ))}
                    </RadioGroup>
                </FormControl>

                <FormControl fullWidth disabled={inviando} sx={{
                    '& .MuiInputLabel-root.Mui-focused': { color: '#f97316' },
                    '& .MuiOutlinedInput-root': {
                        '&:hover fieldset': { borderColor: '#f97316' },
                        '&.Mui-focused fieldset': { borderColor: '#f97316' },
                    }
                }}>
                    <InputLabel id="scarpa-label">Scarpa utilizzata (opzionale)</InputLabel>
                    <Select
                        labelId="scarpa-label"
                        label="Scarpa utilizzata (opzionale)"
                        value={form.scarpaId}
                        onChange={(e) => {
                            const valore = e.target.value;
                            setForm((prev) => ({
                                ...prev,
                                scarpaId: (valore as string | number) === "" ? "" : Number(valore),
                            }));
                        }}
                    >
                        <MenuItem value="">— Nessuna scarpa —</MenuItem>
                        {scarpe.map((s) => (
                            <MenuItem key={s.id} value={s.id}>
                                {s.marca} {s.modello} ({s.kmTotali} km)
                            </MenuItem>
                        ))}
                    </Select>
                </FormControl>
            </DialogContent>

            <DialogActions>
                <Button onClick={handleClose} disabled={inviando} sx={{ color: '#94a3b8', '&:hover': { color: '#f97316' } }}>
                    Annulla
                </Button>
                <Button onClick={handleSubmit} variant="contained" disabled={inviando} sx={{
                    backgroundColor: '#f97316',
                    color: 'white',
                    '&:hover': { backgroundColor: '#ea580c' }
                }}>
                    {inviando ? <CircularProgress size={20} /> : "Salva allenamento"}
                </Button>
            </DialogActions>
        </Dialog>
    )
}