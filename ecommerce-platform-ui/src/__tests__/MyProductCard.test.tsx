import React from "react";
import {  render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import * as ProductRequest from "../app/api/ProductRequest";

import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { Store } from "redux";
import "../app/utils/mockJsdom";
import MyProductCard from "../app/components/pages/products/MyProductCard";

describe("My Product Card Tests", () => {
    const initialState = { jwt: { token: "jwtkey" }, username: { sub: "user" }, roles: { roles: { roles: [{ id: 1, name: "USER" }] } }, lang: { lang: "ENG" } };
    const mockStore = configureStore();
    let store: Store;

    it("Test Product Card displayed", () => {
        store = mockStore(initialState)

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProductCard objectID={1} price="2.5" name="Apple" key={1} categories={[]} description={""} />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const productTitle = screen.getByText("Apple");
            expect(productTitle).toBeInTheDocument();

            const productPrice = screen.getByText("Price: 2.5");
            expect(productPrice).toBeInTheDocument();

            const deleteIcon = screen.getByTestId("delete-icon");
            expect(deleteIcon).toBeInTheDocument();
        })
    });

    it("Test delete Product Card", () => {
        store = mockStore(initialState)

        const deleteProduct = jest.spyOn(ProductRequest, 'deleteProduct');
        deleteProduct.mockReturnValue(Promise.resolve({ "status": 200, "message": "Product successfully deleted!" }));

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <MyProductCard objectID={1} price="2.5" name="Apple" key={1} categories={[]} description={""} />
                </MemoryRouter>
            </Provider>
        );

        waitFor(() => { // NOSONAR: No need to await.
            const deleteButton = screen.getByTestId("delete-button-1");
            deleteButton.click();

            const successAlert = screen.getByAltText("Product successfully deleted!");
            expect(successAlert).toBeInTheDocument();
        })
    });
});