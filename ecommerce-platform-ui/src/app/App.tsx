/*
 * MIT License
 *
 * Copyright (c) 2023 Artiom Bozieac
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import React from 'react';
import './../main.scss';

import Main from './Main';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { useAppSelector } from './hooks';
import { SnackbarProvider } from './components/SnackbarProvider';

function App() {
    const theme = useAppSelector(state => state.theme.theme);

    const lightTheme = createTheme({
        palette: {
            mode: 'light',
            primary: {
                main: '#1976d2',
                light: '#42a5f5',
                dark: '#1565c0',
            },
            secondary: {
                main: '#9c27b0',
                light: '#ba68c8',
                dark: '#7b1fa2',
            },
            background: {
                default: '#f5f5f5',
                paper: '#ffffff',
            },
        },
        typography: {
            fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
            h5: {
                fontWeight: 600,
            },
        },
        shape: {
            borderRadius: 8,
        },
        components: {
            MuiCard: {
                styleOverrides: {
                    root: {
                        borderRadius: 12,
                        transition: '0.3s',
                        '&:hover': {
                            transform: 'translateY(-4px)',
                            boxShadow: '0 4px 20px 0 rgba(0,0,0,0.12)',
                        },
                    },
                },
            },
            MuiButton: {
                styleOverrides: {
                    root: {
                        borderRadius: 8,
                        textTransform: 'none',
                        fontWeight: 600,
                    },
                },
            },
        },
    });

    const darkTheme = createTheme({
        palette: {
            mode: 'dark',
            primary: {
                main: '#90caf9',
                light: '#e3f2fd',
                dark: '#42a5f5',
            },
            secondary: {
                main: '#ce93d8',
                light: '#f3e5f5',
                dark: '#ab47bc',
            },
            background: {
                default: '#121212',
                paper: '#1e1e1e',
            },
        },
        typography: {
            fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
            h5: {
                fontWeight: 600,
            },
        },
        shape: {
            borderRadius: 8,
        },
        components: {
            MuiCard: {
                styleOverrides: {
                    root: {
                        borderRadius: 12,
                        transition: '0.3s',
                        '&:hover': {
                            transform: 'translateY(-4px)',
                            boxShadow: '0 4px 20px 0 rgba(255,255,255,0.08)',
                        },
                    },
                },
            },
            MuiButton: {
                styleOverrides: {
                    root: {
                        borderRadius: 8,
                        textTransform: 'none',
                        fontWeight: 600,
                    },
                },
            },
        },
    });

    return (
        <ThemeProvider theme={theme ? lightTheme : darkTheme}>
            <CssBaseline />
            <SnackbarProvider>
                <Main />
            </SnackbarProvider>
        </ThemeProvider>
    );
}

export default App;
