import * as React from 'react'; 
import { InstantSearch } from 'react-instantsearch-hooks-web';
import algoliasearch from 'algoliasearch';
import { Box, Paper } from "@mui/material";
import SearchBar from './SearchBar';
import SearchHits from './SearchHits';
import { ALGOLIA_APP_ID, ALGOLIA_API_KEY, ALGOLIA_INDEX_NAME } from '../../../../env-config';

const searchClient = algoliasearch(ALGOLIA_APP_ID, ALGOLIA_API_KEY);

function Search() {
    return (
        <InstantSearch searchClient={searchClient} indexName={ALGOLIA_INDEX_NAME}>
            <Box sx={{ width: '100%', display: 'flex', flexDirection: 'column', gap: 3 }}>
                <SearchBar />
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
                    <SearchHits />
                </Paper>
            </Box>
        </InstantSearch>
    );
}

export default Search;
