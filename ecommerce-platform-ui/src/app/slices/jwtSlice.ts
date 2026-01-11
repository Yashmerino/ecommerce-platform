import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import type { RootState } from '../../store/store'

export interface JwtState {
    token: string
}

const initialState: JwtState = {
    token: ""
}

export const jwtSlice = createSlice({
    name: 'jwt',
    initialState,
    reducers: {
        updateJwt: (state, action: PayloadAction<string>) => {
            state.token = action.payload;
        }
    }
})

export const { updateJwt } = jwtSlice.actions

export const selectJwt = (state: RootState) => state.jwt;

export default jwtSlice.reducer;