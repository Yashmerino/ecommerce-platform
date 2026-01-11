import React from "react";
import { act, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import * as ProductRequest from "../app/api/ProductRequest";
import EditProductPage from "../app/components/pages/products/EditProductPage"
import { clickSubmitButton } from "../app/utils/TestUtils";
import * as CategoryRequest from "../app/api/CategoryRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";

describe("Update Product Page Tests", () => {
    const initialState = { jwt: { token: "jwtkey" }, username: { sub: "seller" }, info: { info: { roles: [{ id: 2, name: "SELLER" }], email: null } }, lang: { lang: "ENG" }, theme: { theme: "false" } };
    const mockStore = configureStore()

    let store: Store;

    const categoryMock = jest.spyOn(CategoryRequest, 'getCategories');
    categoryMock.mockReturnValue(Promise.resolve([{ "id": 1, "name": "Digital Services" }, { "id": 2, "name": "Cosmetics and Body Care" }]));

    const photoMock = jest.spyOn(ProductRequest, 'getProductPhoto');
    photoMock.mockReturnValue(Promise.resolve(new Blob(["photo"])));

    it("Test update product success", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <EditProductPage />
                </MemoryRouter>
            </Provider>
        );

        const productMock = jest.spyOn(ProductRequest, 'updateProduct');
        productMock.mockReturnValue(Promise.resolve({ status: 200 }));

        const nameInput = screen.getByTestId("name-field");
        expect(nameInput).toBeInTheDocument();

        const priceInput = screen.getByTestId("price-field");
        expect(priceInput).toBeInTheDocument();

        const categoriesInput = screen.getByTestId("categories-field");
        expect(categoriesInput).toBeInTheDocument();

        const photo = screen.getByTestId("photo");
        expect(photo).toBeInTheDocument();

        const header = screen.getByTestId("header");
        expect(header).toBeInTheDocument();

        const footer = screen.getByTestId("footer");
        expect(footer).toBeInTheDocument();

        await act(async () => {
            clickSubmitButton();
        });

        await waitFor(async () => {
            const alertSuccess = screen.getByTestId("alert-success");
            expect(alertSuccess).toBeInTheDocument();
            expect(alertSuccess).toHaveTextContent("product_updated_successfully");
        });
    });

    it("Test update product invalid fields", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <EditProductPage />
                </MemoryRouter>
            </Provider>
        );

        const productMock = jest.spyOn(ProductRequest, 'updateProduct');
        productMock.mockReturnValue(Promise.resolve({ "fieldErrors": [{ "field": "price", "message": "Price should be greater than or equal to 0.01." }, { "field": "name", "message": "Name is required." }] }));

        await act(async () => {
            clickSubmitButton();
        });

        await waitFor(async () => {
            let priceField = document.getElementById("price-field");
            expect(priceField?.getAttribute("aria-invalid")).toBeTruthy();

            let nameField = document.getElementById("name-field");
            expect(nameField?.getAttribute("aria-invalid")).toBeTruthy();
        });
    });

    it("Test update product wrong seller", async () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <EditProductPage />
                </MemoryRouter>
            </Provider>
        );

        const productMock = jest.spyOn(ProductRequest, 'updateProduct');
        productMock.mockReturnValue(Promise.resolve({
            "timestamp": "2023-09-29 07:30:10",
            "status": 409,
            "error": "Access denied!"
        }));

        await act(async () => {
            clickSubmitButton();
        });

        await waitFor(async () => {
            const alertError = screen.getByTestId("alert-error");
            expect(alertError).toBeInTheDocument();
            expect(alertError).toHaveTextContent("Access denied!");
        });
    });
});