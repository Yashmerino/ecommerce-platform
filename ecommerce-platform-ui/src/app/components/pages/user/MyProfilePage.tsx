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

import { Alert, Box, Button, Divider, Paper, Snackbar, TextField } from '@mui/material';
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import * as React from 'react';
import { InputError, isFieldPresentInInputErrors } from '../../../utils/InputErrorUtils';
import InputFields from '../../../utils/InputFields';
import Header from '../../Header';
import Copyright from '../../footer/Copyright';
import { updateInfo } from '../../../slices/infoSlice';
import NoPhoto from "../../../../img/no_photo.jpg";
import AccountCircleIcon from '@mui/icons-material/AccountCircle';

import { useNavigate } from 'react-router-dom';
import { getTranslation } from '../../../../i18n/i18n';
import { getUserPhoto, setUserPhoto, updateUser } from '../../../api/UserRequest';
import { useAppSelector, useAppDispatch } from '../../../hooks';
import * as UserRequest from "../../../api/UserRequest";

const MyProfilePage = () => {
    const lang = useAppSelector(state => state.lang.lang);
    const jwt = useAppSelector(state => state.jwt.token);
    const username = useAppSelector(state => state.username.sub);
    const navigate = useNavigate();
    const dispatch = useAppDispatch();

    const [photo, setPhoto] = React.useState(NoPhoto);
    const [file, setFile] = React.useState<File | null>(null);

    const [email, setEmail] = React.useState(useAppSelector(state => state.info.info.email));

    const [inputErrors, setInputErrors] = React.useState<InputError[]>([]);
    const [isSuccess, setSuccess] = React.useState(false);

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files) {
            setFile(e.target.files[0]);
            setPhoto(URL.createObjectURL(e.target.files[0] as Blob));
        }
    };

    const handleSavePhoto = async () => {
        if (file != null) {
            const response = await setUserPhoto(jwt, username, file);

            if (response.status) {
                if (response.status == 401) {
                    navigate("/login");
                }
            }
        }
    }

    const handleAlertClick = () => {
        setSuccess(false);
    };

    const handleSaveUser = async () => {
        setSuccess(false);
        setInputErrors([]);

        const response = await updateUser(jwt, username, email);

        if (response.status) {
            if (response.status == 401) {
                navigate("/login");
            }
        }

        if (response.fieldErrors) {
            setInputErrors(response.fieldErrors);
        } else {
            setSuccess(true);
            handleSavePhoto();
        }

        dispatch(updateInfo(await UserRequest.getUserInfo(username ?? "")));
    }

    React.useEffect(() => {
        const getUserPhotoRequest = async () => {
            const photoBlob = await getUserPhoto(username);

            if ((photoBlob as Response).status) {
                if ((photoBlob as Response).status == 401) {
                    navigate("/login");
                }
            }

            if ((photoBlob as Blob).size > 0) {
                setPhoto(URL.createObjectURL(photoBlob as Blob));
            }
        }

        getUserPhotoRequest();
    }, []);

    return (
        <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: 'background.default' }}>
            <Header />
            {isSuccess && (
                <Snackbar 
                    open={isSuccess} 
                    autoHideDuration={2000} 
                    onClose={handleAlertClick}
                    anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                >
                    <Alert onClose={handleAlertClick} id="alert-success" severity="success" sx={{ width: '100%' }}>
                        {getTranslation(lang, "user_updated_successfully")}
                    </Alert>
                </Snackbar>
            )}
            <Container maxWidth="lg" sx={{ flex: 1, py: 4 }}>
                <Box sx={{ mb: 4 }}>
                    <Typography 
                        variant="h4" 
                        fontWeight={700} 
                        sx={{ 
                            display: 'flex', 
                            alignItems: 'center',
                            gap: 2,
                            color: 'primary.main' 
                        }}
                    >
                        <AccountCircleIcon fontSize='large' />
                        {getTranslation(lang, "my_profile")}
                    </Typography>
                </Box>
                <Paper 
                    elevation={2} 
                    sx={{ 
                        borderRadius: 2,
                        overflow: 'hidden',
                        bgcolor: 'background.paper',
                        p: 4
                    }}
                >
                    <Box sx={{ 
                        display: 'flex', 
                        flexDirection: { xs: 'column', md: 'row' },
                        gap: 4,
                    }}>
                        {/* Profile Photo Section */}
                        <Box sx={{ 
                            width: { xs: '100%', md: '300px' },
                            alignSelf: 'start'
                        }}>
                            <input
                                accept="image/*"
                                style={{ display: 'none' }}
                                id="photo-upload-button"
                                type="file"
                                onChange={handleFileChange}
                            />
                            <label htmlFor="photo-upload-button">
                                <Button 
                                    component="span" 
                                    sx={{ 
                                        width: '100%',
                                        aspectRatio: '1/1',
                                        p: 0,
                                        overflow: 'hidden',
                                        borderRadius: 2,
                                        boxShadow: 2,
                                        '&:hover': {
                                            boxShadow: 4,
                                            '& img': {
                                                transform: 'scale(1.05)'
                                            }
                                        }
                                    }}
                                >
                                    <img 
                                        width="100%" 
                                        height="100%" 
                                        className="user-image" 
                                        alt={getTranslation(lang, "profile_photo")}
                                        src={photo}
                                        style={{
                                            objectFit: 'cover',
                                            transition: 'transform 0.3s ease-in-out'
                                        }}
                                    />
                                </Button>
                            </label>
                        </Box>

                        {/* Form Section */}
                        <Box sx={{ flex: 1 }}>
                            <Box sx={{ 
                                display: 'flex',
                                flexDirection: 'column',
                                gap: 2
                            }}>
                                <TextField
                                    defaultValue={username}
                                    disabled
                                    required
                                    fullWidth
                                    name="username"
                                    label={getTranslation(lang, "username")}
                                    type="username"
                                    id="username"
                                    data-testid="username"
                                    autoComplete="current-username"
                                />
                                <TextField
                                    defaultValue={email}
                                    required
                                    fullWidth
                                    name="email"
                                    label={getTranslation(lang, "email")}
                                    type="email"
                                    id="email"
                                    data-testid="email"
                                    autoComplete="current-email"
                                    error={isFieldPresentInInputErrors(InputFields.EMAIL, inputErrors)}
                                    helperText={inputErrors.find(error => error.field === InputFields.EMAIL)?.message ? getTranslation(lang, inputErrors.find(error => error.field === InputFields.EMAIL)?.message || '') : undefined}
                                    onChange={(event: React.ChangeEvent<HTMLInputElement>) => {
                                        setEmail(event.target.value);
                                    }}
                                />
                                <TextField
                                    fullWidth
                                    name="address"
                                    label={getTranslation(lang, "address")}
                                    type="address"
                                    id="address"
                                    data-testid="address"
                                    autoComplete="current-address"
                                />
                                <TextField
                                    fullWidth
                                    name="phone"
                                    label={getTranslation(lang, "phone")}
                                    type="phone"
                                    id="phone"
                                    data-testid="phone"
                                    autoComplete="current-phone"
                                />
                            </Box>
                            <Box sx={{ mt: 3, display: 'flex', justifyContent: 'flex-end' }}>
                                <Button 
                                    variant="contained" 
                                    onClick={handleSaveUser}
                                    sx={{
                                        px: 4,
                                        py: 1,
                                        borderRadius: 2,
                                    }}
                                >
                                    {getTranslation(lang, "save")}
                                </Button>
                             </Box>
                            </Box>
                        </Box>
                    </Paper>
                </Container>
            <Copyright />
        </Box>
    );
}

export default MyProfilePage;