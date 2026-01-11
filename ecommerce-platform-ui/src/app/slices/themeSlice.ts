import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import type { RootState } from '../../store/store'
import { getCookie } from '../utils/Utils';

const initialState = {
    theme: getCookie("ecommerce-platform-light-theme") == "true" || false
}

export const themeSlice = createSlice({
    name: 'theme',
    initialState,
    reducers: {
        updateTheme: (state, action: PayloadAction<boolean>) => {
            state.theme = action.payload;
        }
    }
})

export const { updateTheme } = themeSlice.actions

export const getRoles = (state: RootState) => state.lang;

export default themeSlice.reducer;