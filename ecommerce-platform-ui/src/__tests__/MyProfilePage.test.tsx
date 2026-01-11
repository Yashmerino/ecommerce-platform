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

    it("Test My Profile page displayed", () => {
        store = mockStore(initialState)

        const getUserPhoto = jest.spyOn(UserRequest, 'getUserPhoto');
        getUserPhoto.mockReturnValue(Promise.resolve(new Blob([JSON.stringify("photo", null, 2)], {
            type: "application/octet-stream",
        })));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProfilePage />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const title = screen.getByText("ecommerce_platform");
            expect(title).toBeInTheDocument();

            const copyright = screen.getByText("Copyright");
            expect(copyright).toBeInTheDocument();

            const username = screen.getByText("user");
            expect(username).toBeInTheDocument();

            const saveButton = screen.getAllByText("Save");
            expect(saveButton.length).toBe(1);
        })
    });

    it("Test My Profile update user information", () => {
        store = mockStore(initialState)

        const getUserPhoto = jest.spyOn(UserRequest, 'getUserPhoto');
        getUserPhoto.mockReturnValue(Promise.resolve(new Blob([JSON.stringify("photo", null, 2)], {
            type: "application/octet-stream",
        })));

        const updateUser = jest.spyOn(UserRequest, 'updateUser');
        updateUser.mockReturnValue(Promise.resolve({ "status": 200, "message": "User information was successfully updated." }));

        const updatePhoto = jest.spyOn(UserRequest, 'setUserPhoto');
        updatePhoto.mockReturnValue(Promise.resolve({ "status": 200, "message": "User photo was successfully updated." }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProfilePage />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const saveButton = screen.getByText("Save");
            fireEvent.click(saveButton);

            const successAlert = screen.getByText("The user information has been successfully updated!");
            expect(successAlert).toBeInTheDocument();
        })
    });
});