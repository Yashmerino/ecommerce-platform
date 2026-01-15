import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import SearchBar from '../app/components/pages/search/SearchBar';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';

const mockStore = configureStore([]);

describe('SearchBar', () => {
    let store: any;
    const mockOnSearch = jest.fn();

    beforeEach(() => {
        store = mockStore({
            lang: { lang: 'en' }
        });
        jest.clearAllMocks();
        jest.useFakeTimers();
    });

    afterEach(() => {
        jest.useRealTimers();
    });

    it('renders search bar correctly', () => {
        render(
            <Provider store={store}>
                <SearchBar onSearch={mockOnSearch} />
            </Provider>
        );

        expect(screen.getByRole('textbox')).toBeInTheDocument();
    });

    it('calls onSearch after debounce delay', async () => {
        render(
            <Provider store={store}>
                <SearchBar onSearch={mockOnSearch} />
            </Provider>
        );

        const input = screen.getByRole('textbox');
        fireEvent.change(input, { target: { value: 'test query' } });

        expect(mockOnSearch).not.toHaveBeenCalled();

        jest.advanceTimersByTime(300);

        await waitFor(() => {
            expect(mockOnSearch).toHaveBeenCalledWith('test query');
        });
    });

    it('cancels previous timeout when typing quickly', async () => {
        render(
            <Provider store={store}>
                <SearchBar onSearch={mockOnSearch} />
            </Provider>
        );

        const input = screen.getByRole('textbox');
        
        fireEvent.change(input, { target: { value: 'test' } });
        jest.advanceTimersByTime(100);
        
        fireEvent.change(input, { target: { value: 'test query' } });
        jest.advanceTimersByTime(300);

        await waitFor(() => {
            expect(mockOnSearch).toHaveBeenCalledTimes(1);
            expect(mockOnSearch).toHaveBeenCalledWith('test query');
        });
    });

    it('updates input value on change', () => {
        render(
            <Provider store={store}>
                <SearchBar onSearch={mockOnSearch} />
            </Provider>
        );

        const input = screen.getByRole('textbox') as HTMLInputElement;
        fireEvent.change(input, { target: { value: 'new search' } });

        expect(input.value).toBe('new search');
    });
});
