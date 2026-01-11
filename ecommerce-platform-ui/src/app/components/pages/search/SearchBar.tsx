import * as React from 'react';
import Paper from '@mui/material/Paper';
import InputBase from '@mui/material/InputBase';
import SearchIcon from '@mui/icons-material/Search';
import { useAppSelector } from '../../../hooks';
import { getTranslation } from '../../../../i18n/i18n';

interface SearchBarProps {
    onSearch: (query: string) => void;
}

function SearchBar({ onSearch }: SearchBarProps) {
    const [query, setQuery] = React.useState('');
    const lang = useAppSelector(state => state.lang.lang);
    const searchTimeoutRef = React.useRef<NodeJS.Timeout>();

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.currentTarget.value;
        setQuery(value);

        if (searchTimeoutRef.current) {
            clearTimeout(searchTimeoutRef.current);
        }

        searchTimeoutRef.current = setTimeout(() => {
            onSearch(value);
        }, 300);
    };

    return (
        <Paper
            component="form"
            elevation={0}
            sx={{ 
                p: '12px 20px',
                display: 'flex', 
                alignItems: 'center', 
                width: '100%',
                borderRadius: 2,
                backgroundColor: 'background.paper',
                border: 1,
                borderColor: 'divider',
                transition: 'all 0.2s ease-in-out',
                '&:hover': {
                    borderColor: 'primary.main',
                },
                '&:focus-within': {
                    borderColor: 'primary.main',
                    boxShadow: '0 0 0 2px rgba(25, 118, 210, 0.1)',
                }
            }}
            onSubmit={(e) => e.preventDefault()}
        >
            <SearchIcon sx={{ color: 'text.secondary', mr: 1 }} />
            <InputBase
                sx={{ 
                    flex: 1,
                    '& input': {
                        py: 1,
                        fontSize: '1.1rem',
                    }
                }}
                placeholder={getTranslation(lang, 'search')}
                inputProps={{ 'aria-label': getTranslation(lang, 'search') }}
                value={query}
                onChange={handleInputChange}
            />
        </Paper>
    );
}

export default SearchBar;
