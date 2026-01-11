import { Box, Typography, Pagination } from '@mui/material';
import React from 'react';
import { useHits, usePagination } from 'react-instantsearch-hooks-web';
import SearchHit from './SearchHit';
import { useAppSelector } from '../../../hooks';
import { getTranslation } from '../../../../i18n/i18n';

function SearchHits() {
    const { hits } = useHits();
    const { pages, currentRefinement, nbPages, refine } = usePagination();
    const lang = useAppSelector(state => state.lang.lang);

    return (
        <Box sx={{ width: "100%" }}>
            <Box sx={{ 
                display: "grid",
                gap: 3,
                gridTemplateColumns: { xs: '1fr', sm: 'repeat(auto-fill, minmax(300px, 1fr))' },
                py: 2
            }}>
                {hits.length > 0 ? hits.map(hit => (
                    <SearchHit 
                        key={hit.objectID} 
                        objectID={parseInt(hit.objectID)} 
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
                            {getTranslation(lang, "no_products_found")}
                        </Typography>
                    </Box>
                )}
            </Box>

            {nbPages > 1 && (
                <Box display="flex" justifyContent="center" mt={3}>
                    <Pagination
                        count={nbPages}
                        page={currentRefinement + 1}
                        onChange={(e, value) => refine(value - 1)}
                        color="primary"
                    />
                </Box>
            )}
        </Box>
    );
}

export default SearchHits;
