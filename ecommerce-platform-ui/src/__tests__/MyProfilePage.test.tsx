import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import * as UserRequest from "../app/api/UserRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";
import MyProfilePage from "../app/components/pages/user/MyProfilePage";

describe("Products Container Tests", () => {
    const initialState = { jwt: { token: "jwtkey" }, username: { sub: "user" }, info: { info: { roles: [{ id: 2, name: "SELLER" }], email: null }}, lang: { lang: "ENG" }, theme: { theme: "false" } };
    const mockStore = configureStore()
    let store: Store;

    beforeEach(() => {
        jest.clearAllMocks();
    });

    it("Test My Profile page displayed", async () => {
        store = mockStore(initialState)

        const getUserPhoto = jest.spyOn(UserRequest, 'getUserPhoto');
        const photoBlob = new Blob([JSON.stringify("photo", null, 2)], {
            type: "application/octet-stream",
        });
        Object.defineProperty(photoBlob, 'size', { value: 100 });
        getUserPhoto.mockResolvedValue(photoBlob);

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProfilePage />
                </MemoryRouter>
            </Provider>
        );

        await waitFor(() => {
            const saveButton = screen.queryAllByText("Save");
            expect(saveButton.length).toBeGreaterThan(0);
        });

        getUserPhoto.mockRestore();
    });

    it("Test My Profile update user information", async () => {
        store = mockStore(initialState)

        const getUserPhoto = jest.spyOn(UserRequest, 'getUserPhoto');
        const photoBlob = new Blob([JSON.stringify("photo", null, 2)], {
            type: "application/octet-stream",
        });
        Object.defineProperty(photoBlob, 'size', { value: 100 });
        getUserPhoto.mockResolvedValue(photoBlob);

        const updateUser = jest.spyOn(UserRequest, 'updateUser');
        updateUser.mockResolvedValue({ "status": 200, "message": "User information was successfully updated." });

        const setUserPhoto = jest.spyOn(UserRequest, 'setUserPhoto');
        setUserPhoto.mockResolvedValue({ "status": 200, "message": "User photo was successfully updated." });

        const getUserInfo = jest.spyOn(UserRequest, 'getUserInfo');
        getUserInfo.mockResolvedValue({ "status": 200, "roles": [] });

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProfilePage />
                </MemoryRouter>
            </Provider>
        );

        await waitFor(() => {
            const saveButton = screen.queryAllByText("Save");
            expect(saveButton.length).toBeGreaterThan(0);
        });

        getUserPhoto.mockRestore();
        updateUser.mockRestore();
        setUserPhoto.mockRestore();
        getUserInfo.mockRestore();
    });
});
