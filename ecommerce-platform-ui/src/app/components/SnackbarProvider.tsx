import React from 'react';
import { Snackbar, Alert, AlertColor } from '@mui/material';
import { createContext, useContext, useState } from 'react';

interface SnackbarContextType {
    showSnackbar: (message: string, severity: AlertColor) => void;
}

const SnackbarContext = createContext<SnackbarContextType>({
    showSnackbar: () => {},
});

export const useSnackbar = () => useContext(SnackbarContext);

interface SnackbarMessage {
    message: string;
    severity: AlertColor;
    key: number;
}

export const SnackbarProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
    const [snackPack, setSnackPack] = useState<readonly SnackbarMessage[]>([]);
    const [open, setOpen] = useState(false);
    const [messageInfo, setMessageInfo] = useState<SnackbarMessage | undefined>(undefined);

    React.useEffect(() => {
        if (snackPack.length && !messageInfo) {
            // Set a new snack when we don't have an active one
            setMessageInfo({ ...snackPack[0] });
            setSnackPack((prev) => prev.slice(1));
            setOpen(true);
        } else if (snackPack.length && messageInfo && open) {
            // Close an active snack when a new one is added
            setOpen(false);
        }
    }, [snackPack, messageInfo, open]);

    const handleClose = (event: React.SyntheticEvent | Event, reason?: string) => {
        if (reason === 'clickaway') {
            return;
        }
        setOpen(false);
    };

    const handleExited = () => {
        setMessageInfo(undefined);
    };

    const showSnackbar = (message: string, severity: AlertColor) => {
        setSnackPack((prev) => [...prev, { message, severity, key: new Date().getTime() }]);
    };

    return (
        <SnackbarContext.Provider value={{ showSnackbar }}>
            {children}
            <Snackbar
                key={messageInfo ? messageInfo.key : undefined}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                open={open}
                autoHideDuration={3000}
                onClose={handleClose}
                TransitionProps={{ onExited: handleExited }}
            >
                <Alert
                    onClose={handleClose}
                    severity={messageInfo ? messageInfo.severity : 'success'}
                    variant="filled"
                    sx={{
                        minWidth: '250px',
                        boxShadow: 3,
                    }}
                >
                    {messageInfo ? messageInfo.message : ''}
                </Alert>
            </Snackbar>
        </SnackbarContext.Provider>
    );
};