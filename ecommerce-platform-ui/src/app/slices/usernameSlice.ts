import { createSlice, PayloadAction } from '@reduxjs/toolkit'
import type { RootState } from '../../store/store'

const initialState = {
    sub: ""
}

export const usernameSlice = createSlice({
    name: 'username',
    initialState,
    reducers: {
        updateUsername: (state, action: PayloadAction<string>) => {
            state.sub = action.payload;
        }
    }
})

export const { updateUsername } = usernameSlice.actions

export const getUsername = (state: RootState) => state.username;

export default usernameSlice.reducer;