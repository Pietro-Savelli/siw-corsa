import axios from "axios"

// baseURL scritta in un posto solo: tutti i servizi ereditano questa istanza.
// withCredentials è OBBLIGATORIO: l'autenticazione è a sessione (cookie
// JSESSIONID), non a token. Senza questo flag il browser non manda il
// cookie nelle richieste cross-origin verso il backend Spring su :8080
// durante lo sviluppo con Vite su :5173.
const api = axios.create({
    baseURL: "/api",
    withCredentials: true,
})

export default api