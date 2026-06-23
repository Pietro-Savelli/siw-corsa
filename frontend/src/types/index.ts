// Replica esattamente i DTO Java del backend (AllenamentoDTO, ScarpaDTO,
// AllenamentoRequest). Se il backend cambia, si aggiorna qui e il
// compilatore segnala tutti i punti rotti.

export interface Scarpa {
    id: number
    marca: string
    modello: string
    kmTotali: number
}

export interface Allenamento {
    id: number
    titolo: string
    data: string // ISO "yyyy-MM-dd"
    distanzaInKm: number
    tipoDiAllenamento: string
    scarpa: Scarpa | null
}

// Corpo della POST /api/allenamenti
export interface AllenamentoRequest {
    titolo: string
    data: string
    distanzaInKm: number
    tipoDiAllenamento: string
    scarpaId: number | null
}