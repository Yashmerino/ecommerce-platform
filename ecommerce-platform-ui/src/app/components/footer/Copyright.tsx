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
import {
    Typography,
    useTheme,
    Box,
    Container,
    Grid,
    Stack,
    IconButton,
    Link,
    Divider
} from '@mui/material';
import LocalMallIcon from '@mui/icons-material/LocalMall';
import EmailIcon from '@mui/icons-material/Email';
import PhoneIcon from '@mui/icons-material/Phone';
import LocationOnIcon from '@mui/icons-material/LocationOn';
import FacebookIcon from '@mui/icons-material/Facebook';
import TwitterIcon from '@mui/icons-material/Twitter';
import InstagramIcon from '@mui/icons-material/Instagram';
import LinkedInIcon from '@mui/icons-material/LinkedIn';
import { getTranslation } from '../../../i18n/i18n';
import { useAppSelector } from '../../hooks';

const Copyright = (props: any) => {
    const lang = useAppSelector(state => state.lang.lang);
    const theme = useTheme();

    return (
            <Box
            component="footer"
            sx={{
                mt: 'auto',
                py: 6,
                bgcolor: theme.palette.mode === "light" ? 'primary.main' : 'background.paper',
                color: theme.palette.mode === "light" ? 'primary.contrastText' : 'text.primary',
                width: '100%',
                position: 'relative',
                zIndex: 1,
                borderTop: 1,
                borderColor: 'divider'
            }}
            data-testid="footer"
        >
            <Container maxWidth="lg">
                <Grid container spacing={4}>
                    {/* Company Info */}
                    <Grid item xs={12} md={4}>
                        <Box sx={{ mb: 4 }}>
                            <Typography
                                variant="h5"
                                sx={{
                                    fontWeight: 700,
                                    mb: 2,
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 1,
                                }}
                            >
                                <LocalMallIcon sx={{ fontSize: 28 }} />
                                {getTranslation(lang, "ecommerce_platform")}
                            </Typography>
                            <Typography
                                variant="body2"
                                sx={{
                                    opacity: 0.8,
                                    maxWidth: 300,
                                }}
                            >
                                Your one-stop shop for all your needs. Quality products, great prices, and excellent service.
                            </Typography>
                        </Box>
                    </Grid>

                    {/* Contact Info */}
                    <Grid item xs={12} md={4}>
                        <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
                            {getTranslation(lang, "contact_us")}
                        </Typography>
                        <Stack spacing={1}>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <EmailIcon fontSize="small" />
                                <Typography variant="body2">example@shop.com</Typography>
                            </Box>
                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                                <PhoneIcon fontSize="small" />
                                <Typography variant="body2">+40 336 772 413</Typography>
                            </Box>
                        </Stack>
                    </Grid>

                    {/* Social Links */}
                    <Grid item xs={12} md={4}>
                        <Typography variant="h6" sx={{ mb: 2, fontWeight: 600 }}>
                            {getTranslation(lang, "follow_us")}
                        </Typography>
                        <Stack direction="row" spacing={2}>
                            <IconButton
                                color="inherit"
                                sx={{
                                    '&:hover': {
                                        bgcolor: 'primary.dark',
                                    },
                                }}
                            >
                                <FacebookIcon />
                            </IconButton>
                            <IconButton
                                color="inherit"
                                sx={{
                                    '&:hover': {
                                        bgcolor: 'primary.dark',
                                    },
                                }}
                            >
                                <TwitterIcon />
                            </IconButton>
                            <IconButton
                                color="inherit"
                                sx={{
                                    '&:hover': {
                                        bgcolor: 'primary.dark',
                                    },
                                }}
                            >
                                <InstagramIcon />
                            </IconButton>
                            <IconButton
                                color="inherit"
                                sx={{
                                    '&:hover': {
                                        bgcolor: 'primary.dark',
                                    },
                                }}
                            >
                                <LinkedInIcon />
                            </IconButton>
                        </Stack>
                    </Grid>
                </Grid>

                <Divider sx={{ my: 4, opacity: 0.2 }} />

                <Box
                    sx={{
                        display: 'flex',
                        justifyContent: 'space-between',
                        alignItems: 'center',
                        flexWrap: 'wrap',
                        gap: 2,
                    }}
                >
                    <Typography variant="body2" sx={{ opacity: 0.8 }}>
                        © {new Date().getFullYear()} {getTranslation(lang, "ecommerce_platform")}. {getTranslation(lang, "all_rights_reserved")}
                    </Typography>
                    <Stack direction="row" spacing={2}>
                        <Link
                            href="#"
                            color="inherit"
                            sx={{
                                opacity: 0.8,
                                textDecoration: 'none',
                                '&:hover': { opacity: 1 },
                            }}
                        >
                            {getTranslation(lang, "privacy_policy")}
                        </Link>
                        <Link
                            href="#"
                            color="inherit"
                            sx={{
                                opacity: 0.8,
                                textDecoration: 'none',
                                '&:hover': { opacity: 1 },
                            }}
                        >
                            {getTranslation(lang, "terms_of_service")}
                        </Link>
                    </Stack>
                </Box>
            </Container>
        </Box>
    );
}

export default Copyright;