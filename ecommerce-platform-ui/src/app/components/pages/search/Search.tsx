import * as React from 'react'; 
import { Box, Paper } from '@mui/material';
import SearchBar from './SearchBar';
import SearchHits from './SearchHits';
import { searchProducts } from '../../../api/ProductRequest';
import { useAppSelector } from '../../../hooks';

interface SearchState {
    query: string;
    results: any[];
    page: number;
    totalPages: number;
    totalItems: number;
    loading: boolean;
}

function Search() {
    const jwt = useAppSelector(state => state.jwt);
    const [state, setState] = React.useState<SearchState>({
        query: '',
        results: [],
        page: 0,
        totalPages: 0,
        totalItems: 0,
        loading: false,
    });

    const handleSearch = async (query: string, page: number = 0) => {
        if (!query.trim()) {
            setState(prev => ({
                ...prev,
                results: [],
                query: '',
                page: 0,
                totalPages: 0,
                totalItems: 0,
            }));
            return;
        }

        setState(prev => ({ ...prev, loading: true }));
        try {
            const response = await searchProducts(query, page, 10);

            // searchProducts returns parsed JSON on success, Response on failure/401
            if (response && 'ok' in response) {
                if (!response.ok) {
                    setState(prev => ({ ...prev, loading: false }));
                    return;
                }
            }

            if (response && typeof response === 'object') {
                setState(prev => ({
                    ...prev,
                    query,
                    results: (response as any)?.data || [],
                    page: (response as any)?.currentPage ?? 0,
                    totalPages: (response as any)?.totalPages ?? 0,
                    totalItems: (response as any)?.totalItems ?? 0,
                    loading: false,
                }));
                return;
            }

            setState(prev => ({ ...prev, loading: false }));
        } catch (error) {
            console.error('Search error:', error);
            setState(prev => ({ ...prev, loading: false }));
        }
    };

    return (
        <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 3 }}>
            <SearchBar onSearch={handleSearch} />
            <Paper 
                elevation={0}
                sx={{ 
                    width: '100%',
                    borderRadius: 2,
                    backgroundColor: 'background.paper',
                    p: 3,
                    minHeight: '60vh'
                }}
            >
                <SearchHits 
                    hits={state.results} 
                    page={state.page} 
                    totalPages={state.totalPages}
                    loading={state.loading}
                    onPageChange={(newPage) => handleSearch(state.query, newPage)}
                />
            </Paper>
        </Box>
    );
}

export default Search;
