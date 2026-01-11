import * as React from 'react';
import { Box, Container } from '@mui/material';
import Header from '../../Header';
import Search from './Search';
import Copyright from "../../footer/Copyright";

const SearchPage = () => {
    return (
        <Box sx={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', bgcolor: 'background.default' }}>
            <Header />
            <Container maxWidth="lg" sx={{ flex: 1, py: 4 }}>
                <Search />
            </Container>
            <Copyright />
        </Box>
    );
}

export default SearchPage;