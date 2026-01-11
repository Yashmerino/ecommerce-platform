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

import * as React from 'react';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Container from '@mui/material/Container';
import { Alert, Snackbar, Box} from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { InputError } from '../../../utils/InputErrorUtils';
import { getTranslation } from '../../../../i18n/i18n';
import { useAppSelector } from '../../../hooks';

import UserInputFields from './UserInputFields';
import * as AuthRequest from '../../../api/AuthRequest';

const RegisterPage = () => {
    const lang = useAppSelector(state => state.lang.lang);
    const [inputErrors, setInputErrors] = React.useState<InputError[]>([]);
    const [isSuccess, setSuccess] = React.useState(false);
    const [error, setError] = React.useState("");

    const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
        event.preventDefault();

        setSuccess(false);
        setInputErrors([]);
        setError("");

        const data = new FormData(event.currentTarget);
        const username = data.get('username')?.toString();
        const password = data.get('password')?.toString();
        const role = document.getElementById("role") as HTMLInputElement;
        const email = data.get('email')?.toString();

        const response = await AuthRequest.register(role.innerHTML.toUpperCase() ?? "", email ?? "", username ?? "", password ?? "");

        if (response.error) {
            setError(response.error);
        } else if (response.status == 200) {
            setSuccess(true);
        } else {
            response.fieldErrors && setInputErrors(response.fieldErrors);
        }
    };

    const handleAlertClick = () => {
        setSuccess(false);
        setError("");
    };

    return (
        <Box
            sx={{
                minHeight: '100vh',
                display: 'flex',
                flexDirection: 'column',
                bgcolor: 'background.default',
            }}
        >
            {isSuccess && (
                <Snackbar 
                    open={isSuccess} 
                    autoHideDuration={2000} 
                    onClose={handleAlertClick}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                >
                    <Alert data-testid="alert-success" id="alert-success" onClose={handleAlertClick} severity="success" sx={{ width: '100%' }}>
                        {getTranslation(lang, "user_registered_successfully")}
                    </Alert>
                </Snackbar>
            )}
            {error.length > 0 && (
                <Snackbar 
                    open={error.length > 0} 
                    autoHideDuration={2000} 
                    onClose={handleAlertClick}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                >
                    <Alert data-testid="alert-error" id="alert-error" onClose={handleAlertClick} severity="error" sx={{ width: '100%' }}>
                        {getTranslation(lang, error)}
                    </Alert>
                </Snackbar>
            )}
            <Container
                component="main"
                maxWidth="xs"
                sx={{
                    minHeight: '100vh',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center'
                }}
            >
                <Box
                    sx={{
                        width: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center'
                    }}
                >
                    <UserInputFields 
                        title={getTranslation(lang, "sign_up")} 
                        buttonText={getTranslation(lang, "sign_up")} 
                        handleSubmit={handleSubmit} 
                        isEmailAndRoleMandatory={true} 
                        inputErrors={inputErrors} 
                    />
                    <Box
                        sx={{
                            mt: 3,
                            width: '100%',
                            textAlign: 'center'
                        }}
                    >
                        <Link 
                            component={RouterLink}
                            to={'/login'}
                            variant="body1" 
                            id="have-already-account" 
                        >
                            {getTranslation(lang, "have_account_message")}
                        </Link>
                    </Box>
                </Box>
            </Container>
        </Box>
    );
}

export default RegisterPage;