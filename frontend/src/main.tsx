import { StrictMode } from "react"
import { createRoot } from "react-dom/client"
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider"
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs"
import "dayjs/locale/it"

import App from "./App" // La bacheca del Profilo
import RegistraAllenamento from "./RegistraAllenamento" // Il nuovo form della Home

// Controlliamo in quale pagina siamo guardando l'ID del div
const rootElement = document.getElementById("root");

if (rootElement) {
    const pagina = rootElement.getAttribute("data-pagina"); // Legge un attributo personalizzato

    createRoot(rootElement).render(
        <StrictMode>
            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="it">
                {pagina === "home" ? <RegistraAllenamento /> : <App />}
            </LocalizationProvider>
        </StrictMode>
    )
}