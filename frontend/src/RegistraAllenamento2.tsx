import { useState } from "react";
import { Fab } from "@mui/material";
import AddIcon from '@mui/icons-material/Add';
import AllenamentoCreateDialog from "./components/AllenamentoCreateDialog";

export default function RegistraAllenamento() {
    const [open, setOpen] = useState(false);

    return (
        <>
            {/* FAB: Posizionato fisso in basso a destra non mi funziona */}
            <Fab
                color="primary"
                aria-label="add"
                onClick={() => setOpen(true)}
                sx={{
                    position: 'fixed',
                    bottom: 32,
                    right: 32,
                    boxShadow: 3
                }}
            >
                <AddIcon />
            </Fab>

            <AllenamentoCreateDialog
                open={open}
                onClose={() => setOpen(false)}
                onCreated={() => window.location.href = '/profilo'}
            />
        </>
    );
}