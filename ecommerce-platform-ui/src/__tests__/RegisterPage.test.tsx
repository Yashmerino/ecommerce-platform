import React from "react";
import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import RegisterPage from "../app/components/pages/auth/RegisterPage";
import * as AuthRequest from "../app/api/AuthRequest";
import { clickSubmitButton } from "../app/utils/TestUtils";
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";

describe("Register Page Tests", () => {
    const initialState = { lang: { lang: "ENG" } }
    const mockStore = configureStore()
    let store: Store;

    it("Test register success", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <RegisterPage />
                </MemoryRouter>
            </Provider>
        );

        const loginMock = jest.spyOn(AuthRequest, 'register');
        loginMock.mockReturnValue(Promise.resolve({ status: 200 }));

        const title = screen.getByTestId("title");
        expect(title).toBeInTheDocument();
        expect(title).toHaveTextContent("sign_up");

        const roleSelection = screen.getByText("user");
        expect(roleSelection).toBeInTheDocument();

        const emailInput = screen.getByText("email");
        expect(emailInput).toBeInTheDocument();

        const usernameInput = screen.getByText("username");
        expect(usernameInput).toBeInTheDocument();

        const passwordInput = screen.getByText("password");
        expect(passwordInput).toBeInTheDocument();

        const loginHref = screen.getByText("have_account_message");
        expect(loginHref).toBeInTheDocument();

        clickSubmitButton();

        await waitFor(() => {
            const alertSuccess = screen.getByTestId("alert-success");
            expect(alertSuccess).toBeInTheDocument();
            expect(alertSuccess).toHaveTextContent("user_registered_successfully");
        });
    });

    it("Test register fail", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <RegisterPage />
                </MemoryRouter>
            </Provider>
        );

        const loginMock = jest.spyOn(AuthRequest, 'register');
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
                {
                    "field": "email",
                    "message": "Email is required."
                }
            ]
        }));

        clickSubmitButton();

        await waitFor(async () => {
            let fieldErrorMessage = document.getElementById("username-helper-text")
            expect(fieldErrorMessage).toBeInTheDocument();

            fieldErrorMessage = document.getElementById("password-helper-text");
            expect(fieldErrorMessage).toBeInTheDocument();

            fieldErrorMessage = document.getElementById("email-helper-text");
            expect(fieldErrorMessage).toBeInTheDocument();
        });
    });

    it("Test register username is taken", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <RegisterPage />
                </MemoryRouter>
            </Provider>
        );

        const loginMock = jest.spyOn(AuthRequest, 'register');
        loginMock.mockReturnValue(Promise.resolve({
            "timestamp": "2023-09-29 07:30:10",
            "status": 409,
            "error": "Username is already taken!"
        }));

        clickSubmitButton();

        await waitFor(() => {
            const alertError = screen.getByTestId("alert-error");
            expect(alertError).toBeInTheDocument();
            expect(alertError).toHaveTextContent("Username is already taken!");
        });
    });
});