import api from "./api"
import type { Scarpa } from "../types"

export async function getScarpe(): Promise<Scarpa[]> {
    try {
        const { data } = await api.get<Scarpa[]>("/scarpe")
        return data
    } catch (error) {
        console.error("Errore nel recupero delle scarpe", error)
        throw new Error("Non è stato possibile caricare le scarpe")
    }
}