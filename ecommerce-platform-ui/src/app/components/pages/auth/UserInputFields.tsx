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
import Avatar from '@mui/material/Avatar';
import Button from '@mui/material/Button';
import CssBaseline from '@mui/material/CssBaseline';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import Paper from '@mui/material/Paper';
import LockOutlinedIcon from '@mui/icons-material/LockOutlined';
import Typography from '@mui/material/Typography';
import FormControl from '@mui/material/FormControl';
import InputLabel from '@mui/material/InputLabel';
import MenuItem from '@mui/material/MenuItem';
import Select, { SelectChangeEvent } from '@mui/material/Select';
import InputFields from '../../../utils/InputFields';
import { InputError, getFieldInputErrorMessage, isFieldPresentInInputErrors } from '../../../utils/InputErrorUtils';
import { useAppSelector } from '../../../hooks';
import { getTranslation } from '../../../../i18n/i18n';

interface UserInputProps {
    title: string,
    buttonText: string,
    handleSubmit: Function,
    isEmailAndRoleMandatory: boolean,
    inputErrors: InputError[]
}

const UserInputFields = ({ title, buttonText, handleSubmit, isEmailAndRoleMandatory, inputErrors }: UserInputProps) => {
    const [role, setRole] = React.useState('USER');
    const lang = useAppSelector(state => state.lang.lang);

    const handleRoleChange = (event: SelectChangeEvent) => {
        setRole(event.target.value);
    };

    return (
        <React.Fragment>
            <CssBaseline />
            <Paper 
                elevation={2} 
                sx={{ 
                    borderRadius: 2,
                    overflow: 'hidden',
                    bgcolor: 'background.paper',
                    p: 4,
                    width: '100%',
                    maxWidth: 400
                }}
            >
                <Box
                    sx={{
                        display: 'flex',
                        flexDirection: 'column',
                        alignItems: 'center',
                    }}
                >
                    <Avatar 
                        sx={{ 
                            width: 56,
                            height: 56,
                            bgcolor: 'primary.main',
                            boxShadow: 2,
                            mb: 2
                        }}
                    >
                        <LockOutlinedIcon sx={{ fontSize: 32 }} />
                    </Avatar>
                    <Typography 
                        component="h1" 
                        variant="h5" 
                        data-testid="title"
                        fontWeight={700}
                        color="primary.main"
                        mb={3}
                    >
                        {title}
                    </Typography>
                    <Box component="form" onSubmit={(e) => handleSubmit(e)} noValidate sx={{ width: '100%' }}>
                    {isEmailAndRoleMandatory &&
                        <>
                            <FormControl fullWidth sx={{ mb: 2 }}>
                                <InputLabel id="roleInput">{getTranslation(lang, "role")}</InputLabel>
                                <Select
                                    labelId="role"
                                    id="role"
                                    data-testid="role"
                                    value={role}
                                    label={getTranslation(lang, "role")}
                                    onChange={handleRoleChange}
                                >
                                    <MenuItem value={"USER"} id="user-role">{getTranslation(lang, "user")}</MenuItem>
                                    <MenuItem value={"SELLER"} id="seller-role">{getTranslation(lang, "seller")}</MenuItem>
                                </Select>
                            </FormControl>
                            <TextField
                                error={isFieldPresentInInputErrors(InputFields.EMAIL, inputErrors)}
                                helperText={isFieldPresentInInputErrors(InputFields.EMAIL, inputErrors) ? getFieldInputErrorMessage(InputFields.EMAIL, inputErrors, lang) : null}
                                required
                                fullWidth
                                name="email"
                                label={getTranslation(lang, "email")}
                                type="email"
                                id="email"
                                data-testid="email"
                                autoComplete="current-email"
                                sx={{ mb: 2 }}
                            />
                        </>}
                    <TextField
                        error={isFieldPresentInInputErrors(InputFields.USERNAME, inputErrors)}
                        helperText={isFieldPresentInInputErrors(InputFields.USERNAME, inputErrors) ? getFieldInputErrorMessage(InputFields.USERNAME, inputErrors, lang) : null}
                        required
                        fullWidth
                        id="username"
                        data-testid="username"
                        label={getTranslation(lang, "username")}
                        name="username"
                        autoComplete="username"
                        autoFocus
                        sx={{ mb: 2 }}
                    />
                    <TextField
                        error={isFieldPresentInInputErrors(InputFields.PASSWORD, inputErrors)}
                        helperText={isFieldPresentInInputErrors(InputFields.PASSWORD, inputErrors) ? getFieldInputErrorMessage(InputFields.PASSWORD, inputErrors, lang) : null}
                        required
                        fullWidth
                        name="password"
                        label={getTranslation(lang, "password")}
                        type="password"
                        id="password"
                        data-testid="password"
                        autoComplete="current-password"
                    />
                    <Button
                        type="submit"
                        data-testid="submit-button"
                        fullWidth
                        variant="contained"
                        id="submit"
                        sx={{ 
                            mt: 3,
                            py: 1.5,
                            borderRadius: 2,
                            fontWeight: 600,
                            fontSize: '1rem'
                        }}
                    >
                        {buttonText}
                    </Button>
                </Box>
            </Box>
            </Paper>
        </React.Fragment>);
}

export default UserInputFields;