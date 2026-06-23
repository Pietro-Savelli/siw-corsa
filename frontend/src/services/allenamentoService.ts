import api from "./api"
import type { Allenamento, AllenamentoRequest } from "../types"

export async function getTipiAllenamento(): Promise<string[]> {
    try {
        const { data } = await api.get<string[]>("/allenamenti/tipi")
        return data
    } catch (error) {
        console.error("Errore nel recupero dei tipi di allenamento", error)
        throw new Error("Non è stato possibile caricare i tipi di allenamento")
    }
}

export async function getMieiAllenamenti(): Promise<Allenamento[]> {
    try {
        const { data } = await api.get<Allenamento[]>("/allenamenti/miei")
        return data
    } catch (error) {
        console.error("Errore nel recupero degli allenamenti", error)
        throw new Error("Non è stato possibile caricare i tuoi allenamenti")
    }
}

export async function creaAllenamento(
    nuovoAllenamento: AllenamentoRequest
): Promise<Allenamento> {
    const { data } = await api.post<Allenamento>("/allenamenti", nuovoAllenamento)
    return data
}