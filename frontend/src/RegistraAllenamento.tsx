import { useState } from "react";
import { Button } from "@mui/material";
import AddIcon from '@mui/icons-material/Add'; // Importa un'icona carina
import AllenamentoCreateDialog from "./components/AllenamentoCreateDialog";

export default function RegistraAllenamento() {
    const [open, setOpen] = useState(false); // Inizia chiuso

    return (
        <>
            <Button
                variant="contained"
                startIcon={<AddIcon />}
                onClick={() => setOpen(true)}
            >
                Registra un nuovo allenamento
            </Button>

            <AllenamentoCreateDialog
                open={open}
                onClose={() => setOpen(false)}
                onCreated={() => window.location.href = '/profilo'}
            />
        </>
    );
}



