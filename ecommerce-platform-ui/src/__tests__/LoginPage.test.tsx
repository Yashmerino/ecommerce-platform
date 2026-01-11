import React from "react";
import { fireEvent, render, screen, waitFor, act } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import LoginPage from "../app/components/pages/auth/LoginPage";
import * as AuthRequest from "../app/api/AuthRequest";
import * as UserRequest from "../app/api/UserRequest";
import { clickSubmitButton } from "../app/utils/TestUtils";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";

describe("Login Page Tests", () => {
    const initialState = { token: "", lang: { lang: "ENG" } }
    const mockStore = configureStore()
    let store: Store;

    it("Test login success", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginPage />
                </MemoryRouter>
            </Provider>
        );

        const loginMock = jest.spyOn(AuthRequest, 'login');
        loginMock.mockReturnValue(Promise.resolve({
            "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjk0ODYzNjU3LCJleHAiOjE2OTQ4NjM3Mjd9.k87mXmLpbB__qmZRm3wqEFlg8poJcRUsnGxSoZSUQSAd8ZqRurz9WVpznilWUT9QYaS_rIdprBnAGunOTg6Rpg",
            "tokenType": "Bearer "
        }));

        const getUserInfoMock = jest.spyOn(UserRequest, 'getUserInfo');
        getUserInfoMock.mockReturnValue(Promise.resolve({ "roles": [{ "id": 1, "name": "USER" }] }));

        const title = screen.getByTestId("title");
        expect(title).toBeInTheDocument();
        expect(title).toHaveTextContent("sign_in");

        const usernameInput = screen.getByText("username");
        expect(usernameInput).toBeInTheDocument();

        const passwordInput = screen.getByText("password");
        expect(passwordInput).toBeInTheDocument();

        const registerHref = screen.getByText("create_account_message");
        expect(registerHref).toBeInTheDocument();

        await act(async () => {
            const loginButton = screen.getByTestId("submit-button");
            fireEvent.click(loginButton);
        })
    });

    it("Test login fail", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginPage />
                </MemoryRouter>
            </Provider>
        );

        const loginMock = jest.spyOn(AuthRequest, 'login');
        loginMock.mockReturnValue(Promise.resolve({
            "fieldErrors": [
                {
                    "field": "password",
                    "message": "Password is required."
                },
                {
                    "field": "username",
                    "message": "Username is required."
                },
            ]
        }));

        clickSubmitButton();

        await waitFor(async () => {
            let fieldErrorMessage = document.getElementById("username-helper-text")
            expect(fieldErrorMessage).toBeInTheDocument();

            fieldErrorMessage = document.getElementById("password-helper-text");
            expect(fieldErrorMessage).toBeInTheDocument();
        });
    });

    it("Test login user doesn't exist", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginPage />
                </MemoryRouter>
            </Provider>
        );

        const loginMock = jest.spyOn(AuthRequest, 'login');
        loginMock.mockReturnValue(Promise.resolve({
            "timestamp": "2023-09-16 06:39:30",
            "status": 404,
            "error": "Username doesn't exist!"
        }));

        clickSubmitButton();

        await waitFor(async () => {
            let errorAlert = screen.getByText("Username doesn't exist!");
            expect(errorAlert).toBeInTheDocument();
        });
    });
});