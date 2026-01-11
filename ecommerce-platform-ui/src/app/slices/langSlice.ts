import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import type { RootState } from '../../store/store'
import Lang from '../../i18n/LangEnum';
import { getCookie } from '../utils/Utils';

const initialState = {
    lang: getCookie("ecommerce-platform-lang") as Lang || Lang.ENG
}

export const langSlice = createSlice({
    name: 'lang',
    initialState,
    reducers: {
        updateLang: (state, action: PayloadAction<Lang>) => {
            state.lang = action.payload;
        }
    }
})

export const { updateLang } = langSlice.actions

export const getRoles = (state: RootState) => state.lang;

export default langSlice.reducer;