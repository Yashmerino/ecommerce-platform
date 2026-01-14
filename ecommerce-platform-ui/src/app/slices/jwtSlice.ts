import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import type { RootState } from '../../store/store'

export interface JwtState {
    token: string
    refreshToken: string
}

const initialState: JwtState = {
    token: "",
    refreshToken: ""
}

export const jwtSlice = createSlice({
    name: 'jwt',
    initialState,
    reducers: {
        updateJwt: (state, action: PayloadAction<string>) => {
            state.token = action.payload;
        },
        updateRefreshToken: (state, action: PayloadAction<string>) => {
            state.refreshToken = action.payload;
        },
        clearTokens: (state) => {
            state.token = "";
            state.refreshToken = "";
        }
    }
})

export const { updateJwt, updateRefreshToken, clearTokens } = jwtSlice.actions

export const selectJwt = (state: RootState) => state.jwt;

export default jwtSlice.reducer;