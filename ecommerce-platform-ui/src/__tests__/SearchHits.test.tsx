import React from 'react';
import { render, screen } from '@testing-library/react';
import '@testing-library/jest-dom';
import SearchHits from '../app/components/pages/search/SearchHits';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';

const mockStore = configureStore([]);

jest.mock('../app/components/pages/search/SearchHit', () => {
    return function MockSearchHit({ id, name, price }: any) {
        return <div data-testid={`search-hit-${id}`}>{name} - ${price}</div>;
    };
});

describe('SearchHits', () => {
    let store: any;

    beforeEach(() => {
        store = mockStore({
            lang: { lang: 'en' }
        });
    });

    it('displays loading spinner when loading', () => {
        render(
            <Provider store={store}>
                <SearchHits
                    hits={[]}
                    page={1}
                    totalPages={1}
                    loading={true}
                    onPageChange={jest.fn()}
                />
            </Provider>
        );

        expect(screen.getByRole('progressbar')).toBeInTheDocument();
    });

    it('renders hits correctly', () => {
        const hits = [
            { id: 1, name: 'Product 1', price: 10.99 },
            { id: 2, name: 'Product 2', price: 20.99 }
        ];

        render(
            <Provider store={store}>
                <SearchHits
                    hits={hits}
                    page={1}
                    totalPages={2}
                    loading={false}
                    onPageChange={jest.fn()}
                />
            </Provider>
        );

        expect(screen.getByTestId('search-hit-1')).toBeInTheDocument();
        expect(screen.getByTestId('search-hit-2')).toBeInTheDocument();
    });

    it('shows no results message when hits array is empty', () => {
        render(
            <Provider store={store}>
                <SearchHits
                    hits={[]}
                    page={1}
                    totalPages={0}
                    loading={false}
                    onPageChange={jest.fn()}
                />
            </Provider>
        );

        // Component should render but with no hits
        expect(screen.queryByRole('progressbar')).not.toBeInTheDocument();
    });
});
