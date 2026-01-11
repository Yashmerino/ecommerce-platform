import { Box, Typography, Pagination, CircularProgress } from '@mui/material';
import React from 'react';
import SearchHit from './SearchHit';
import { useAppSelector } from '../../../hooks';
import { getTranslation } from '../../../../i18n/i18n';

interface SearchHitsProps {
    hits: any[];
    page: number;
    totalPages: number;
    loading: boolean;
    onPageChange: (page: number) => void;
}

function SearchHits({ hits, page, totalPages, loading, onPageChange }: SearchHitsProps) {
    const lang = useAppSelector(state => state.lang.lang);

    return (
        <Box sx={{ width: '100%' }}>
            {loading ? (
                <Box sx={{ 
                    display: 'flex',
                    flexDirection: 'column',
                    alignItems: 'center',
                    justifyContent: 'center',
                    minHeight: '50vh',
                    gap: 2
                }}>
                    <CircularProgress />
                </Box>
            ) : (
                <>
                    <Box sx={{ 
                        display: 'grid',
                        gap: 3,
                        gridTemplateColumns: { xs: '1fr', sm: 'repeat(auto-fill, minmax(300px, 1fr))' },
                        py: 2
                    }}>
                        {hits.length > 0 ? hits.map(hit => (
                            <SearchHit 
                                key={hit.id} 
                                id={hit.id} 
                                name={String(hit.name)} 
                                price={Number(hit.price)} 
                            />
                        )) : (
                            <Box sx={{ 
                                gridColumn: '1/-1',
                                display: 'flex',
                                flexDirection: 'column',
                                alignItems: 'center',
                                justifyContent: 'center',
                                minHeight: '50vh',
                                gap: 2
                            }}>
                                <Typography variant="h5" color="text.secondary" sx={{ fontWeight: 500 }}>
                                    {getTranslation(lang, 'no_products_found')}
                                </Typography>
                            </Box>
                        )}
                    </Box>

                    {totalPages > 1 && (
                        <Box display="flex" justifyContent="center" mt={3}>
                            <Pagination
                                count={totalPages}
                                page={page + 1}
                                onChange={(e, value) => onPageChange(value - 1)}
                                color="primary"
                            />
                        </Box>
                    )}
                </>
            )}
        </Box>
    );
}

export default SearchHits;
