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
            const resp = await searchProducts(jwt.token, query, page, 10);

            // searchProducts may return either a parsed JSON (when helper parsed it)
            // or the raw Response object. Normalize both cases into `data`.
            let data: any = null;
            if (!resp) {
                data = null;
            } else if (typeof resp === 'object' && 'data' in resp) {
                // already parsed JSON
                data = resp;
            } else if (typeof resp === 'object' && 'ok' in resp) {
                // raw Response
                if (resp.ok) {
                    data = await resp.json();
                }
            }

            if (data) {
                setState(prev => ({
                    ...prev,
                    query,
                    results: data.data || [],
                    page: data.currentPage || 0,
                    totalPages: data.totalPages || 0,
                    totalItems: data.totalItems || 0,
                    loading: false,
                }));
            } else {
                setState(prev => ({ ...prev, loading: false }));
            }
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
